package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    // ResourceNotFoundException Tests
    @Test
    void testResourceNotFoundExceptionMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        assertEquals("Resource not found", exception.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionWithId() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Opportunity not found with id: 123");

        assertTrue(exception.getMessage().contains("123"));
        assertTrue(exception.getMessage().contains("Opportunity"));
    }

    @Test
    void testResourceNotFoundExceptionInheritance() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        assertTrue(exception instanceof RuntimeException);
    }

    // GlobalExceptionHandler - handleResourceNotFoundException Tests
    @Test
    void testHandleResourceNotFoundExceptionReturnsNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Opportunity not found with id: 1");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Opportunity not found with id: 1", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleResourceNotFoundExceptionWithPromoter() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Promoter not found with id: 999");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Promoter"));
        assertTrue(response.getBody().message().contains("999"));
    }

    // GlobalExceptionHandler - handleValidationExceptions Tests
    @Test
    void testHandleValidationExceptionsReturnsBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        FieldError fieldError1 = new FieldError("object", "title", "Title is required");
        FieldError fieldError2 = new FieldError("object", "description", "Description is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Title is required", errors.get("title"));
        assertEquals("Description is required", errors.get("description"));
    }

    @Test
    void testHandleValidationExceptionsWithSingleError() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        FieldError fieldError = new FieldError("object", "email", "Email is invalid");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(1, errors.size());
        assertEquals("Email is invalid", errors.get("email"));
    }

    // GlobalExceptionHandler - handleGenericException Tests
    @Test
    void testHandleGenericExceptionReturnsInternalServerError() {
        Exception exception = new Exception("Something went wrong");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertTrue(response.getBody().message().contains("Something went wrong"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleGenericExceptionWithNullPointer() {
        NullPointerException exception = new NullPointerException("Null value encountered");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Null value encountered"));
    }

    @Test
    void testHandleGenericExceptionWithIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Invalid argument"));
    }

    // ErrorResponse record Tests
    @Test
    void testErrorResponseRecord() {
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(404, "Not found", java.time.LocalDateTime.now());

        assertEquals(404, errorResponse.status());
        assertEquals("Not found", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }

    @Test
    void testErrorResponseRecordEquality() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse error1 =
                new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);
        GlobalExceptionHandler.ErrorResponse error2 =
                new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void testErrorResponseRecordToString() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(500, "Server error", now);

        String toString = errorResponse.toString();
        assertTrue(toString.contains("500"));
        assertTrue(toString.contains("Server error"));
    }
}
