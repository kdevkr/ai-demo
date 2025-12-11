package com.example.demo.service;

import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;

import java.util.List;

public interface AIService {
    
    GenerateResponse generate(GenerateRequest request);
    
    List<ModelInfo> getAvailableModels();
    
    boolean isModelSupported(String modelId);
    
    boolean isHealthy();
    
    String getProviderName();
}
