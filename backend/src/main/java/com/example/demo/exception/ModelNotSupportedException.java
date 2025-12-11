package com.example.demo.exception;

public class ModelNotSupportedException extends AIServiceException {
    
    private final String requestedModel;
    
    public ModelNotSupportedException(String requestedModel, String provider) {
        super(String.format("모델 '%s'은(는) %s에서 지원하지 않습니다", requestedModel, provider), 
              provider, 
              "MODEL_NOT_SUPPORTED");
        this.requestedModel = requestedModel;
    }
    
    public String getRequestedModel() {
        return requestedModel;
    }
}
