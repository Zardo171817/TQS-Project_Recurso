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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("ResourceNotFoundException Handler Tests")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 with correct message")
        void shouldReturn404WithCorrectMessage() {
            ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleResourceNotFoundException(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Resource not found");
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            ResourceNotFoundException exception = new ResourceNotFoundException("Test");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleResourceNotFoundException(exception);

            assertThat(response.getBody().timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("IllegalStateException Handler Tests")
    class IllegalStateExceptionTests {

        @Test
        @DisplayName("Should return 409 with correct message")
        void shouldReturn409WithCorrectMessage() {
            IllegalStateException exception = new IllegalStateException("Conflict occurred");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleIllegalStateException(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Conflict occurred");
            assertThat(response.getBody().status()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handler Tests")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("Should return 400 with correct message")
        void shouldReturn400WithCorrectMessage() {
            IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleIllegalArgumentException(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().message()).isEqualTo("Invalid argument");
            assertThat(response.getBody().status()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handler Tests")
    class MethodArgumentNotValidExceptionTests {

        @Test
        @DisplayName("Should return 400 with validation errors")
        void shouldReturn400WithValidationErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("object", "field", "must not be blank");
            when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().get("status")).isEqualTo(400);
            assertThat(response.getBody()).containsKey("errors");
        }

        @Test
        @DisplayName("Should map field errors correctly")
        void shouldMapFieldErrorsCorrectly() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError1 = new FieldError("object", "name", "must not be blank");
            FieldError fieldError2 = new FieldError("object", "email", "invalid format");
            when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));

            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertThat(errors).containsEntry("name", "must not be blank");
            assertThat(errors).containsEntry("email", "invalid format");
        }
    }

    @Nested
    @DisplayName("Generic Exception Handler Tests")
    class GenericExceptionTests {

        @Test
        @DisplayName("Should return 500 with generic message")
        void shouldReturn500WithGenericMessage() {
            Exception exception = new Exception("Unexpected error");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleGenericException(exception);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().status()).isEqualTo(500);
            assertThat(response.getBody().message()).contains("An unexpected error occurred");
        }
    }

    @Nested
    @DisplayName("ErrorResponse Record Tests")
    class ErrorResponseTests {

        @Test
        @DisplayName("Should create ErrorResponse with all fields")
        void shouldCreateErrorResponseWithAllFields() {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            GlobalExceptionHandler.ErrorResponse response =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertThat(response.status()).isEqualTo(404);
            assertThat(response.message()).isEqualTo("Not found");
            assertThat(response.timestamp()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            GlobalExceptionHandler.ErrorResponse response1 =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);
            GlobalExceptionHandler.ErrorResponse response2 =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertThat(response1).isEqualTo(response2);
        }
    }
}
