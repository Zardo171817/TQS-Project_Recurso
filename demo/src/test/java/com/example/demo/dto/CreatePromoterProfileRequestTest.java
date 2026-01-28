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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreatePromoterProfileRequest Tests")
class CreatePromoterProfileRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should fail when name is blank")
        void shouldFailWhenNameIsBlank() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("");
            request.setEmail("test@test.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Name is required") ||
                    v.getMessage().contains("Name must be between"));
        }

        @Test
        @DisplayName("Should fail when name is null")
        void shouldFailWhenNameIsNull() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName(null);
            request.setEmail("test@test.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Name is required"));
        }

        @Test
        @DisplayName("Should fail when name is too short")
        void shouldFailWhenNameTooShort() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("A");
            request.setEmail("test@test.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Name must be between 2 and 100"));
        }

        @Test
        @DisplayName("Should pass with valid name")
        void shouldPassWithValidName() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("test@test.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should fail when email is blank")
        void shouldFailWhenEmailIsBlank() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should fail when email is invalid format")
        void shouldFailWhenEmailIsInvalidFormat() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("invalid-email");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Invalid email format"));
        }

        @Test
        @DisplayName("Should pass with valid email")
        void shouldPassWithValidEmail() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("maria@organizacao.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Organization Validation Tests")
    class OrganizationValidationTests {

        @Test
        @DisplayName("Should fail when organization is blank")
        void shouldFailWhenOrganizationIsBlank() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("test@test.org");
            request.setOrganization("");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should fail when organization is too short")
        void shouldFailWhenOrganizationTooShort() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("test@test.org");
            request.setOrganization("A");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Organization must be between"));
        }
    }

    @Nested
    @DisplayName("Optional Fields Validation Tests")
    class OptionalFieldsValidationTests {

        @Test
        @DisplayName("Should pass with all fields filled")
        void shouldPassWithAllFieldsFilled() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest(
                    "Maria Santos",
                    "maria@org.org",
                    "Associacao Solidaria",
                    "Description",
                    "+351 234 567 890",
                    "https://www.org.org",
                    "Address",
                    "https://logo.png",
                    "ONG",
                    "Educacao",
                    "2010",
                    "21-50",
                    "Facebook: /org"
            );

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass with only required fields")
        void shouldPassWithOnlyRequiredFields() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("maria@org.org");
            request.setOrganization("Test Org");

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail when description exceeds max length")
        void shouldFailWhenDescriptionExceedsMaxLength() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("maria@org.org");
            request.setOrganization("Test Org");
            request.setDescription("A".repeat(1001));

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Description must be at most 1000"));
        }

        @Test
        @DisplayName("Should fail when phone exceeds max length")
        void shouldFailWhenPhoneExceedsMaxLength() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
            request.setName("Maria Santos");
            request.setEmail("maria@org.org");
            request.setOrganization("Test Org");
            request.setPhone("A".repeat(21));

            Set<ConstraintViolation<CreatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Phone must be at most 20"));
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set all fields correctly")
        void shouldGetAndSetAllFieldsCorrectly() {
            CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();

            request.setName("Test Name");
            request.setEmail("test@test.org");
            request.setOrganization("Test Org");
            request.setDescription("Test Description");
            request.setPhone("123456789");
            request.setWebsite("https://test.org");
            request.setAddress("Test Address");
            request.setLogoUrl("https://logo.png");
            request.setOrganizationType("ONG");
            request.setAreaOfActivity("Education");
            request.setFoundedYear("2020");
            request.setNumberOfEmployees("1-5");
            request.setSocialMedia("Twitter: @test");

            assertThat(request.getName()).isEqualTo("Test Name");
            assertThat(request.getEmail()).isEqualTo("test@test.org");
            assertThat(request.getOrganization()).isEqualTo("Test Org");
            assertThat(request.getDescription()).isEqualTo("Test Description");
            assertThat(request.getPhone()).isEqualTo("123456789");
            assertThat(request.getWebsite()).isEqualTo("https://test.org");
            assertThat(request.getAddress()).isEqualTo("Test Address");
            assertThat(request.getLogoUrl()).isEqualTo("https://logo.png");
            assertThat(request.getOrganizationType()).isEqualTo("ONG");
            assertThat(request.getAreaOfActivity()).isEqualTo("Education");
            assertThat(request.getFoundedYear()).isEqualTo("2020");
            assertThat(request.getNumberOfEmployees()).isEqualTo("1-5");
            assertThat(request.getSocialMedia()).isEqualTo("Twitter: @test");
        }
    }
}
