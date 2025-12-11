package com.example.demo.exception;

public class AIServiceUnavailableException extends AIServiceException {
    
    public AIServiceUnavailableException(String provider, Throwable cause) {
        super(String.format("%s 서비스를 사용할 수 없습니다", provider), 
              cause, 
              provider, 
              "SERVICE_UNAVAILABLE");
    }
    
    public AIServiceUnavailableException(String message, String provider) {
        super(message, provider, "SERVICE_UNAVAILABLE");
    }
}
