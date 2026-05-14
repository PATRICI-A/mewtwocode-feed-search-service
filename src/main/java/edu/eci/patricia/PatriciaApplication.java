package edu.eci.patricia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the M06 Feed &amp; Search microservice.
 *
 * <p>This Spring Boot application provides the patch feed, search,
 * recommendation, and join capabilities for the PATRICI.A platform.</p>
 */
@SpringBootApplication
public class PatriciaApplication {

    /**
     * Bootstraps the Spring application context and starts the embedded server.
     *
     * @param args command-line arguments passed to the JVM at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(PatriciaApplication.class, args);
    }
}
