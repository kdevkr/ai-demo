package com.example.demo.config;

import com.example.demo.config.properties.AIModelProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AIModelProperties.class)
public class AIModelConfig {
}
