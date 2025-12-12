package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateResponse {
    
    private String generatedText;
    private String model;
    private Integer tokensUsed;
    private Long processingTimeMs;
    private Double costUsd;
}
