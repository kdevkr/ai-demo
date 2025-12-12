package com.example.demo.controller;

import com.example.demo.model.GenerateRequest;
import com.example.demo.model.GenerateResponse;
import com.example.demo.model.ModelInfo;
import com.example.demo.service.AIService;
import com.example.demo.service.AIServiceFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final AIServiceFactory aiServiceFactory;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@Valid @RequestBody GenerateRequest request) {
        log.info("텍스트 생성 요청: model={}, prompt length={}", request.getModel(), request.getPrompt().length());
        
        AIService service = aiServiceFactory.getServiceByModel(request.getModel());
        GenerateResponse response = service.generate(request);
        
        log.info("텍스트 생성 완료: model={}, tokens={}, time={}ms", 
                response.getModel(), response.getTokensUsed(), response.getProcessingTimeMs());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/models")
    public ResponseEntity<?> getAllModels() {
        log.info("모든 모델 목록 조회");
        
        List<ModelInfo> models = aiServiceFactory.getAllServices().stream()
                .flatMap(service -> service.getAvailableModels().stream())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(models);
    }
    
    @GetMapping("/models/{provider}")
    public ResponseEntity<?> getModelsByProvider(@PathVariable String provider) {
        log.info("프로바이더별 모델 목록 조회: provider={}", provider);
        
        AIService service = aiServiceFactory.getServiceByProvider(provider);
        List<ModelInfo> models = service.getAvailableModels();
        
        return ResponseEntity.ok(models);
    }
    
    @PostMapping(value = "/generate/stream", produces = "text/event-stream")
    public Flux<String> generateStream(@Valid @RequestBody GenerateRequest request) {
        log.info("스트리밍 텍스트 생성 요청: model={}, prompt length={}", request.getModel(), request.getPrompt().length());
        
        AIService service = aiServiceFactory.getServiceByModel(request.getModel());
        return service.generateStream(request)
                .doOnComplete(() -> log.info("스트리밍 완료: model={}", request.getModel()))
                .doOnError(error -> log.error("스트리밍 오류: model={}", request.getModel(), error));
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {
        log.info("AI 서비스 헬스체크");
        
        Map<String, Boolean> healthStatus = aiServiceFactory.getAllServices().stream()
                .collect(Collectors.toMap(
                        AIService::getProviderName,
                        AIService::isHealthy
                ));
        
        return ResponseEntity.ok(healthStatus);
    }
}
