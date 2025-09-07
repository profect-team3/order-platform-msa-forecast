package app.domain.forecast.service;

import app.commonUtil.security.TokenPrincipalParser;
import app.domain.forecast.client.FastApiClient;
import app.domain.forecast.model.dto.request.ForecastRequest;
import app.domain.forecast.model.dto.response.ForecastResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final TokenPrincipalParser tokenPrincipalParser;
    private final FastApiClient fastApiClient;

    public ForecastResponse generateDescription(Authentication authentication) {
        // 사용자 정보 추출
        String userIdStr = tokenPrincipalParser.getUserId(authentication);
        Long userId = Long.parseLong(userIdStr);

        // TODO: 카프카로부터 최신 주문 기록을 받는 로직을 구현해야 함.
        // 어떤 토픽을 구독할지, 메시지 역직렬화 방식, 그리고 메시지 처리 로직에 대한 고려가 필요함.
        List<ForecastRequest.OrderRecord> orderRecords = List.of(); // 임시로 빈 리스트

        // 요청 객체 생성
        ForecastRequest request = new ForecastRequest(userId, LocalDateTime.now(), orderRecords);

        // FastAPI 서버에 요청
        ForecastResponse response = fastApiClient.predict(request);

        // 응답 반환
        return response;
    }
}
