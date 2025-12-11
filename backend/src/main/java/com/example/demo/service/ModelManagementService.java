package com.example.demo.service;

import com.example.demo.exception.ModelNotSupportedException;
import com.example.demo.model.ModelInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModelManagementService {
    
    private final AIServiceFactory serviceFactory;
    
    public ModelManagementService(AIServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }
    
    public List<ModelInfo> getAllAvailableModels() {
        return serviceFactory.getAllServices().stream()
                .flatMap(service -> service.getAvailableModels().stream())
                .collect(Collectors.toList());
    }
    
    public List<ModelInfo> getModelsByProvider(String provider) {
        try {
            AIService service = serviceFactory.getServiceByProvider(provider);
            return service.getAvailableModels();
        } catch (ModelNotSupportedException e) {
            log.warn("지원하지 않는 프로바이더: {}", provider);
            return List.of();
        }
    }
    
    public ModelInfo getModelInfo(String modelId) {
        return getAllAvailableModels().stream()
                .filter(model -> model.getId().equals(modelId))
                .findFirst()
                .orElseThrow(() -> new ModelNotSupportedException(modelId, "Unknown"));
    }
    
    public boolean isModelSupported(String modelId) {
        try {
            AIService service = serviceFactory.getServiceByModel(modelId);
            return service.isModelSupported(modelId);
        } catch (ModelNotSupportedException e) {
            return false;
        }
    }
    
    public AIService getServiceForModel(String modelId) {
        return serviceFactory.getServiceByModel(modelId);
    }
}
