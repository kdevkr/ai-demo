package com.example.demo.config;

import com.example.demo.config.properties.AIOpenAIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AIOpenAIProperties.class)
public class OpenAIConfig {
}
