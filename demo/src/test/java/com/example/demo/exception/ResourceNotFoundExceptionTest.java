package com.example.demo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Volunteer not found with id: 1";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test message");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should handle empty message")
    void shouldHandleEmptyMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }

    @Test
    @DisplayName("Should preserve message when caught")
    void shouldPreserveMessageWhenCaught() {
        String expectedMessage = "Profile not found with email: test@email.com";

        try {
            throw new ResourceNotFoundException(expectedMessage);
        } catch (ResourceNotFoundException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
