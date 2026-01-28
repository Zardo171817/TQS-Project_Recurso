package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleResourceNotFoundException Tests")
    class HandleResourceNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 status with error message")
        void handleResourceNotFoundExceptionReturns404() {
            ResourceNotFoundException exception = new ResourceNotFoundException("Volunteer not found with id: 1");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleResourceNotFoundException(exception);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().status());
            assertEquals("Volunteer not found with id: 1", response.getBody().message());
            assertNotNull(response.getBody().timestamp());
        }

        @Test
        @DisplayName("Should handle empty message")
        void handleResourceNotFoundExceptionWithEmptyMessage() {
            ResourceNotFoundException exception = new ResourceNotFoundException("");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleResourceNotFoundException(exception);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("", response.getBody().message());
        }
    }

    @Nested
    @DisplayName("handleIllegalStateException Tests")
    class HandleIllegalStateExceptionTests {

        @Test
        @DisplayName("Should return 409 status with error message")
        void handleIllegalStateExceptionReturns409() {
            IllegalStateException exception = new IllegalStateException("Invalid state");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleIllegalStateException(exception);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().status());
            assertEquals("Invalid state", response.getBody().message());
            assertNotNull(response.getBody().timestamp());
        }
    }

    @Nested
    @DisplayName("handleIllegalArgumentException Tests")
    class HandleIllegalArgumentExceptionTests {

        @Test
        @DisplayName("Should return 400 status with error message")
        void handleIllegalArgumentExceptionReturns400() {
            IllegalArgumentException exception = new IllegalArgumentException("Email already exists: test@email.com");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleIllegalArgumentException(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().status());
            assertEquals("Email already exists: test@email.com", response.getBody().message());
            assertNotNull(response.getBody().timestamp());
        }

        @Test
        @DisplayName("Should handle no fields to update message")
        void handleIllegalArgumentExceptionNoFields() {
            IllegalArgumentException exception = new IllegalArgumentException("No fields to update provided");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleIllegalArgumentException(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("No fields to update provided", response.getBody().message());
        }
    }

    @Nested
    @DisplayName("handleValidationExceptions Tests")
    class HandleValidationExceptionsTests {

        @Test
        @DisplayName("Should return 400 status with field errors")
        void handleValidationExceptionsReturns400() {
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError1 = new FieldError("request", "name", "Name is required");
            FieldError fieldError2 = new FieldError("request", "email", "Invalid email format");

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
            assertEquals("Name is required", errors.get("name"));
            assertEquals("Invalid email format", errors.get("email"));
        }

        @Test
        @DisplayName("Should handle single field error")
        void handleSingleValidationError() {
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError = new FieldError("request", "name", "Name must be between 2 and 100 characters");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<Map<String, Object>> response =
                    exceptionHandler.handleValidationExceptions(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertEquals(1, errors.size());
            assertEquals("Name must be between 2 and 100 characters", errors.get("name"));
        }
    }

    @Nested
    @DisplayName("handleGenericException Tests")
    class HandleGenericExceptionTests {

        @Test
        @DisplayName("Should return 500 status with error message")
        void handleGenericExceptionReturns500() {
            Exception exception = new RuntimeException("Unexpected error occurred");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleGenericException(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().status());
            assertTrue(response.getBody().message().contains("An unexpected error occurred"));
            assertTrue(response.getBody().message().contains("Unexpected error occurred"));
            assertNotNull(response.getBody().timestamp());
        }

        @Test
        @DisplayName("Should handle NullPointerException")
        void handleNullPointerException() {
            Exception exception = new NullPointerException("null value");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    exceptionHandler.handleGenericException(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().message().contains("null value"));
        }
    }

    @Nested
    @DisplayName("ErrorResponse Record Tests")
    class ErrorResponseRecordTests {

        @Test
        @DisplayName("ErrorResponse should store and return values correctly")
        void errorResponseStoresValues() {
            var now = java.time.LocalDateTime.now();
            var errorResponse = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertEquals(404, errorResponse.status());
            assertEquals("Not found", errorResponse.message());
            assertEquals(now, errorResponse.timestamp());
        }

        @Test
        @DisplayName("ErrorResponse equals should work correctly")
        void errorResponseEquals() {
            var now = java.time.LocalDateTime.now();
            var errorResponse1 = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);
            var errorResponse2 = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertEquals(errorResponse1, errorResponse2);
        }

        @Test
        @DisplayName("ErrorResponse hashCode should be consistent")
        void errorResponseHashCode() {
            var now = java.time.LocalDateTime.now();
            var errorResponse1 = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);
            var errorResponse2 = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertEquals(errorResponse1.hashCode(), errorResponse2.hashCode());
        }

        @Test
        @DisplayName("ErrorResponse toString should contain field values")
        void errorResponseToString() {
            var now = java.time.LocalDateTime.now();
            var errorResponse = new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            String toString = errorResponse.toString();
            assertTrue(toString.contains("404"));
            assertTrue(toString.contains("Not found"));
        }
    }
}
