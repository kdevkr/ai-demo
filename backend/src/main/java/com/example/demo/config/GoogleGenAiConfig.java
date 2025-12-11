package com.example.demo.config;

import com.example.demo.config.properties.AIGoogleProperties;
import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AIGoogleProperties.class)
public class GoogleGenAiConfig {

    private final AIGoogleProperties properties;

    public GoogleGenAiConfig(AIGoogleProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "close")
    public Client googleGenAiClient() {
        HttpOptions httpOptions = HttpOptions.builder()
                .baseUrl(properties.getBaseUrl())
                .timeout(properties.getTimeout())
                .retryOptions(HttpRetryOptions.builder()
                        .attempts(properties.getAttempts())
                        .build())
                .build();

        ClientOptions options = ClientOptions.builder()
                .maxConnections(properties.getMaxConnections())
                .build();

        return Client.builder()
                .apiKey(properties.getApiKey())
                .httpOptions(httpOptions)
                .clientOptions(options)
                .build();
    }
}
