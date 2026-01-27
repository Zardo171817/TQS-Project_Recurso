package com.example.demo.dto;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ver Resgates Feitos Parceiro - DTO Tests")
class VerResgatesFeitosParceiroDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("BenefitResponse Tests")
    class BenefitResponseTests {

        @Test
        @DisplayName("Should create from entity correctly")
        void shouldCreateFromEntityCorrectly() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Desconto Cinema");
            benefit.setDescription("20% desconto");
            benefit.setPointsRequired(150);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Cinema NOS");
            benefit.setImageUrl("http://img.jpg");
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 30));

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Desconto Cinema", response.getName());
            assertEquals("20% desconto", response.getDescription());
            assertEquals(150, response.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
            assertEquals("Cinema NOS", response.getProvider());
            assertEquals("http://img.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertEquals(LocalDateTime.of(2024, 6, 15, 10, 30), response.getCreatedAt());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            BenefitResponse response = BenefitResponse.fromEntity(null);
            assertNull(response);
        }

        @Test
        @DisplayName("Should handle entity with null imageUrl")
        void shouldHandleEntityWithNullImageUrl() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Test");
            benefit.setDescription("Test desc");
            benefit.setPointsRequired(100);
            benefit.setCategory(BenefitCategory.UA);
            benefit.setProvider("UA");
            benefit.setActive(true);

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertNotNull(response);
            assertNull(response.getImageUrl());
        }

        @Test
        @DisplayName("Should create with no-args constructor and setters")
        void shouldCreateWithNoArgsConstructorAndSetters() {
            BenefitResponse response = new BenefitResponse();
            response.setId(5L);
            response.setName("Test Name");
            response.setDescription("Test Desc");
            response.setPointsRequired(200);
            response.setCategory(BenefitCategory.PARTNER);
            response.setProvider("Test Provider");
            response.setImageUrl("http://test.jpg");
            response.setActive(true);
            response.setCreatedAt(LocalDateTime.now());

            assertEquals(5L, response.getId());
            assertEquals("Test Name", response.getName());
            assertEquals("Test Desc", response.getDescription());
            assertEquals(200, response.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
            assertEquals("Test Provider", response.getProvider());
            assertEquals("http://test.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertNotNull(response.getCreatedAt());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            BenefitResponse response = new BenefitResponse(
                    1L, "Name", "Desc", 100, BenefitCategory.PARTNER, "Provider", "http://img.jpg", true, now
            );

            assertEquals(1L, response.getId());
            assertEquals("Name", response.getName());
            assertEquals(100, response.getPointsRequired());
            assertEquals(now, response.getCreatedAt());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            BenefitResponse r1 = new BenefitResponse();
            r1.setId(1L);
            r1.setName("Test");

            BenefitResponse r2 = new BenefitResponse();
            r2.setId(1L);
            r2.setName("Test");

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            BenefitResponse response = new BenefitResponse();
            response.setId(1L);
            response.setName("Test");

            String str = response.toString();
            assertNotNull(str);
            assertTrue(str.contains("Test"));
        }
    }

    @Nested
    @DisplayName("CreateBenefitRequest Tests")
    class CreateBenefitRequestTests {

        @Test
        @DisplayName("Should validate valid request")
        void shouldValidateValidRequest() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "Description", 100, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void shouldFailWhenNameIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "", "Description", 100, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void shouldFailWhenNameIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    null, "Description", 100, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when description is blank")
        void shouldFailWhenDescriptionIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Name", "", 100, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is null")
        void shouldFailWhenPointsIsNull() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Name", "Desc", null, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is zero")
        void shouldFailWhenPointsIsZero() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Name", "Desc", 0, "Provider", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when provider is blank")
        void shouldFailWhenProviderIsBlank() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Name", "Desc", 100, "", null
            );

            Set<ConstraintViolation<CreateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should get and set all fields")
        void shouldGetAndSetAllFields() {
            CreateBenefitRequest request = new CreateBenefitRequest();
            request.setName("Test Name");
            request.setDescription("Test Desc");
            request.setPointsRequired(200);
            request.setProvider("Test Provider");
            request.setImageUrl("http://img.jpg");

            assertEquals("Test Name", request.getName());
            assertEquals("Test Desc", request.getDescription());
            assertEquals(200, request.getPointsRequired());
            assertEquals("Test Provider", request.getProvider());
            assertEquals("http://img.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            CreateBenefitRequest r1 = new CreateBenefitRequest("Name", "Desc", 100, "Provider", null);
            CreateBenefitRequest r2 = new CreateBenefitRequest("Name", "Desc", 100, "Provider", null);

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            CreateBenefitRequest request = new CreateBenefitRequest("Name", "Desc", 100, "Provider", null);
            assertNotNull(request.toString());
            assertTrue(request.toString().contains("Name"));
        }
    }

    @Nested
    @DisplayName("UpdateBenefitRequest Tests")
    class UpdateBenefitRequestTests {

        @Test
        @DisplayName("Should validate valid request")
        void shouldValidateValidRequest() {
            UpdateBenefitRequest request = new UpdateBenefitRequest(
                    "Updated Name", "Updated Desc", 200, "Updated Provider", "http://new.jpg"
            );

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should validate request with null fields")
        void shouldValidateRequestWithNullFields() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            // All fields are optional for update

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when pointsRequired is less than 1")
        void shouldFailWhenPointsLessThan1() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setPointsRequired(0);

            Set<ConstraintViolation<UpdateBenefitRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should get and set all fields")
        void shouldGetAndSetAllFields() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Updated");
            request.setDescription("Updated desc");
            request.setPointsRequired(300);
            request.setProvider("New Provider");
            request.setImageUrl("http://new.jpg");

            assertEquals("Updated", request.getName());
            assertEquals("Updated desc", request.getDescription());
            assertEquals(300, request.getPointsRequired());
            assertEquals("New Provider", request.getProvider());
            assertEquals("http://new.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            UpdateBenefitRequest r1 = new UpdateBenefitRequest("Name", "Desc", 100, "Provider", null);
            UpdateBenefitRequest r2 = new UpdateBenefitRequest("Name", "Desc", 100, "Provider", null);

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            UpdateBenefitRequest request = new UpdateBenefitRequest("Name", null, null, null, null);
            assertNotNull(request.toString());
            assertTrue(request.toString().contains("Name"));
        }
    }

    @Nested
    @DisplayName("RedemptionResponse Tests")
    class RedemptionResponseTests {

        @Test
        @DisplayName("Should create from entity correctly")
        void shouldCreateFromEntityCorrectly() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setName("Maria");
            volunteer.setEmail("maria@email.com");
            volunteer.setTotalPoints(350);

            Benefit benefit = new Benefit();
            benefit.setId(2L);
            benefit.setName("Desconto Cinema");
            benefit.setDescription("20% desconto");
            benefit.setProvider("Cinema NOS");

            Redemption redemption = new Redemption();
            redemption.setId(10L);
            redemption.setVolunteer(volunteer);
            redemption.setBenefit(benefit);
            redemption.setPointsSpent(150);
            redemption.setStatus(RedemptionStatus.COMPLETED);
            redemption.setRedeemedAt(LocalDateTime.of(2024, 7, 1, 14, 0));

            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertEquals(10L, response.getId());
            assertEquals(1L, response.getVolunteerId());
            assertEquals("Maria", response.getVolunteerName());
            assertEquals("maria@email.com", response.getVolunteerEmail());
            assertEquals(2L, response.getBenefitId());
            assertEquals("Desconto Cinema", response.getBenefitName());
            assertEquals("20% desconto", response.getBenefitDescription());
            assertEquals("Cinema NOS", response.getBenefitProvider());
            assertEquals(150, response.getPointsSpent());
            assertEquals(RedemptionStatus.COMPLETED, response.getStatus());
            assertEquals(350, response.getRemainingPoints());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            assertNull(RedemptionResponse.fromEntity(null));
        }

        @Test
        @DisplayName("Should handle entity with null volunteer")
        void shouldHandleEntityWithNullVolunteer() {
            Redemption redemption = new Redemption();
            redemption.setId(1L);
            redemption.setPointsSpent(100);
            redemption.setStatus(RedemptionStatus.COMPLETED);
            redemption.setRedeemedAt(LocalDateTime.now());

            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertNull(response.getVolunteerId());
            assertNull(response.getVolunteerName());
        }

        @Test
        @DisplayName("Should handle entity with null benefit")
        void shouldHandleEntityWithNullBenefit() {
            Volunteer vol = new Volunteer();
            vol.setId(1L);
            vol.setName("Test");
            vol.setEmail("test@email.com");
            vol.setTotalPoints(100);

            Redemption redemption = new Redemption();
            redemption.setId(1L);
            redemption.setVolunteer(vol);
            redemption.setPointsSpent(100);
            redemption.setStatus(RedemptionStatus.COMPLETED);
            redemption.setRedeemedAt(LocalDateTime.now());

            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertNull(response.getBenefitId());
            assertNull(response.getBenefitName());
        }
    }

    @Nested
    @DisplayName("BenefitRedemptionDetailResponse Tests")
    class BenefitRedemptionDetailResponseTests {

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse(
                    1L, "Desconto Cinema", "20% desconto", 150, "Cinema NOS", true, 5L, 750L
            );

            assertEquals(1L, detail.getBenefitId());
            assertEquals("Desconto Cinema", detail.getBenefitName());
            assertEquals("20% desconto", detail.getBenefitDescription());
            assertEquals(150, detail.getPointsRequired());
            assertEquals("Cinema NOS", detail.getProvider());
            assertTrue(detail.getActive());
            assertEquals(5L, detail.getTotalRedemptions());
            assertEquals(750L, detail.getTotalPointsRedeemed());
        }

        @Test
        @DisplayName("Should create with no-args constructor and setters")
        void shouldCreateWithNoArgsConstructorAndSetters() {
            BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse();
            detail.setBenefitId(2L);
            detail.setBenefitName("Voucher");
            detail.setBenefitDescription("Voucher 10 euros");
            detail.setPointsRequired(300);
            detail.setProvider("Restaurante");
            detail.setActive(false);
            detail.setTotalRedemptions(3L);
            detail.setTotalPointsRedeemed(900L);

            assertEquals(2L, detail.getBenefitId());
            assertEquals("Voucher", detail.getBenefitName());
            assertEquals(300, detail.getPointsRequired());
            assertFalse(detail.getActive());
            assertEquals(3L, detail.getTotalRedemptions());
            assertEquals(900L, detail.getTotalPointsRedeemed());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            BenefitRedemptionDetailResponse d1 = new BenefitRedemptionDetailResponse(
                    1L, "Name", "Desc", 100, "Provider", true, 5L, 500L
            );
            BenefitRedemptionDetailResponse d2 = new BenefitRedemptionDetailResponse(
                    1L, "Name", "Desc", 100, "Provider", true, 5L, 500L
            );

            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }
    }

    @Nested
    @DisplayName("PartnerRedemptionStatsResponse Tests")
    class PartnerRedemptionStatsResponseTests {

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse(
                    1L, "Name", "Desc", 100, "Provider", true, 5L, 500L
            );
            RedemptionResponse redemption = new RedemptionResponse();
            redemption.setId(1L);

            PartnerRedemptionStatsResponse stats = new PartnerRedemptionStatsResponse(
                    "Cinema NOS", 2, 10L, 1500L,
                    Arrays.asList(detail), Arrays.asList(redemption)
            );

            assertEquals("Cinema NOS", stats.getProvider());
            assertEquals(2, stats.getTotalBenefits());
            assertEquals(10L, stats.getTotalRedemptions());
            assertEquals(1500L, stats.getTotalPointsRedeemed());
            assertEquals(1, stats.getBenefitDetails().size());
            assertEquals(1, stats.getRecentRedemptions().size());
        }

        @Test
        @DisplayName("Should create with no-args constructor and setters")
        void shouldCreateWithNoArgsConstructorAndSetters() {
            PartnerRedemptionStatsResponse stats = new PartnerRedemptionStatsResponse();
            stats.setProvider("Test");
            stats.setTotalBenefits(3);
            stats.setTotalRedemptions(15L);
            stats.setTotalPointsRedeemed(2000L);
            stats.setBenefitDetails(Arrays.asList());
            stats.setRecentRedemptions(Arrays.asList());

            assertEquals("Test", stats.getProvider());
            assertEquals(3, stats.getTotalBenefits());
            assertEquals(15L, stats.getTotalRedemptions());
            assertEquals(2000L, stats.getTotalPointsRedeemed());
            assertTrue(stats.getBenefitDetails().isEmpty());
            assertTrue(stats.getRecentRedemptions().isEmpty());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            PartnerRedemptionStatsResponse s1 = new PartnerRedemptionStatsResponse(
                    "Provider", 1, 5L, 500L, Arrays.asList(), Arrays.asList()
            );
            PartnerRedemptionStatsResponse s2 = new PartnerRedemptionStatsResponse(
                    "Provider", 1, 5L, 500L, Arrays.asList(), Arrays.asList()
            );

            assertEquals(s1, s2);
            assertEquals(s1.hashCode(), s2.hashCode());
        }
    }
}
