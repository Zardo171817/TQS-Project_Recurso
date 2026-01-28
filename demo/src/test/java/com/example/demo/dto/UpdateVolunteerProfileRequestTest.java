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

@DisplayName("UpdateVolunteerProfileRequest Tests")
class UpdateVolunteerProfileRequestTest {

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
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            assertNotNull(request);
        }

        @Test
        @DisplayName("All-args constructor sets all fields")
        void allArgsConstructorSetsAllFields() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest(
                    "John Doe", "+351912345678", "Java, Python",
                    "Education", "Weekends", "My bio"
            );

            assertEquals("John Doe", request.getName());
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
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("Maria");
            assertEquals("Maria", request.getName());
        }

        @Test
        @DisplayName("setPhone and getPhone work correctly")
        void setAndGetPhone() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setPhone("+351912345678");
            assertEquals("+351912345678", request.getPhone());
        }

        @Test
        @DisplayName("setSkills and getSkills work correctly")
        void setAndGetSkills() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setSkills("Programming");
            assertEquals("Programming", request.getSkills());
        }

        @Test
        @DisplayName("setInterests and getInterests work correctly")
        void setAndGetInterests() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setInterests("Environment");
            assertEquals("Environment", request.getInterests());
        }

        @Test
        @DisplayName("setAvailability and getAvailability work correctly")
        void setAndGetAvailability() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setAvailability("Mornings");
            assertEquals("Mornings", request.getAvailability());
        }

        @Test
        @DisplayName("setBio and getBio work correctly")
        void setAndGetBio() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setBio("My biography");
            assertEquals("My biography", request.getBio());
        }
    }

    @Nested
    @DisplayName("hasUpdates Method Tests")
    class HasUpdatesTests {

        @Test
        @DisplayName("hasUpdates returns false when all fields are null")
        void hasUpdatesReturnsFalseWhenAllNull() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            assertFalse(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when name is set")
        void hasUpdatesReturnsTrueWhenNameSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("John");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when phone is set")
        void hasUpdatesReturnsTrueWhenPhoneSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setPhone("+351912345678");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when skills is set")
        void hasUpdatesReturnsTrueWhenSkillsSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setSkills("Java");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when interests is set")
        void hasUpdatesReturnsTrueWhenInterestsSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setInterests("Education");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when availability is set")
        void hasUpdatesReturnsTrueWhenAvailabilitySet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setAvailability("Weekends");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when bio is set")
        void hasUpdatesReturnsTrueWhenBioSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setBio("My bio");
            assertTrue(request.hasUpdates());
        }

        @Test
        @DisplayName("hasUpdates returns true when multiple fields are set")
        void hasUpdatesReturnsTrueWhenMultipleFieldsSet() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest(
                    "John", "+351912345678", "Java", "Education", "Weekends", "Bio"
            );
            assertTrue(request.hasUpdates());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Empty request passes validation")
        void emptyRequestPassesValidation() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Valid partial update passes validation")
        void validPartialUpdatePassesValidation() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("John Doe");

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Name too short fails validation")
        void nameTooShortFailsValidation() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("J");

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Name too long fails validation")
        void nameTooLongFailsValidation() {
            String longName = "A".repeat(101);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName(longName);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Phone too long fails validation")
        void phoneTooLongFailsValidation() {
            String longPhone = "1".repeat(21);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setPhone(longPhone);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
        }

        @Test
        @DisplayName("Skills too long fails validation")
        void skillsTooLongFailsValidation() {
            String longSkills = "A".repeat(501);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setSkills(longSkills);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("skills")));
        }

        @Test
        @DisplayName("Interests too long fails validation")
        void interestsTooLongFailsValidation() {
            String longInterests = "A".repeat(501);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setInterests(longInterests);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("interests")));
        }

        @Test
        @DisplayName("Availability too long fails validation")
        void availabilityTooLongFailsValidation() {
            String longAvailability = "A".repeat(501);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setAvailability(longAvailability);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("availability")));
        }

        @Test
        @DisplayName("Bio too long fails validation")
        void bioTooLongFailsValidation() {
            String longBio = "A".repeat(1001);
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setBio(longBio);

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bio")));
        }

        @Test
        @DisplayName("Valid complete update passes validation")
        void validCompleteUpdatePassesValidation() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest(
                    "John Doe", "+351912345678", "Java, Python",
                    "Education, Environment", "Weekends, Evenings",
                    "I am a passionate volunteer."
            );

            Set<ConstraintViolation<UpdateVolunteerProfileRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode and ToString Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("equals returns true for same values")
        void equalsReturnsTrueForSameValues() {
            UpdateVolunteerProfileRequest r1 = new UpdateVolunteerProfileRequest();
            r1.setName("John");

            UpdateVolunteerProfileRequest r2 = new UpdateVolunteerProfileRequest();
            r2.setName("John");

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("equals returns false for different values")
        void equalsReturnsFalseForDifferentValues() {
            UpdateVolunteerProfileRequest r1 = new UpdateVolunteerProfileRequest();
            r1.setName("John");

            UpdateVolunteerProfileRequest r2 = new UpdateVolunteerProfileRequest();
            r2.setName("Maria");

            assertNotEquals(r1, r2);
        }

        @Test
        @DisplayName("hashCode is consistent for equal objects")
        void hashCodeIsConsistent() {
            UpdateVolunteerProfileRequest r1 = new UpdateVolunteerProfileRequest();
            r1.setName("John");
            r1.setSkills("Java");

            UpdateVolunteerProfileRequest r2 = new UpdateVolunteerProfileRequest();
            r2.setName("John");
            r2.setSkills("Java");

            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("toString contains field values")
        void toStringContainsValues() {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("John");
            request.setSkills("Java");

            String toString = request.toString();
            assertTrue(toString.contains("John"));
            assertTrue(toString.contains("Java"));
        }
    }
}
