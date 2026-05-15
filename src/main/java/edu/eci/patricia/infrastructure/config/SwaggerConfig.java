package edu.eci.patricia.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for the M06 Feed &amp; Search microservice.
 *
 * <p>Registers the API metadata (title, description, version) and adds a global Bearer JWT
 * security scheme so that all endpoints can be tested with a JWT token directly from the
 * Swagger UI.</p>
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * Produces the {@link OpenAPI} bean that configures the Swagger UI.
     *
     * <p>The resulting specification includes:
     * <ul>
     *   <li>API title: {@code M06 — Feed &amp; Search}</li>
     *   <li>A global Bearer JWT security requirement</li>
     *   <li>The {@code bearerAuth} security scheme using the HTTP bearer format</li>
     * </ul>
     * </p>
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("M06 — Feed & Search")
                        .description("Patch feed, search and recommendation service for PATRICI.A")
                        .version("0.0.1-SNAPSHOT"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
