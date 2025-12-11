package com.example.demo.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "ai.claude")
public class AIClaudeProperties {
    
    private final String apiKey;
    private final String baseUrl;
    private final int timeout;
    private final int maxRetries;
    
    @ConstructorBinding
    public AIClaudeProperties(
            String apiKey,
            String baseUrl,
            int timeout,
            int maxRetries) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
    }
}
