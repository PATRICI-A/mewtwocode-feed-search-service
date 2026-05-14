package edu.eci.patricia.infrastructure.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security configuration for the Feed &amp; Search microservice.
 *
 * <p>Configures a stateless, CSRF-disabled security filter chain with all requests permitted
 * (JWT validation is delegated to downstream checks or a future gateway). Also registers a
 * {@link JwtDecoder} bean using the HMAC-SHA256 secret defined by the {@code jwt.secret} property.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String SECRET = "patriciasecretkey2026supersecreto";
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/h2-console",
            "/h2-console/**"
    };

    /**
     * Builds and returns the application's primary {@link SecurityFilterChain}.
     *
     * <p>The chain disables CSRF protection, enables same-origin framing (for H2 console),
     * enforces stateless session management, and permits all incoming HTTP requests without
     * authentication checks.</p>
     *
     * @param http the {@link HttpSecurity} builder provided by Spring Security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if any security configuration step fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }

    /**
     * Creates a {@link JwtDecoder} bean that validates JWT tokens signed with HMAC-SHA256
     * using the secret loaded from the {@code jwt.secret} application property.
     *
     * @return a {@link NimbusJwtDecoder} configured with the application's signing key
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
