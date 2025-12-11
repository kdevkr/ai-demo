package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {
    
    private Double memoryUsagePercent;
    private Double cpuUsagePercent;
    private Long uptimeSeconds;
}
