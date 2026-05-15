package edu.eci.patricia.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration class for the Feed &amp; Search microservice.
 *
 * <p>Registers a global CORS policy that permits requests from any origin with the standard
 * HTTP methods and arbitrary headers. This configuration is intentionally permissive for
 * development and should be tightened for production deployments.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registers a CORS mapping that applies to all URL patterns ({@code /**}).
     *
     * <p>Allowed origins: {@code *} (any). Allowed methods: GET, POST, PUT, DELETE, OPTIONS.
     * Allowed headers: {@code *} (any).</p>
     *
     * @param registry the {@link CorsRegistry} to which the mapping is added
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
