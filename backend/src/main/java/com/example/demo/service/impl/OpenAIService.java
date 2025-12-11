package com.example.demo.service.impl;

import com.example.demo.exception.AIServiceException;
import com.example.demo.exception.ModelNotSupportedException;
import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import com.example.demo.service.AIService;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;
import com.openai.models.models.ModelListPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OpenAI Service Implementation
 * 
 * Model Reference: https://platform.openai.com/docs/models
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService implements AIService {
    
    private static final String PROVIDER_NAME = "OpenAI";
    private static final List<ModelInfo> AVAILABLE_MODELS = List.of(
            ModelInfo.builder()
                    .id("gpt-5.1")
                    .name("GPT-5.1")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("gpt-5-mini")
                    .name("GPT-5 Mini")
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
    
    private final OpenAIClient client;
    

    
    @Override
    public GenerateResponse generate(GenerateRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String modelId = request.getModel() != null ? request.getModel() : "gpt-3.5-turbo";
            
            if (!isModelSupported(modelId)) {
                throw new ModelNotSupportedException(modelId, PROVIDER_NAME);
            }
            
            ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                    .model(modelId)
                    .addSystemMessage(getSystemInstruction())
                    .addMessage(ChatCompletionUserMessageParam.builder()
                            .content(ChatCompletionUserMessageParam.Content.ofText(request.getPrompt()))
                            .build());
            
            if (request.getMaxTokens() != null) {
                paramsBuilder.maxCompletionTokens(request.getMaxTokens().longValue());
            }
            
            if (request.getTemperature() != null) {
                paramsBuilder.temperature(request.getTemperature());
            }
            
            ChatCompletion completion = client.chat().completions().create(paramsBuilder.build());
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            String generatedText = completion.choices().get(0).message().content().get();
            Integer tokensUsed = (int) completion.usage().get().totalTokens();
            
            return GenerateResponse.builder()
                    .generatedText(generatedText)
                    .model(modelId)
                    .tokensUsed(tokensUsed)
                    .processingTimeMs(processingTime)
                    .build();
                    
        } catch (ModelNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new AIServiceException("OpenAI API 호출 중 오류가 발생했습니다: " + e.getMessage(), e, PROVIDER_NAME);
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
            ModelListPage models = client.models().list();
            return models != null && !models.data().isEmpty();
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
