package com.example.demo.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "ai.google")
public class AIGoogleProperties {
    private final String apiKey;
    private final String baseUrl = "https://generativelanguage.googleapis.com";
    private final Integer timeout = 30000;
    private final Integer attempts = 3;
    private final Integer maxConnections = 64;
}
