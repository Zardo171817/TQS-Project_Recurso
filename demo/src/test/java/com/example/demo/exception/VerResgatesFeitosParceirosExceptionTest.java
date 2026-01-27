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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Ver Resgates Feitos Parceiro - Exception Tests")
class VerResgatesFeitosParceirosExceptionTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("ResourceNotFoundException Tests")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with message")
        void shouldCreateExceptionWithMessage() {
            ResourceNotFoundException ex = new ResourceNotFoundException("Test message");
            assertEquals("Test message", ex.getMessage());
        }

        @Test
        @DisplayName("Should be a RuntimeException")
        void shouldBeRuntimeException() {
            ResourceNotFoundException ex = new ResourceNotFoundException("test");
            assertInstanceOf(RuntimeException.class, ex);
        }
    }

    @Nested
    @DisplayName("GlobalExceptionHandler - handleResourceNotFoundException")
    class HandleResourceNotFoundTests {

        @Test
        @DisplayName("Should return 404 status")
        void shouldReturn404Status() {
            ResourceNotFoundException ex = new ResourceNotFoundException("Not found");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleResourceNotFoundException(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().status());
            assertEquals("Not found", response.getBody().message());
            assertNotNull(response.getBody().timestamp());
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            ResourceNotFoundException ex = new ResourceNotFoundException("Test");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleResourceNotFoundException(ex);

            assertNotNull(response.getBody().timestamp());
            assertTrue(response.getBody().timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("GlobalExceptionHandler - handleIllegalStateException")
    class HandleIllegalStateTests {

        @Test
        @DisplayName("Should return 409 CONFLICT status")
        void shouldReturn409Status() {
            IllegalStateException ex = new IllegalStateException("Conflict");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleIllegalStateException(ex);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().status());
            assertEquals("Conflict", response.getBody().message());
        }
    }

    @Nested
    @DisplayName("GlobalExceptionHandler - handleValidationExceptions")
    class HandleValidationTests {

        @Test
        @DisplayName("Should return 400 BAD REQUEST with field errors")
        void shouldReturn400WithFieldErrors() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError = new FieldError("request", "name", "Name is required");
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().get("status"));
            assertNotNull(response.getBody().get("errors"));
            assertNotNull(response.getBody().get("timestamp"));
        }

        @Test
        @DisplayName("Should include multiple field errors")
        void shouldIncludeMultipleFieldErrors() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError error1 = new FieldError("request", "name", "Name is required");
            FieldError error2 = new FieldError("request", "description", "Description is required");
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));

            ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertEquals(2, errors.size());
            assertEquals("Name is required", errors.get("name"));
            assertEquals("Description is required", errors.get("description"));
        }
    }

    @Nested
    @DisplayName("GlobalExceptionHandler - handleGenericException")
    class HandleGenericExceptionTests {

        @Test
        @DisplayName("Should return 500 INTERNAL SERVER ERROR")
        void shouldReturn500Status() {
            Exception ex = new Exception("Something went wrong");

            ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                    handler.handleGenericException(ex);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().status());
            assertTrue(response.getBody().message().contains("Something went wrong"));
        }
    }

    @Nested
    @DisplayName("ErrorResponse Record Tests")
    class ErrorResponseRecordTests {

        @Test
        @DisplayName("Should create ErrorResponse record")
        void shouldCreateErrorResponseRecord() {
            LocalDateTime now = LocalDateTime.now();
            GlobalExceptionHandler.ErrorResponse error =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertEquals(404, error.status());
            assertEquals("Not found", error.message());
            assertEquals(now, error.timestamp());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            LocalDateTime now = LocalDateTime.now();
            GlobalExceptionHandler.ErrorResponse e1 =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);
            GlobalExceptionHandler.ErrorResponse e2 =
                    new GlobalExceptionHandler.ErrorResponse(404, "Not found", now);

            assertEquals(e1, e2);
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            GlobalExceptionHandler.ErrorResponse error =
                    new GlobalExceptionHandler.ErrorResponse(404, "Test", LocalDateTime.now());

            String str = error.toString();
            assertNotNull(str);
            assertTrue(str.contains("404"));
            assertTrue(str.contains("Test"));
        }
    }
}
