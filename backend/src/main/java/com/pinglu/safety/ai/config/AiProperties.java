package com.pinglu.safety.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private boolean mockEnabled = true;

    private String apiKey;

    private String baseUrl = "https://api.openai.com/v1";

    private String model = "gpt-4o-mini";

    private int timeoutSeconds = 30;
}
