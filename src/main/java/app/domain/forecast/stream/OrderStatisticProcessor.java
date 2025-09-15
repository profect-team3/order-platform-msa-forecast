package app.domain.forecast.stream;

import app.domain.forecast.document.ForecastDocument;
import app.domain.forecast.model.dto.kafka.OrderCreatedEvent;
import app.domain.forecast.model.stream.StoreHourlyAggregation;
import app.domain.forecast.repository.ForecastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Kafka Streams를 사용하여 주문 데이터를 처리하고 통계를 집계하는 클래스입니다.
 * Spring 컴포넌트로 등록되어 애플리케이션 시작 시 스트림 처리를 자동으로 시작합니다.
 */
@Component
@RequiredArgsConstructor
public class OrderStatisticProcessor {

    // 데이터를 수신할 Kafka 토픽 이름
    private static final String ORDER_CREATED_TOPIC = "dev.order.completed";

    // 집계된 통계 데이터를 저장하기 위한 MongoDB 리포지토리
    private final ForecastRepository forecastRepository;

    /**
     * Spring Kafka가 자동으로 호출하여 Kafka Streams 파이프라인(토폴로지)을 구성하는 메서드입니다.
     * @param streamsBuilder 스트림 토폴로지를 구축하기 위한 빌더 객체
     */
    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        // Kafka 메시지를 Java 객체로 직렬화/역직렬화하기 위한 Serde(Serializer/Deserializer) 설정
        // Java 8의 시간 관련 클래스(LocalDateTime 등)를 처리하기 위해 JavaTimeModule을 등록합니다.
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // OrderCreatedEvent 객체를 위한 JSON Serde
        Serde<OrderCreatedEvent> orderCreatedEventSerde = new JsonSerde<>(OrderCreatedEvent.class, mapper);
        // StoreHourlyAggregation 객체를 위한 JSON Serde
        Serde<StoreHourlyAggregation> aggregationSerde = new JsonSerde<>(StoreHourlyAggregation.class, mapper);

        // 1. 소스(Source): 지정된 토픽으로부터 메시지를 읽어 KStream을 생성합니다.
        KStream<String, OrderCreatedEvent> messageStream = streamsBuilder
                .stream(ORDER_CREATED_TOPIC, Consumed.with(Serdes.String(), orderCreatedEventSerde))
                .peek((key, value) -> {
                    System.out.println("📦 수신된 주문 이벤트:");
                    System.out.println(" - Key: " + key);
                    System.out.println(" - Store ID: " + value.getStoreId());
                    System.out.println(" - Price: " + value.getTotalPrice());
                });

        // 2. 처리(Process) 및 집계(Aggregate)
        messageStream
                // 가게 ID(storeId)를 기준으로 메시지를 그룹화합니다. 집계를 위해 필수적인 단계입니다.
                .groupBy((key, value) -> value.getStoreId().toString(), Grouped.with(Serdes.String(), orderCreatedEventSerde))
                
                // 1시간 단위의 텀블링 윈도우(Tumbling Window)를 적용합니다.
                // 윈도우는 겹치지 않는 고정된 크기의 시간 간격입니다. (예: 1:00-2:00, 2:00-3:00)
                .windowedBy(TimeWindows.of(Duration.ofHours(1)))
                
                // 윈도우 내에서 그룹화된 데이터를 집계합니다.
                .aggregate(
                        // Initializer: 각 윈도우가 처음 시작될 때 집계 객체를 초기화합니다. (주문 수 0, 매출 0.0)
                        () -> new StoreHourlyAggregation(0, 0L),
                        
                        // Aggregator: 새로운 메시지가 들어올 때마다 집계 로직을 수행합니다.
                        (key, event, aggregate) -> {
                            aggregate.setOrderCount(aggregate.getOrderCount() + 1); // 주문 수 1 증가
                            aggregate.setTotalRevenue(aggregate.getTotalRevenue() + event.getTotalPrice().longValue()); // 매출액 더하기
                            return aggregate;
                        },
                        
                        // Materialized: 집계 결과를 상태 저장소(State Store)에 저장하기 위한 설정을 합니다.
                        // 장애 발생 시 상태를 복구하는 데 사용됩니다.
                        Materialized.with(Serdes.String(), aggregationSerde)
                )
                
                // 집계된 KTable을 다시 KStream으로 변환하여 각 윈도우의 최종 결과를 스트림으로 만듭니다.
                .toStream()
                
                // 3. 싱크(Sink): 스트림의 최종 결과를 처리합니다.
                // 여기서는 집계된 통계를 MongoDB에 저장합니다.
                .foreach((windowedKey, aggregation) -> {
                    // 윈도우 키에서 가게 ID와 윈도우 종료 시간을 추출합니다.
                    String storeId = windowedKey.key();
                    Instant windowEndInstant = windowedKey.window().endTime();
                    LocalDateTime windowEnd = LocalDateTime.ofInstant(windowEndInstant, ZoneId.systemDefault());

                    // 해당 가게와 시간대에 대한 통계 데이터가 이미 있는지 확인합니다. (Upsert 로직)
                    forecastRepository.findByStoreIdAndTimestamp(storeId, windowEnd)
                            .ifPresentOrElse(statistic -> {
                                // 데이터가 이미 존재하면, 최신 집계 값으로 업데이트합니다.
                                // (늦게 도착하는 데이터나 재처리 시 동일한 윈도우에 대한 업데이트를 위함)
                                statistic.setRealOrderQuantity(aggregation.getOrderCount());
                                statistic.setRealSalesRevenue(aggregation.getTotalRevenue());
                                forecastRepository.save(statistic);
                            }, () -> {
                                // 데이터가 없으면, 새로운 통계 문서를 생성하여 저장합니다.
                                ForecastDocument newStatistic = ForecastDocument.builder()
                                        .storeId(storeId)
                                        .timestamp(windowEnd)
                                        .realOrderQuantity(aggregation.getOrderCount())
                                        .realSalesRevenue(aggregation.getTotalRevenue())
                                        .build();
                                forecastRepository.save(newStatistic);
                            });
                });
    }
}