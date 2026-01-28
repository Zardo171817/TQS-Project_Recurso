package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateVolunteerProfileRequest Tests")
class CreateVolunteerProfileRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("No-args constructor creates instance")
        void noArgsConstructorCreatesInstance() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            assertNotNull(request);
        }

        @Test
        @DisplayName("All-args constructor sets all fields")
        void allArgsConstructorSetsAllFields() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", "+351912345678",
                    "Java, Python", "Education", "Weekends", "My bio"
            );

            assertEquals("John Doe", request.getName());
            assertEquals("john@email.com", request.getEmail());
            assertEquals("+351912345678", request.getPhone());
            assertEquals("Java, Python", request.getSkills());
            assertEquals("Education", request.getInterests());
            assertEquals("Weekends", request.getAvailability());
            assertEquals("My bio", request.getBio());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setName and getName work correctly")
        void setAndGetName() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setName("Maria");
            assertEquals("Maria", request.getName());
        }

        @Test
        @DisplayName("setEmail and getEmail work correctly")
        void setAndGetEmail() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setEmail("maria@email.com");
            assertEquals("maria@email.com", request.getEmail());
        }

        @Test
        @DisplayName("setPhone and getPhone work correctly")
        void setAndGetPhone() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setPhone("+351912345678");
            assertEquals("+351912345678", request.getPhone());
        }

        @Test
        @DisplayName("setSkills and getSkills work correctly")
        void setAndGetSkills() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setSkills("Programming");
            assertEquals("Programming", request.getSkills());
        }

        @Test
        @DisplayName("setInterests and getInterests work correctly")
        void setAndGetInterests() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setInterests("Environment");
            assertEquals("Environment", request.getInterests());
        }

        @Test
        @DisplayName("setAvailability and getAvailability work correctly")
        void setAndGetAvailability() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setAvailability("Mornings");
            assertEquals("Mornings", request.getAvailability());
        }

        @Test
        @DisplayName("setBio and getBio work correctly")
        void setAndGetBio() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
            request.setBio("My biography");
            assertEquals("My biography", request.getBio());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid request passes validation")
        void validRequestPassesValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Blank name fails validation")
        void blankNameFailsValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "", "john@email.com", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Null name fails validation")
        void nullNameFailsValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    null, "john@email.com", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Name too short fails validation")
        void nameTooShortFailsValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "J", "john@email.com", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Name too long fails validation")
        void nameTooLongFailsValidation() {
            String longName = "A".repeat(101);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    longName, "john@email.com", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Blank email fails validation")
        void blankEmailFailsValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Invalid email format fails validation")
        void invalidEmailFailsValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "invalid-email", null, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Phone too long fails validation")
        void phoneTooLongFailsValidation() {
            String longPhone = "1".repeat(21);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", longPhone, null, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
        }

        @Test
        @DisplayName("Skills too long fails validation")
        void skillsTooLongFailsValidation() {
            String longSkills = "A".repeat(501);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", null, longSkills, null, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("skills")));
        }

        @Test
        @DisplayName("Interests too long fails validation")
        void interestsTooLongFailsValidation() {
            String longInterests = "A".repeat(501);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", null, null, longInterests, null, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("interests")));
        }

        @Test
        @DisplayName("Availability too long fails validation")
        void availabilityTooLongFailsValidation() {
            String longAvailability = "A".repeat(501);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", null, null, null, longAvailability, null
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("availability")));
        }

        @Test
        @DisplayName("Bio too long fails validation")
        void bioTooLongFailsValidation() {
            String longBio = "A".repeat(1001);
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", null, null, null, null, longBio
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bio")));
        }

        @Test
        @DisplayName("Valid complete request passes validation")
        void validCompleteRequestPassesValidation() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", "+351912345678",
                    "Java, Python", "Education, Environment", "Weekends, Evenings",
                    "I am a software developer passionate about volunteering."
            );

            Set<ConstraintViolation<CreateVolunteerProfileRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode and ToString Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("equals returns true for same values")
        void equalsReturnsTrueForSameValues() {
            CreateVolunteerProfileRequest r1 = new CreateVolunteerProfileRequest(
                    "John", "john@email.com", null, null, null, null, null
            );
            CreateVolunteerProfileRequest r2 = new CreateVolunteerProfileRequest(
                    "John", "john@email.com", null, null, null, null, null
            );

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("hashCode is consistent for equal objects")
        void hashCodeIsConsistent() {
            CreateVolunteerProfileRequest r1 = new CreateVolunteerProfileRequest(
                    "John", "john@email.com", null, null, null, null, null
            );
            CreateVolunteerProfileRequest r2 = new CreateVolunteerProfileRequest(
                    "John", "john@email.com", null, null, null, null, null
            );

            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("toString contains field values")
        void toStringContainsValues() {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John", "john@email.com", null, null, null, null, null
            );

            String toString = request.toString();
            assertTrue(toString.contains("John"));
            assertTrue(toString.contains("john@email.com"));
        }
    }
}
