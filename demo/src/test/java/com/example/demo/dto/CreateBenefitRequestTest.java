package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateBenefitRequest DTO Tests")
class CreateBenefitRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassValidationWithAllValidFields() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Desconto Cinema", "20% desconto", 150, "Cinema NOS", "http://img.jpg"
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation without imageUrl")
        void shouldPassValidationWithoutImageUrl() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Desconto Cinema", "20% desconto", 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void shouldFailWhenNameIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    null, "20% desconto", 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void shouldFailWhenNameIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "", "20% desconto", 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when description is null")
        void shouldFailWhenDescriptionIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", null, 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
        }

        @Test
        @DisplayName("Should fail validation when description is blank")
        void shouldFailWhenDescriptionIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "", 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is null")
        void shouldFailWhenPointsRequiredIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", null, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pointsRequired")));
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is zero")
        void shouldFailWhenPointsRequiredIsZero() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 0, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is negative")
        void shouldFailWhenPointsRequiredIsNegative() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", -1, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation when pointsRequired is 1")
        void shouldPassWhenPointsRequiredIsOne() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 1, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when provider is null")
        void shouldFailWhenProviderIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 150, null, null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("provider")));
        }

        @Test
        @DisplayName("Should fail validation when provider is blank")
        void shouldFailWhenProviderIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 150, "", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when description exceeds 1000 characters")
        void shouldFailWhenDescriptionTooLong() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "A".repeat(1001), 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass when description is exactly 1000 characters")
        void shouldPassWhenDescriptionIsExactly1000() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "A".repeat(1000), 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when name exceeds 255 characters")
        void shouldFailWhenNameTooLong() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "A".repeat(256), "desc", 150, "Cinema NOS", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            CreateBenefitRequest request = new CreateBenefitRequest();

            assertNull(request.getName());
            assertNull(request.getDescription());
            assertNull(request.getPointsRequired());
            assertNull(request.getProvider());
            assertNull(request.getImageUrl());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Name", "Desc", 100, "Provider", "http://img.jpg"
            );

            assertEquals("Name", request.getName());
            assertEquals("Desc", request.getDescription());
            assertEquals(100, request.getPointsRequired());
            assertEquals("Provider", request.getProvider());
            assertEquals("http://img.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            CreateBenefitRequest request = new CreateBenefitRequest();
            request.setName("Test Name");
            request.setDescription("Test Description");
            request.setPointsRequired(200);
            request.setProvider("Test Provider");
            request.setImageUrl("http://test.jpg");

            assertEquals("Test Name", request.getName());
            assertEquals("Test Description", request.getDescription());
            assertEquals(200, request.getPointsRequired());
            assertEquals("Test Provider", request.getProvider());
            assertEquals("http://test.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("Should implement equals and hashCode")
        void shouldImplementEqualsAndHashCode() {
            CreateBenefitRequest request1 = new CreateBenefitRequest("A", "B", 1, "C", "D");
            CreateBenefitRequest request2 = new CreateBenefitRequest("A", "B", 1, "C", "D");

            assertEquals(request1, request2);
            assertEquals(request1.hashCode(), request2.hashCode());
        }

        @Test
        @DisplayName("Should implement toString")
        void shouldImplementToString() {
            CreateBenefitRequest request = new CreateBenefitRequest("Test", "Desc", 100, "Prov", null);

            String toString = request.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("Test"));
        }
    }
}
