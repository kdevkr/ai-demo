package com.example.demo.exception;

public class AIServiceException extends RuntimeException {
    
    private final String provider;
    private final String errorCode;
    
    public AIServiceException(String message, String provider) {
        super(message);
        this.provider = provider;
        this.errorCode = "UNKNOWN";
    }
    
    public AIServiceException(String message, String provider, String errorCode) {
        super(message);
        this.provider = provider;
        this.errorCode = errorCode;
    }
    
    public AIServiceException(String message, Throwable cause, String provider) {
        super(message, cause);
        this.provider = provider;
        this.errorCode = "UNKNOWN";
    }
    
    public AIServiceException(String message, Throwable cause, String provider, String errorCode) {
        super(message, cause);
        this.provider = provider;
        this.errorCode = errorCode;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
