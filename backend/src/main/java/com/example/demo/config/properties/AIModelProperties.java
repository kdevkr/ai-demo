package com.example.demo.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "ai.model")
public class AIModelProperties {
    
    private final Validation validation;
    private final Logging logging;
    
    @ConstructorBinding
    public AIModelProperties(Validation validation, Logging logging) {
        this.validation = validation;
        this.logging = logging;
    }
    
    @Getter
    public static class Validation {
        private final boolean enabled;
        private final boolean verbose;
        
        @ConstructorBinding
        public Validation(boolean enabled, boolean verbose) {
            this.enabled = enabled;
            this.verbose = verbose;
        }
    }
    
    @Getter
    public static class Logging {
        private final boolean enabled;
        
        @ConstructorBinding
        public Logging(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
