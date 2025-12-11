package com.example.demo.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.example.demo.config.properties.AIClaudeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AIClaudeProperties.class)
public class AnthropicConfig {

    @Bean
    public AnthropicClient anthropicClient(AIClaudeProperties properties) {
        return AnthropicOkHttpClient.builder()
                .apiKey(properties.getApiKey())
                .baseUrl(properties.getBaseUrl())
                .timeout(Duration.ofMillis(properties.getTimeout()))
                .maxRetries(properties.getMaxRetries())
                .build();
    }
}
