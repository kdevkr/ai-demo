package com.example.demo.service;

import com.example.demo.model.ModelInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelValidationService {
    
    private final List<AIService> aiServices;
    
    @PostConstruct
    public void validateModels() {
        log.info("=".repeat(80));
        log.info("AI 모델 검증 시작");
        log.info("=".repeat(80));
        
        List<CompletableFuture<Void>> validationTasks = aiServices.stream()
                .map(service -> CompletableFuture.runAsync(() -> validateService(service)))
                .collect(Collectors.toList());
        
        CompletableFuture.allOf(validationTasks.toArray(new CompletableFuture[0])).join();
        
        log.info("=".repeat(80));
        log.info("AI 모델 검증 완료");
        log.info("=".repeat(80));
    }
    
    private void validateService(AIService service) {
        String providerName = service.getProviderName();
        
        try {
            boolean isHealthy = service.isHealthy();
            
            if (isHealthy) {
                log.info("[{}] 연결 성공 - API 정상 작동", providerName);
                validateModels(service);
            } else {
                log.warn("[{}] 연결 실패 - API 키 또는 네트워크 확인 필요", providerName);
                markModelsUnavailable(service);
            }
        } catch (Exception e) {
            log.error("[{}] 검증 중 오류 발생: {}", providerName, e.getMessage());
            markModelsUnavailable(service);
        }
    }
    
    private void validateModels(AIService service) {
        List<String> actualModelIds = service.getActualModelIds();
        List<ModelInfo> definedModels = service.getAvailableModels();
        
        if (actualModelIds.isEmpty()) {
            log.warn("[{}] API에서 모델 목록을 가져올 수 없습니다", service.getProviderName());
            return;
        }
        
        log.info("[{}] API에서 {} 개의 모델을 발견했습니다", 
                service.getProviderName(), 
                actualModelIds.size());
        
        for (ModelInfo model : definedModels) {
            boolean exists = actualModelIds.stream()
                    .anyMatch(id -> id.contains(model.getId()) || model.getId().contains(id));
            
            if (exists) {
                log.info("[{}] ✓ 모델 확인됨: {}", service.getProviderName(), model.getId());
            } else {
                log.warn("[{}] ✗ 모델 미확인: {} - API에 존재하지 않을 수 있습니다", 
                        service.getProviderName(), 
                        model.getId());
            }
        }
    }
    
    private void markModelsUnavailable(AIService service) {
        List<ModelInfo> models = service.getAvailableModels();
        log.warn("[{}] {} 개의 모델을 사용할 수 없습니다", 
                service.getProviderName(), 
                models.size());
    }
}
