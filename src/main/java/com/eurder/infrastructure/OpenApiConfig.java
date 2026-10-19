package com.eurder.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eurderOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eurder API")
                        .description("Back-end of the Eurder order management application")
                        .version("v1"));
    }
}