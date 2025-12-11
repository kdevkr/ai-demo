package com.example.demo.service.impl;

import com.example.demo.exception.AIServiceException;
import com.example.demo.exception.ModelNotSupportedException;
import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import com.example.demo.service.AIService;
import com.google.genai.Client;
import com.google.genai.Pager;
import com.google.genai.types.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Google Gemini AI Service Implementation
 * 
 * Model Reference: https://ai.google.dev/gemini-api/docs/models
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAIService implements AIService {

    private static final String PROVIDER_NAME = "Google";
    private static final List<ModelInfo> AVAILABLE_MODELS = List.of(
            ModelInfo.builder()
                    .id("gemini-3-pro-preview")
                    .name("Gemini 3 Pro Preview")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("gemini-2.5-flash")
                    .name("Gemini 2.5 Flash")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build(),
            ModelInfo.builder()
                    .id("gemini-2.5-flash-lite")
                    .name("Gemini 2.5 Flash Lite")
                    .provider(PROVIDER_NAME)
                    .available(true)
                    .build()
    );

    private final Client client;

    @Override
    public GenerateResponse generate(GenerateRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            String modelId = request.getModel() != null ? request.getModel() : "gemini-2.5-flash-lite";

            if (!isModelSupported(modelId)) {
                throw new ModelNotSupportedException(modelId, PROVIDER_NAME);
            }

            GenerateContentConfig.Builder configBuilder = GenerateContentConfig.builder();
            
            if (request.getMaxTokens() != null) {
                configBuilder.maxOutputTokens(request.getMaxTokens());
            }
            
            if (request.getTemperature() != null) {
                configBuilder.temperature(request.getTemperature().floatValue());
            }

            Content content = Content.builder()
                    .role("user")
                    .parts(List.of(Part.builder().text(request.getPrompt()).build()))
                    .build();

            GenerateContentResponse response = client.models
                    .generateContent(request.getModel(), List.of(content), configBuilder.build());

            long processingTime = System.currentTimeMillis() - startTime;

            if (response == null || response.candidates().isEmpty()) {
                throw new AIServiceException("Google Gemini API 응답이 없습니다", PROVIDER_NAME);
            }

            String generatedText = response.text();
            Integer tokensUsed = response.usageMetadata()
                    .flatMap(GenerateContentResponseUsageMetadata::totalTokenCount)
                    .orElse(0);

            return GenerateResponse.builder()
                    .generatedText(generatedText)
                    .model(modelId)
                    .tokensUsed(tokensUsed)
                    .processingTimeMs(processingTime)
                    .build();

        } catch (ModelNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google Gemini API 호출 실패", e);
            throw new AIServiceException("Google Gemini API 호출 중 오류가 발생했습니다: " + e.getMessage(), e, PROVIDER_NAME);
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
            Pager<Model> modelsPager = client.models.list(ListModelsConfig.builder().build());
            // Pager가 null이 아니면 API 연결이 정상
            return modelsPager != null;
        } catch (Exception e) {
            log.warn("Google Gemini 헬스체크 실패", e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
