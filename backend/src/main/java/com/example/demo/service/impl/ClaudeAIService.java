package com.example.demo.service.impl;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageParam;
import com.anthropic.models.messages.Model;
import com.example.demo.exception.AIServiceException;
import com.example.demo.exception.ModelNotSupportedException;
import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import com.example.demo.service.AIService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Anthropic Claude AI Service Implementation
 * 
 * Model Reference: https://platform.claude.com/docs/en/about-claude/models/overview
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeAIService implements AIService {
    
    private static final String PROVIDER_NAME = "Anthropic";
    private static final List<ModelInfo> AVAILABLE_MODELS = List.of(
            ModelInfo.builder()
                    .id("claude-sonnet-4-5-20250929")
                    .name("Claude 4.5 Sonnet")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("claude-haiku-4-5-20251001")
                    .name("Claude 4.5 Haiku")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("claude-3-5-haiku-20241022")
                    .name("Claude Haiku 3.5")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("claude-3-haiku-20240307")
                    .name("Claude Haiku 3")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build()
    );
    
    private final AnthropicClient client;
    
    @Override
    public GenerateResponse generate(GenerateRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String modelId = request.getModel() != null ? request.getModel() : "claude-4.5-sonnet-20250514";
            
            if (!isModelSupported(modelId)) {
                throw new ModelNotSupportedException(modelId, PROVIDER_NAME);
            }
            
            MessageCreateParams.Builder paramsBuilder = MessageCreateParams.builder()
                    .model(Model.of(modelId))
                    .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens().longValue() : 1000L)
                    .addMessage(MessageParam.builder()
                            .role(MessageParam.Role.USER)
                            .content(MessageParam.Content.ofString(request.getPrompt()))
                            .build());
            
            if (request.getTemperature() != null) {
                paramsBuilder.temperature(request.getTemperature());
            }
            
            Message response = client.messages().create(paramsBuilder.build());
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            if (response == null || response.content().isEmpty()) {
                throw new AIServiceException("Claude API 응답이 없습니다", PROVIDER_NAME);
            }
            
            String generatedText = response.content().get(0).asText().text();
            Integer tokensUsed = (int) (response.usage().inputTokens() + response.usage().outputTokens());
            
            return GenerateResponse.builder()
                    .generatedText(generatedText)
                    .model(modelId)
                    .tokensUsed(tokensUsed)
                    .processingTimeMs(processingTime)
                    .build();
                    
        } catch (ModelNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude API 호출 실패", e);
            throw new AIServiceException("Claude API 호출 중 오류가 발생했습니다: " + e.getMessage(), e, PROVIDER_NAME);
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
            Message response = client.messages().create(
                    MessageCreateParams.builder()
                            .model(Model.of("claude-4.5-haiku-20250514"))
                            .maxTokens(5L)
                            .addMessage(MessageParam.builder()
                                    .role(MessageParam.Role.USER)
                                    .content(MessageParam.Content.ofString("test"))
                                    .build())
                            .build()
            );
            
            return response != null && !response.content().isEmpty();
        } catch (Exception e) {
            log.warn("Claude 헬스체크 실패", e);
            return false;
        }
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
