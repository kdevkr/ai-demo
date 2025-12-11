package com.example.demo.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequest {
    
    @NotBlank(message = "프롬프트는 필수입니다")
    private String prompt;
    
    private String model;
    
    @Min(value = 1, message = "최대 토큰은 1 이상이어야 합니다")
    @Max(value = 4096, message = "최대 토큰은 4096 이하여야 합니다")
    private Integer maxTokens;
    
    @Min(value = 0, message = "온도는 0 이상이어야 합니다")
    @Max(value = 2, message = "온도는 2 이하여야 합니다")
    private Double temperature;
}
