package com.example.demo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404")
    void shouldHandleResourceNotFoundWith404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Benefit not found with id: 1");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Benefit not found with id: 1", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Should handle IllegalStateException with 409")
    void shouldHandleIllegalStateExceptionWith409() {
        IllegalStateException ex = new IllegalStateException("Only PARTNER benefits can be updated");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Only PARTNER benefits can be updated", response.getBody().message());
    }

    @Test
    @DisplayName("Should handle generic Exception with 500")
    void shouldHandleGenericExceptionWith500() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertTrue(response.getBody().message().contains("Unexpected error"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400")
    void shouldHandleValidationExceptionWith400() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "Name is required"));
        bindingResult.addError(new FieldError("request", "description", "Description is required"));

        MethodParameter methodParameter = new MethodParameter(
                this.getClass().getDeclaredMethod("shouldHandleValidationExceptionWith400"), -1
        );
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("errors"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Name is required", errors.get("name"));
        assertEquals("Description is required", errors.get("description"));
    }

    @Test
    @DisplayName("Should create ResourceNotFoundException with message")
    void shouldCreateResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test message");

        assertEquals("Test message", ex.getMessage());
    }

    @Test
    @DisplayName("Should handle ErrorResponse record correctly")
    void shouldHandleErrorResponseRecord() {
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(404, "Not Found", java.time.LocalDateTime.now());

        assertEquals(404, errorResponse.status());
        assertEquals("Not Found", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
}
