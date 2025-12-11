package com.example.demo.config;

import com.example.demo.config.properties.AIGoogleProperties;
import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AIGoogleProperties.class)
public class GoogleGenAiConfig {

    @Bean(destroyMethod = "close")
    public Client googleGenAiClient(AIGoogleProperties properties) {
        HttpOptions httpOptions = HttpOptions.builder()
                .baseUrl(properties.getBaseUrl())
                .timeout(properties.getTimeout())
                .retryOptions(HttpRetryOptions.builder()
                        .attempts(properties.getMaxRetries())
                        .build())
                .build();

        return Client.builder()
                .apiKey(properties.getApiKey())
                .httpOptions(httpOptions)
                .build();
    }
}
