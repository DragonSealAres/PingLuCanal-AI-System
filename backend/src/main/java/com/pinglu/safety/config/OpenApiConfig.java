package com.pinglu.safety.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pingluSafetyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("平陆运河AI水上安全巡检系统")
                        .description("Backend APIs for PingLu Canal AI water safety inspection system")
                        .version("v1.0.0"));
    }
}
