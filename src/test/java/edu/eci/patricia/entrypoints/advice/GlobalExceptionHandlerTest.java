package edu.eci.patricia.entrypoints.advice;

import edu.eci.patricia.domain.exceptions.BusinessRuleException;
import edu.eci.patricia.domain.exceptions.PatchNotFoundException;
import edu.eci.patricia.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundRetorna404() {
        UUID patchId = UUID.randomUUID();
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new PatchNotFoundException(patchId));

        assertError(response, HttpStatus.NOT_FOUND, "PATCH_NOT_FOUND", "Parche no encontrado: " + patchId);
    }

    @Test
    void handleBusinessRuleRetorna422() {
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessRule(new BusinessRuleException("lleno"));

        assertError(response, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", "lleno");
    }

    @Test
    void handleServiceUnavailableRetorna503() {
        ResponseEntity<Map<String, Object>> response = handler.handleServiceUnavailable(new ServiceUnavailableException("redis", new RuntimeException()));

        assertError(response, HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "redis");
    }

    @Test
    void handleUnexpectedOcultaDetalleInterno() {
        ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertError(response, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Error inesperado en el servidor");
    }

    private void assertError(ResponseEntity<Map<String, Object>> response,
                             HttpStatus status,
                             String error,
                             String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).containsEntry("error", error);
        assertThat(response.getBody()).containsEntry("message", message);
        assertThat(response.getBody()).containsEntry("status", status.value());
    }
}
