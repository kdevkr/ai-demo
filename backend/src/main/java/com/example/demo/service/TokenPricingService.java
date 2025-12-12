package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenPricingService {
    
    private static final Map<String, TokenPrice> MODEL_PRICES = new HashMap<>();
    
    static {
        MODEL_PRICES.put("gpt-5.2", new TokenPrice(0.00000175, 0.000014));
        MODEL_PRICES.put("gpt-5.2-pro", new TokenPrice(0.000021, 0.000168));
        MODEL_PRICES.put("gpt-5.1", new TokenPrice(0.00000125, 0.00001));
        MODEL_PRICES.put("gpt-5.1-pro", new TokenPrice(0.000015, 0.000075));
        MODEL_PRICES.put("gpt-5-mini", new TokenPrice(0.000001, 0.000005));
        MODEL_PRICES.put("gpt-3.5-turbo", new TokenPrice(0.0000005, 0.0000015));
        
        MODEL_PRICES.put("claude-sonnet-4-5-20250929", new TokenPrice(0.000003, 0.000015));
        MODEL_PRICES.put("claude-haiku-4-5-20251001", new TokenPrice(0.000001, 0.000005));
        MODEL_PRICES.put("claude-3-5-haiku-20241022", new TokenPrice(0.0000008, 0.000004));
        MODEL_PRICES.put("claude-3-haiku-20240307", new TokenPrice(0.00000025, 0.00000125));
        
        MODEL_PRICES.put("gemini-3-pro-preview", new TokenPrice(0.000002, 0.000012));
        MODEL_PRICES.put("gemini-2.5-flash", new TokenPrice(0.0000003, 0.0000025));
        MODEL_PRICES.put("gemini-2.5-flash-lite", new TokenPrice(0.0000001, 0.0000004));
    }
    
    public double calculateCost(String modelId, int inputTokens, int outputTokens) {
        TokenPrice price = MODEL_PRICES.get(modelId);
        if (price == null) {
            return 0.0;
        }
        
        return (inputTokens * price.inputPricePerToken) + (outputTokens * price.outputPricePerToken);
    }
    
    public double calculateCostFromTotal(String modelId, int totalTokens) {
        TokenPrice price = MODEL_PRICES.get(modelId);
        if (price == null) {
            return 0.0;
        }
        
        double avgPrice = (price.inputPricePerToken + price.outputPricePerToken) / 2;
        return totalTokens * avgPrice;
    }
    
    private static class TokenPrice {
        final double inputPricePerToken;
        final double outputPricePerToken;
        
        TokenPrice(double inputPricePerToken, double outputPricePerToken) {
            this.inputPricePerToken = inputPricePerToken;
            this.outputPricePerToken = outputPricePerToken;
        }
    }
}
