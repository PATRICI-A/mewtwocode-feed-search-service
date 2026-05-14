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
 * Centralised exception handler for all REST controllers in the Feed &amp; Search microservice.
 *
 * <p>Intercepts known domain and validation exceptions and maps them to structured JSON error
 * responses with a consistent format:
 * <pre>{ "error": "ERROR_TYPE", "message": "human-readable description", "status": 4xx }</pre>
 * Unrecognised exceptions are caught by a catch-all handler that returns HTTP 500.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures triggered by {@code @Valid} on controller method parameters.
     *
     * @param ex the exception containing the field-level binding errors
     * @return a 400 Bad Request response with error type {@code VALIDATION_ERROR}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return error("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the case where a requested patch does not exist.
     *
     * @param ex the exception carrying the not-found message
     * @return a 404 Not Found response with error type {@code PATCH_NOT_FOUND}
     */
    @ExceptionHandler(PatchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PatchNotFoundException ex) {
        return error("PATCH_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles domain business-rule violations such as joining a full patch or a patch that is not open.
     *
     * @param ex the exception carrying the business-rule violation message
     * @return a 422 Unprocessable Entity response with error type {@code BUSINESS_RULE_VIOLATION}
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException ex) {
        return error("BUSINESS_RULE_VIOLATION", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles failures caused by an unavailable downstream service or infrastructure component.
     *
     * @param ex the exception carrying the service-unavailable message
     * @return a 503 Service Unavailable response with error type {@code SERVICE_UNAVAILABLE}
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return error("SERVICE_UNAVAILABLE", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Catch-all handler for any unrecognised or unexpected runtime exception.
     *
     * @param ex the unhandled exception
     * @return a 500 Internal Server Error response with error type {@code INTERNAL_ERROR}
     *         and a generic message that does not expose internal details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        return error("INTERNAL_ERROR", "Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds a standardised error response body and wraps it in a {@link ResponseEntity}.
     *
     * @param type    a short machine-readable error code (e.g. {@code VALIDATION_ERROR})
     * @param message a human-readable description of the error
     * @param status  the HTTP status code to set on the response
     * @return a {@link ResponseEntity} containing the error map with keys {@code error},
     *         {@code message}, and {@code status}
     */
    private ResponseEntity<Map<String, Object>> error(String type, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of(
                "error", type,
                "message", message,
                "status", status.value()
        ));
    }
}
