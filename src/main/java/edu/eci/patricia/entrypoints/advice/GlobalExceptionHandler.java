package edu.eci.patricia.entrypoints.advice;

import edu.eci.patricia.domain.exceptions.BusinessRuleException;
import edu.eci.patricia.domain.exceptions.PatchNotFoundException;
import edu.eci.patricia.domain.exceptions.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Formato estándar de error del equipo:
 * { "error": "TIPO_ERROR", "message": "descripción legible", "status": 4xx }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validación fallida");
        return error("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PatchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PatchNotFoundException ex) {
        return error("PATCH_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException ex) {
        return error("BUSINESS_RULE_VIOLATION", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return error("SERVICE_UNAVAILABLE", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        return error("INTERNAL_ERROR", "Error inesperado en el servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> error(String type, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of(
                "error", type,
                "message", message,
                "status", status.value()
        ));
    }
}
