package com.example.demo.service;

import com.example.demo.exception.ModelNotSupportedException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    
    @PostConstruct
    public void printAvailableModels() {
        log.info("=".repeat(80));
        log.info("등록된 AI 모델 목록:");
        log.info("=".repeat(80));
        
        servicesByProvider.forEach((provider, service) -> {
            List<String> modelIds = service.getAvailableModels().stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            log.info("[{}] {} 개 모델: {}", provider, modelIds.size(), String.join(", ", modelIds));
        });
        
        log.info("=".repeat(80));
        log.info("총 {} 개의 모델이 등록되었습니다.", servicesByModel.size());
        log.info("=".repeat(80));
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
