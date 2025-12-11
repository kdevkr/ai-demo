package com.example.demo.validation;

import com.example.demo.model.GenerateResponse;

public class ResponseValidator {
    
    public static boolean isValidGenerateResponse(GenerateResponse response) {
        if (response == null) {
            return false;
        }
        
        return response.getGeneratedText() != null && 
               !response.getGeneratedText().isEmpty() &&
               response.getModel() != null &&
               !response.getModel().isEmpty() &&
               response.getTokensUsed() != null &&
               response.getTokensUsed() >= 0 &&
               response.getProcessingTimeMs() != null &&
               response.getProcessingTimeMs() >= 0;
    }
    
    public static String validateAndGetErrorMessage(GenerateResponse response) {
        if (response == null) {
            return "응답이 null입니다";
        }
        
        if (response.getGeneratedText() == null || response.getGeneratedText().isEmpty()) {
            return "생성된 텍스트가 누락되었습니다";
        }
        
        if (response.getModel() == null || response.getModel().isEmpty()) {
            return "모델 정보가 누락되었습니다";
        }
        
        if (response.getTokensUsed() == null || response.getTokensUsed() < 0) {
            return "토큰 사용량 정보가 유효하지 않습니다";
        }
        
        if (response.getProcessingTimeMs() == null || response.getProcessingTimeMs() < 0) {
            return "처리 시간 정보가 유효하지 않습니다";
        }
        
        return null;
    }
}
