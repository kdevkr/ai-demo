package com.example.demo.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "sentry")
public class SentryProperties {
    
    private final String dsn;
    private final String environment;
    private final Double tracesSampleRate;
    private final Boolean enabled;
    
    @ConstructorBinding
    public SentryProperties(
            String dsn,
            String environment,
            Double tracesSampleRate,
            Boolean enabled
    ) {
        this.dsn = dsn;
        this.environment = environment;
        this.tracesSampleRate = tracesSampleRate;
        this.enabled = enabled;
    }
}
