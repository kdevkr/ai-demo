package com.example.demo.service;

import com.example.demo.exception.ModelNotSupportedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AIServiceFactory {
    
    private final Map<String, AIService> servicesByProvider;
    private final Map<String, AIService> servicesByModel;
    
    public AIServiceFactory(List<AIService> aiServices) {
        this.servicesByProvider = aiServices.stream()
                .collect(Collectors.toMap(
                        AIService::getProviderName,
                        service -> service
                ));
        
        this.servicesByModel = aiServices.stream()
                .flatMap(service -> service.getAvailableModels().stream()
                        .map(model -> Map.entry(model.getId(), service)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
    
    public AIService getServiceByProvider(String provider) {
        return Optional.ofNullable(servicesByProvider.get(provider))
                .orElseThrow(() -> new ModelNotSupportedException(provider, "Unknown"));
    }
    
    public AIService getServiceByModel(String modelId) {
        return Optional.ofNullable(servicesByModel.get(modelId))
                .orElseThrow(() -> new ModelNotSupportedException(modelId, "Unknown"));
    }
    
    public List<AIService> getAllServices() {
        return List.copyOf(servicesByProvider.values());
    }
}
