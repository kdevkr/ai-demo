package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class LokiConnectionLogger {

    private final Environment environment;

    @Value("${logging.loki.url:http://localhost:3100/loki/api/v1/push}")
    private String lokiUrl;

    @Value("${logging.loki.enabled:false}")
    private boolean lokiEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void checkLokiConnection() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevOrProd = Arrays.asList(activeProfiles).stream()
                .anyMatch(profile -> profile.equals("dev") || profile.equals("prod"));

        if (!isDevOrProd) {
            log.info("Loki 로깅 비활성화 (프로파일: {})", Arrays.toString(activeProfiles));
            return;
        }

        if (!lokiEnabled) {
            log.info("Loki 로깅 설정 비활성화 (logging.loki.enabled=false)");
            return;
        }

        log.info("Loki 연결 확인 중: {}", lokiUrl);
        
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
                log.info("✓ Loki 연결 성공 - 로그가 Loki로 전송됩니다");
            } else {
                log.warn("✗ Loki 응답 비정상 (코드: {})", responseCode);
            }
        } catch (Exception e) {
            log.warn("✗ Loki 연결 실패: {}", e.getMessage());
        }
    }
}
