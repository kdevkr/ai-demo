package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "logging.loki.enabled", havingValue = "true", matchIfMissing = false)
public class LokiHealthIndicator implements HealthIndicator {

    @Value("${logging.loki.url:http://localhost:3100/loki/api/v1/push}")
    private String lokiUrl;

    @Override
    public Health health() {
        try {
            String healthUrl = lokiUrl.replace("/loki/api/v1/push", "/ready");
            URL url = new URL(healthUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            if (responseCode == 200) {
                log.info("Loki 연결 성공: {}", healthUrl);
                return Health.up()
                        .withDetail("url", lokiUrl)
                        .withDetail("status", "connected")
                        .build();
            } else {
                log.warn("Loki 응답 코드 비정상: {}", responseCode);
                return Health.down()
                        .withDetail("url", lokiUrl)
                        .withDetail("status", "unhealthy")
                        .withDetail("responseCode", responseCode)
                        .build();
            }
        } catch (Exception e) {
            log.warn("Loki 연결 실패: {} - {}", lokiUrl, e.getMessage());
            return Health.down()
                    .withDetail("url", lokiUrl)
                    .withDetail("status", "disconnected")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
