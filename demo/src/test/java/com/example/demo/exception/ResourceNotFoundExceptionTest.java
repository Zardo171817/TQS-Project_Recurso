package com.example.demo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create exception with message")
        void shouldCreateExceptionWithMessage() {
            ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

            assertThat(exception.getMessage()).isEqualTo("Resource not found");
        }

        @Test
        @DisplayName("Should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new ResourceNotFoundException("Test exception");
            })
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Test exception");
        }

        @Test
        @DisplayName("Should extend RuntimeException")
        void shouldExtendRuntimeException() {
            ResourceNotFoundException exception = new ResourceNotFoundException("Test");

            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Message Tests")
    class MessageTests {

        @Test
        @DisplayName("Should handle null message")
        void shouldHandleNullMessage() {
            ResourceNotFoundException exception = new ResourceNotFoundException(null);

            assertThat(exception.getMessage()).isNull();
        }

        @Test
        @DisplayName("Should handle empty message")
        void shouldHandleEmptyMessage() {
            ResourceNotFoundException exception = new ResourceNotFoundException("");

            assertThat(exception.getMessage()).isEmpty();
        }

        @Test
        @DisplayName("Should handle long message")
        void shouldHandleLongMessage() {
            String longMessage = "A".repeat(1000);
            ResourceNotFoundException exception = new ResourceNotFoundException(longMessage);

            assertThat(exception.getMessage()).hasSize(1000);
        }
    }
}
