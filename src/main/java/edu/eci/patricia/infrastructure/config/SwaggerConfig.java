package edu.eci.patricia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("M06 — Feed y Búsqueda")
                        .description("Servicio de recomendación de parches para PATRICI.A")
                        .version("0.0.1-SNAPSHOT"));
    }
}
