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

@DisplayName("UpdateBenefitRequest DTO Tests")
class UpdateBenefitRequestTest {

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
            UpdateBenefitRequest request = new UpdateBenefitRequest(
                    "Updated Name", "Updated desc", 200, "Updated Provider", "http://new.jpg"
            );

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation with all null fields (partial update)")
        void shouldPassValidationWithAllNullFields() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation with only name set")
        void shouldPassValidationWithOnlyNameSet() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("New Name");

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when pointsRequired is zero")
        void shouldFailWhenPointsRequiredIsZero() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setPointsRequired(0);

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when pointsRequired is negative")
        void shouldFailWhenPointsRequiredIsNegative() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setPointsRequired(-10);

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass when pointsRequired is 1")
        void shouldPassWhenPointsRequiredIsOne() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setPointsRequired(1);

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when description exceeds 1000 characters")
        void shouldFailWhenDescriptionTooLong() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setDescription("A".repeat(1001));

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when name exceeds 255 characters")
        void shouldFailWhenNameTooLong() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("A".repeat(256));

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();

            assertNull(request.getName());
            assertNull(request.getDescription());
            assertNull(request.getPointsRequired());
            assertNull(request.getProvider());
            assertNull(request.getImageUrl());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            UpdateBenefitRequest request = new UpdateBenefitRequest(
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
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Test Name");
            request.setDescription("Test Desc");
            request.setPointsRequired(300);
            request.setProvider("Test Provider");
            request.setImageUrl("http://test.jpg");

            assertEquals("Test Name", request.getName());
            assertEquals("Test Desc", request.getDescription());
            assertEquals(300, request.getPointsRequired());
            assertEquals("Test Provider", request.getProvider());
            assertEquals("http://test.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("Should implement equals and hashCode")
        void shouldImplementEqualsAndHashCode() {
            UpdateBenefitRequest r1 = new UpdateBenefitRequest("A", "B", 1, "C", "D");
            UpdateBenefitRequest r2 = new UpdateBenefitRequest("A", "B", 1, "C", "D");

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("Should implement toString")
        void shouldImplementToString() {
            UpdateBenefitRequest request = new UpdateBenefitRequest("Test", null, null, null, null);

            assertNotNull(request.toString());
            assertTrue(request.toString().contains("Test"));
        }
    }
}
