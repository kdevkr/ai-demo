package com.example.demo.config;

import com.example.demo.config.properties.SentryProperties;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SentryProperties.class)
public class SentryConfig {
    
    private final SentryProperties properties;
    
    @PostConstruct
    public void init() {
        if (Boolean.TRUE.equals(properties.getEnabled())) {
            Sentry.init(options -> {
                options.setDsn(properties.getDsn());
                options.setEnvironment(properties.getEnvironment());
                options.setTracesSampleRate(properties.getTracesSampleRate());
                options.setEnableTracing(true);
                options.setDebug(false);
                
                options.setBeforeSend((event, hint) -> {
                    log.debug("Sentry 이벤트 전송: {}", event.getEventId());
                    return event;
                });
            });
            
            log.info("Sentry 초기화 완료 - Environment: {}, DSN: {}", 
                    properties.getEnvironment(), 
                    maskDsn(properties.getDsn()));
        } else {
            log.info("Sentry가 비활성화되어 있습니다");
        }
    }
    
    private String maskDsn(String dsn) {
        if (dsn == null || dsn.length() < 20) {
            return "***";
        }
        return dsn.substring(0, 10) + "..." + dsn.substring(dsn.length() - 10);
    }
}
