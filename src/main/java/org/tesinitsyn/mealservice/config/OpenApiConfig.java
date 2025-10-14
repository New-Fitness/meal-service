package org.tesinitsyn.mealservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private final OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                    .title("Meal Service API")
                    .description("API документация для сервиса питания New Fitness")
                    .version("1.0.0"));

    @Bean
    public OpenAPI customOpenAPI() {
        return openAPI;
    }

}

