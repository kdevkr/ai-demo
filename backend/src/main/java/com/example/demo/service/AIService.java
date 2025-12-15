package com.example.demo.service;

import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AIService {
    
    String DEFAULT_SYSTEM_INSTRUCTION = "당신은 한국어로 답변하는 AI 어시스턴트입니다. 모든 응답은 한국어로 작성해주세요.";
    int DEFAULT_MAX_TOKENS = 1000;
    double DEFAULT_TEMPERATURE = 0.7;
    
    GenerateResponse generate(GenerateRequest request);
    
    Flux<String> generateStream(GenerateRequest request);
    
    List<ModelInfo> getAvailableModels();
    
    boolean isModelSupported(String modelId);
    
    boolean isHealthy();
    
    String getProviderName();
    
    default List<String> getActualModelIds() {
        return List.of();
    }
    
    default String getSystemInstruction() {
        return DEFAULT_SYSTEM_INSTRUCTION;
    }
    
    default int getDefaultMaxTokens() {
        return DEFAULT_MAX_TOKENS;
    }
    
    default double getDefaultTemperature() {
        return DEFAULT_TEMPERATURE;
    }
}
