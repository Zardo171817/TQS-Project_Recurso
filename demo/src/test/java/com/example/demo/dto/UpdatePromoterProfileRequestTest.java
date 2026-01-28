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

@DisplayName("UpdatePromoterProfileRequest Tests")
class UpdatePromoterProfileRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("HasUpdates Tests")
    class HasUpdatesTests {

        @Test
        @DisplayName("Should return false when all fields are null")
        void shouldReturnFalseWhenAllFieldsAreNull() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();

            assertThat(request.hasUpdates()).isFalse();
        }

        @Test
        @DisplayName("Should return true when name is set")
        void shouldReturnTrueWhenNameIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setName("New Name");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when organization is set")
        void shouldReturnTrueWhenOrganizationIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setOrganization("New Org");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when description is set")
        void shouldReturnTrueWhenDescriptionIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setDescription("New Description");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when phone is set")
        void shouldReturnTrueWhenPhoneIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setPhone("123456789");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when website is set")
        void shouldReturnTrueWhenWebsiteIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setWebsite("https://test.org");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when address is set")
        void shouldReturnTrueWhenAddressIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setAddress("New Address");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when logoUrl is set")
        void shouldReturnTrueWhenLogoUrlIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setLogoUrl("https://logo.png");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when organizationType is set")
        void shouldReturnTrueWhenOrganizationTypeIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setOrganizationType("ONG");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when areaOfActivity is set")
        void shouldReturnTrueWhenAreaOfActivityIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setAreaOfActivity("Education");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when foundedYear is set")
        void shouldReturnTrueWhenFoundedYearIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setFoundedYear("2020");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when numberOfEmployees is set")
        void shouldReturnTrueWhenNumberOfEmployeesIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setNumberOfEmployees("1-5");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when socialMedia is set")
        void shouldReturnTrueWhenSocialMediaIsSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setSocialMedia("Twitter: @test");

            assertThat(request.hasUpdates()).isTrue();
        }

        @Test
        @DisplayName("Should return true when multiple fields are set")
        void shouldReturnTrueWhenMultipleFieldsAreSet() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setName("New Name");
            request.setOrganization("New Org");
            request.setPhone("123456789");

            assertThat(request.hasUpdates()).isTrue();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should fail when name is too short")
        void shouldFailWhenNameTooShort() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setName("A");

            Set<ConstraintViolation<UpdatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Name must be between 2 and 100"));
        }

        @Test
        @DisplayName("Should fail when organization is too short")
        void shouldFailWhenOrganizationTooShort() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setOrganization("A");

            Set<ConstraintViolation<UpdatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Organization must be between"));
        }

        @Test
        @DisplayName("Should fail when description exceeds max length")
        void shouldFailWhenDescriptionExceedsMaxLength() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setDescription("A".repeat(1001));

            Set<ConstraintViolation<UpdatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should pass with valid fields")
        void shouldPassWithValidFields() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();
            request.setName("Valid Name");
            request.setOrganization("Valid Organization");
            request.setDescription("Valid Description");

            Set<ConstraintViolation<UpdatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass with null fields")
        void shouldPassWithNullFields() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();

            Set<ConstraintViolation<UpdatePromoterProfileRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set all fields correctly")
        void shouldGetAndSetAllFieldsCorrectly() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest();

            request.setName("Test Name");
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

        @Test
        @DisplayName("Should create with all args constructor")
        void shouldCreateWithAllArgsConstructor() {
            UpdatePromoterProfileRequest request = new UpdatePromoterProfileRequest(
                    "Name", "Org", "Desc", "Phone", "Web", "Addr",
                    "Logo", "Type", "Area", "Year", "Emp", "Social"
            );

            assertThat(request.getName()).isEqualTo("Name");
            assertThat(request.getOrganization()).isEqualTo("Org");
            assertThat(request.getDescription()).isEqualTo("Desc");
            assertThat(request.hasUpdates()).isTrue();
        }
    }
}
