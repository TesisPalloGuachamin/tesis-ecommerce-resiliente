package com.tesis.ecommerce.coreapi.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Core API - E-commerce")
                .version("1.0.0")
                .description("API del servicio core para e-commerce móvil académico")
                .contact(new Contact()
                    .name("Tesis E-commerce")
                    .url("https://github.com")));
    }
}

