package com.example.demo.service.impl;

import com.example.demo.config.properties.AIOpenAIProperties;
import com.example.demo.exception.AIServiceException;
import com.example.demo.exception.ModelNotSupportedException;
import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import com.example.demo.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAIService implements AIService {
    
    private static final String PROVIDER_NAME = "OpenAI";
    private static final List<ModelInfo> AVAILABLE_MODELS = List.of(
            ModelInfo.builder()
                    .id("gpt-4o")
                    .name("GPT-4o")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("gpt-4o-mini")
                    .name("GPT-4o Mini")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("gpt-3.5-turbo")
                    .name("GPT-3.5 Turbo")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build()
    );
    
    private final WebClient webClient;
    private final AIOpenAIProperties properties;
    
    public OpenAIService(AIOpenAIProperties properties, WebClient.Builder webClientBuilder) {
        this.properties = properties;
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public GenerateResponse generate(GenerateRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String modelId = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
            
            if (!isModelSupported(modelId)) {
                throw new ModelNotSupportedException(modelId, PROVIDER_NAME);
            }
            
            Map<String, Object> requestBody = Map.of(
                    "model", modelId,
                    "messages", List.of(
                            Map.of("role", "user", "content", request.getPrompt())
                    ),
                    "max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : 1000,
                    "temperature", request.getTemperature() != null ? request.getTemperature() : 0.7
            );
            
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.fixedDelay(properties.getMaxRetries(), Duration.ofSeconds(1))
                            .filter(throwable -> !(throwable instanceof WebClientResponseException.Unauthorized)))
                    .timeout(Duration.ofMillis(properties.getTimeout()))
                    .block();
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            if (response == null) {
                throw new AIServiceException("OpenAI API 응답이 없습니다", PROVIDER_NAME);
            }
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String generatedText = (String) message.get("content");
            
            Map<String, Object> usage = (Map<String, Object>) response.get("usage");
            Integer tokensUsed = (Integer) usage.get("total_tokens");
            
            return GenerateResponse.builder()
                    .generatedText(generatedText)
                    .model(modelId)
                    .tokensUsed(tokensUsed)
                    .processingTimeMs(processingTime)
                    .build();
                    
        } catch (ModelNotSupportedException e) {
            throw e;
        } catch (WebClientResponseException e) {
            log.error("OpenAI API 호출 실패: {}", e.getResponseBodyAsString(), e);
            throw new AIServiceException("OpenAI API 호출 중 오류가 발생했습니다: " + e.getMessage(), e, PROVIDER_NAME);
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new AIServiceException("OpenAI API 호출 중 오류가 발생했습니다", e, PROVIDER_NAME);
        }
    }
    
    @Override
    public List<ModelInfo> getAvailableModels() {
        return AVAILABLE_MODELS;
    }
    
    @Override
    public boolean isModelSupported(String modelId) {
        return AVAILABLE_MODELS.stream()
                .anyMatch(model -> model.getId().equals(modelId));
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // 모델 목록 조회는 토큰을 소비하지 않음
            Map<String, Object> response = webClient.get()
                    .uri("/models")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return response != null && response.containsKey("data");
        } catch (Exception e) {
            log.warn("OpenAI 헬스체크 실패", e);
            return false;
        }
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
