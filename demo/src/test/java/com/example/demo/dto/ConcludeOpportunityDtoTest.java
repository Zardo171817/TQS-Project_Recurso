package com.example.demo.dto;

import com.example.demo.entity.OpportunityStatus;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Conclude Opportunity DTO Tests")
class ConcludeOpportunityDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("ConfirmParticipationRequest Tests")
    class ConfirmParticipationRequestTests {

        @Test
        @DisplayName("Should create valid request with all fields")
        void createRequest_AllFields_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));

            assertThat(request.getPromoterId()).isEqualTo(1L);
            assertThat(request.getApplicationIds()).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("Should create request with no-args constructor")
        void createRequest_NoArgs_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest();
            request.setPromoterId(1L);
            request.setApplicationIds(Collections.singletonList(1L));

            assertThat(request.getPromoterId()).isEqualTo(1L);
            assertThat(request.getApplicationIds()).hasSize(1);
        }

        @Test
        @DisplayName("Should fail validation when promoter ID is null")
        void validateRequest_NullPromoterId_Fails() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(null, Collections.singletonList(1L));

            Set<ConstraintViolation<ConfirmParticipationRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Promoter ID is required");
        }

        @Test
        @DisplayName("Should fail validation when application IDs is null")
        void validateRequest_NullApplicationIds_Fails() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, null);

            Set<ConstraintViolation<ConfirmParticipationRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Application IDs are required");
        }

        @Test
        @DisplayName("Should pass validation with empty application IDs list")
        void validateRequest_EmptyApplicationIds_Passes() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.emptyList());

            Set<ConstraintViolation<ConfirmParticipationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle multiple application IDs")
        void createRequest_MultipleIds_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L, 3L, 4L, 5L));

            assertThat(request.getApplicationIds()).hasSize(5);
            assertThat(request.getApplicationIds()).containsExactly(1L, 2L, 3L, 4L, 5L);
        }

    }

    @Nested
    @DisplayName("ConcludeOpportunityResponse Tests")
    class ConcludeOpportunityResponseTests {

        @Test
        @DisplayName("Should create response with all fields")
        void createResponse_AllFields_Success() {
            LocalDateTime now = LocalDateTime.now();
            ConcludeOpportunityResponse.ParticipantSummary participant =
                new ConcludeOpportunityResponse.ParticipantSummary(1L, "John", "john@test.com", 100, 200);

            ConcludeOpportunityResponse response = new ConcludeOpportunityResponse(
                    1L, "Beach Cleanup", OpportunityStatus.CONCLUDED, now, 1, 100, Collections.singletonList(participant));

            assertThat(response.getOpportunityId()).isEqualTo(1L);
            assertThat(response.getOpportunityTitle()).isEqualTo("Beach Cleanup");
            assertThat(response.getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
            assertThat(response.getConcludedAt()).isEqualTo(now);
            assertThat(response.getTotalParticipantsConfirmed()).isEqualTo(1);
            assertThat(response.getTotalPointsAwarded()).isEqualTo(100);
            assertThat(response.getConfirmedParticipants()).hasSize(1);
        }

        @Test
        @DisplayName("Should create response with no-args constructor")
        void createResponse_NoArgs_Success() {
            ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
            response.setOpportunityId(1L);
            response.setOpportunityTitle("Test");
            response.setStatus(OpportunityStatus.CONCLUDED);

            assertThat(response.getOpportunityId()).isEqualTo(1L);
            assertThat(response.getOpportunityTitle()).isEqualTo("Test");
            assertThat(response.getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        }

        @Test
        @DisplayName("Should handle empty confirmed participants list")
        void createResponse_EmptyParticipants_Success() {
            ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
            response.setConfirmedParticipants(Collections.emptyList());

            assertThat(response.getConfirmedParticipants()).isEmpty();
        }

        @Test
        @DisplayName("Should handle multiple participants in response")
        void createResponse_MultipleParticipants_Success() {
            ConcludeOpportunityResponse.ParticipantSummary p1 =
                new ConcludeOpportunityResponse.ParticipantSummary(1L, "John", "john@test.com", 100, 200);
            ConcludeOpportunityResponse.ParticipantSummary p2 =
                new ConcludeOpportunityResponse.ParticipantSummary(2L, "Jane", "jane@test.com", 100, 300);

            ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
            response.setConfirmedParticipants(Arrays.asList(p1, p2));
            response.setTotalParticipantsConfirmed(2);
            response.setTotalPointsAwarded(200);

            assertThat(response.getConfirmedParticipants()).hasSize(2);
            assertThat(response.getTotalParticipantsConfirmed()).isEqualTo(2);
            assertThat(response.getTotalPointsAwarded()).isEqualTo(200);
        }

    }

    @Nested
    @DisplayName("ParticipantSummary Tests")
    class ParticipantSummaryTests {

        @Test
        @DisplayName("Should create participant summary with all fields")
        void createSummary_AllFields_Success() {
            ConcludeOpportunityResponse.ParticipantSummary summary =
                new ConcludeOpportunityResponse.ParticipantSummary(1L, "John Doe", "john@test.com", 100, 500);

            assertThat(summary.getVolunteerId()).isEqualTo(1L);
            assertThat(summary.getVolunteerName()).isEqualTo("John Doe");
            assertThat(summary.getVolunteerEmail()).isEqualTo("john@test.com");
            assertThat(summary.getPointsAwarded()).isEqualTo(100);
            assertThat(summary.getTotalPoints()).isEqualTo(500);
        }

        @Test
        @DisplayName("Should create participant summary with no-args constructor")
        void createSummary_NoArgs_Success() {
            ConcludeOpportunityResponse.ParticipantSummary summary = new ConcludeOpportunityResponse.ParticipantSummary();
            summary.setVolunteerId(1L);
            summary.setVolunteerName("Test");
            summary.setVolunteerEmail("test@test.com");
            summary.setPointsAwarded(50);
            summary.setTotalPoints(100);

            assertThat(summary.getVolunteerId()).isEqualTo(1L);
            assertThat(summary.getVolunteerName()).isEqualTo("Test");
        }

        @Test
        @DisplayName("Should handle different points values")
        void createSummary_DifferentPoints_Success() {
            ConcludeOpportunityResponse.ParticipantSummary summary = new ConcludeOpportunityResponse.ParticipantSummary();
            summary.setPointsAwarded(0);
            summary.setTotalPoints(0);

            assertThat(summary.getPointsAwarded()).isZero();
            assertThat(summary.getTotalPoints()).isZero();
        }

        @Test
        @DisplayName("Should handle high points values")
        void createSummary_HighPoints_Success() {
            ConcludeOpportunityResponse.ParticipantSummary summary =
                new ConcludeOpportunityResponse.ParticipantSummary(1L, "John", "john@test.com", 10000, 50000);

            assertThat(summary.getPointsAwarded()).isEqualTo(10000);
            assertThat(summary.getTotalPoints()).isEqualTo(50000);
        }

    }

    @Nested
    @DisplayName("ConcludeOpportunityRequest Tests")
    class ConcludeOpportunityRequestTests {

        @Test
        @DisplayName("Should create request with promoter ID")
        void createRequest_WithPromoterId_Success() {
            ConcludeOpportunityRequest request = new ConcludeOpportunityRequest(1L);

            assertThat(request.getPromoterId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should create request with no-args constructor")
        void createRequest_NoArgs_Success() {
            ConcludeOpportunityRequest request = new ConcludeOpportunityRequest();
            request.setPromoterId(5L);

            assertThat(request.getPromoterId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should fail validation when promoter ID is null")
        void validateRequest_NullPromoterId_Fails() {
            ConcludeOpportunityRequest request = new ConcludeOpportunityRequest(null);

            Set<ConstraintViolation<ConcludeOpportunityRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Promoter ID is required");
        }

        @Test
        @DisplayName("Should pass validation with valid promoter ID")
        void validateRequest_ValidPromoterId_Passes() {
            ConcludeOpportunityRequest request = new ConcludeOpportunityRequest(1L);

            Set<ConstraintViolation<ConcludeOpportunityRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

    }

    @Nested
    @DisplayName("VolunteerPointsResponse Tests")
    class VolunteerPointsResponseTests {

        @Test
        @DisplayName("Should map volunteer entity to VolunteerPointsResponse correctly")
        void fromEntity_MapsAllFields_Success() {
            com.example.demo.entity.Volunteer volunteer = new com.example.demo.entity.Volunteer();
            volunteer.setId(1L);
            volunteer.setName("John Doe");
            volunteer.setEmail("john@test.com");
            volunteer.setTotalPoints(250);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("John Doe");
            assertThat(response.getEmail()).isEqualTo("john@test.com");
            assertThat(response.getTotalPoints()).isEqualTo(250);
        }
    }

    @Nested
    @DisplayName("VolunteerResponse Tests")
    class VolunteerResponseTests {

        @Test
        @DisplayName("Should map volunteer entity to VolunteerResponse including totalPoints")
        void fromEntity_IncludesTotalPoints_Success() {
            com.example.demo.entity.Volunteer volunteer = new com.example.demo.entity.Volunteer();
            volunteer.setId(1L);
            volunteer.setName("Jane Doe");
            volunteer.setEmail("jane@test.com");
            volunteer.setPhone("123456789");
            volunteer.setSkills("Organizing");
            volunteer.setTotalPoints(500);

            VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Jane Doe");
            assertThat(response.getEmail()).isEqualTo("jane@test.com");
            assertThat(response.getPhone()).isEqualTo("123456789");
            assertThat(response.getSkills()).isEqualTo("Organizing");
            assertThat(response.getTotalPoints()).isEqualTo(500);
        }
    }
}
