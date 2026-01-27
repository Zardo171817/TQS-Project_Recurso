package com.example.demo.dto;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RedemptionResponse DTO Tests")
class RedemptionResponseTest {

    private Redemption redemption;
    private Volunteer volunteer;
    private Benefit benefit;
    private RedemptionResponse redemptionResponse;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Joao Silva");
        volunteer.setEmail("joao@example.com");
        volunteer.setTotalPoints(400);

        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setName("Desconto Cantina UA");
        benefit.setDescription("10% desconto na cantina da UA");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.UA);
        benefit.setProvider("Universidade de Aveiro");
        benefit.setActive(true);
        benefit.setCreatedAt(LocalDateTime.now());

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefit);
        redemption.setPointsSpent(100);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.of(2024, 6, 15, 10, 30, 0));

        redemptionResponse = new RedemptionResponse();
        redemptionResponse.setId(1L);
        redemptionResponse.setVolunteerId(1L);
        redemptionResponse.setVolunteerName("Joao Silva");
        redemptionResponse.setVolunteerEmail("joao@example.com");
        redemptionResponse.setBenefitId(1L);
        redemptionResponse.setBenefitName("Desconto Cantina UA");
        redemptionResponse.setBenefitDescription("10% desconto na cantina da UA");
        redemptionResponse.setBenefitProvider("Universidade de Aveiro");
        redemptionResponse.setPointsSpent(100);
        redemptionResponse.setStatus(RedemptionStatus.COMPLETED);
        redemptionResponse.setRedeemedAt(LocalDateTime.of(2024, 6, 15, 10, 30, 0));
        redemptionResponse.setRemainingPoints(400);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with no-args constructor")
        void shouldCreateInstanceWithNoArgsConstructor() {
            RedemptionResponse response = new RedemptionResponse();
            assertNotNull(response);
        }

        @Test
        @DisplayName("Should create instance with all-args constructor")
        void shouldCreateInstanceWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            RedemptionResponse response = new RedemptionResponse(
                    1L, 1L, "Joao", "joao@example.com",
                    1L, "Benefit", "Description", "Provider",
                    100, RedemptionStatus.COMPLETED, now, 400
            );

            assertEquals(1L, response.getId());
            assertEquals(1L, response.getVolunteerId());
            assertEquals("Joao", response.getVolunteerName());
            assertEquals("joao@example.com", response.getVolunteerEmail());
            assertEquals(1L, response.getBenefitId());
            assertEquals("Benefit", response.getBenefitName());
            assertEquals("Description", response.getBenefitDescription());
            assertEquals("Provider", response.getBenefitProvider());
            assertEquals(100, response.getPointsSpent());
            assertEquals(RedemptionStatus.COMPLETED, response.getStatus());
            assertEquals(now, response.getRedeemedAt());
            assertEquals(400, response.getRemainingPoints());
        }

        @Test
        @DisplayName("Should create instance with null values in all-args constructor")
        void shouldCreateInstanceWithNullValues() {
            RedemptionResponse response = new RedemptionResponse(
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null
            );

            assertNull(response.getId());
            assertNull(response.getVolunteerId());
            assertNull(response.getVolunteerName());
            assertNull(response.getPointsSpent());
            assertNull(response.getStatus());
        }
    }

    @Nested
    @DisplayName("fromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should correctly map entity to response")
        void shouldCorrectlyMapEntityToResponse() {
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertEquals(redemption.getId(), response.getId());
            assertEquals(redemption.getPointsSpent(), response.getPointsSpent());
            assertEquals(redemption.getStatus(), response.getStatus());
            assertEquals(redemption.getRedeemedAt(), response.getRedeemedAt());
        }

        @Test
        @DisplayName("Should map volunteer data correctly")
        void shouldMapVolunteerDataCorrectly() {
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertEquals(volunteer.getId(), response.getVolunteerId());
            assertEquals(volunteer.getName(), response.getVolunteerName());
            assertEquals(volunteer.getEmail(), response.getVolunteerEmail());
            assertEquals(volunteer.getTotalPoints(), response.getRemainingPoints());
        }

        @Test
        @DisplayName("Should map benefit data correctly")
        void shouldMapBenefitDataCorrectly() {
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertEquals(benefit.getId(), response.getBenefitId());
            assertEquals(benefit.getName(), response.getBenefitName());
            assertEquals(benefit.getDescription(), response.getBenefitDescription());
            assertEquals(benefit.getProvider(), response.getBenefitProvider());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            RedemptionResponse response = RedemptionResponse.fromEntity(null);
            assertNull(response);
        }

        @Test
        @DisplayName("Should handle entity with null volunteer")
        void shouldHandleEntityWithNullVolunteer() {
            redemption.setVolunteer(null);
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertNull(response.getVolunteerId());
            assertNull(response.getVolunteerName());
            assertNull(response.getVolunteerEmail());
            assertNull(response.getRemainingPoints());
        }

        @Test
        @DisplayName("Should handle entity with null benefit")
        void shouldHandleEntityWithNullBenefit() {
            redemption.setBenefit(null);
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);

            assertNotNull(response);
            assertNull(response.getBenefitId());
            assertNull(response.getBenefitName());
            assertNull(response.getBenefitDescription());
            assertNull(response.getBenefitProvider());
        }

        @Test
        @DisplayName("Should map COMPLETED status correctly")
        void shouldMapCompletedStatusCorrectly() {
            redemption.setStatus(RedemptionStatus.COMPLETED);
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);
            assertEquals(RedemptionStatus.COMPLETED, response.getStatus());
        }

        @Test
        @DisplayName("Should map CANCELLED status correctly")
        void shouldMapCancelledStatusCorrectly() {
            redemption.setStatus(RedemptionStatus.CANCELLED);
            RedemptionResponse response = RedemptionResponse.fromEntity(redemption);
            assertEquals(RedemptionStatus.CANCELLED, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            redemptionResponse.setId(100L);
            assertEquals(100L, redemptionResponse.getId());
        }

        @Test
        @DisplayName("Should get and set volunteerId")
        void shouldGetAndSetVolunteerId() {
            redemptionResponse.setVolunteerId(2L);
            assertEquals(2L, redemptionResponse.getVolunteerId());
        }

        @Test
        @DisplayName("Should get and set volunteerName")
        void shouldGetAndSetVolunteerName() {
            redemptionResponse.setVolunteerName("Maria Santos");
            assertEquals("Maria Santos", redemptionResponse.getVolunteerName());
        }

        @Test
        @DisplayName("Should get and set volunteerEmail")
        void shouldGetAndSetVolunteerEmail() {
            redemptionResponse.setVolunteerEmail("maria@example.com");
            assertEquals("maria@example.com", redemptionResponse.getVolunteerEmail());
        }

        @Test
        @DisplayName("Should get and set benefitId")
        void shouldGetAndSetBenefitId() {
            redemptionResponse.setBenefitId(2L);
            assertEquals(2L, redemptionResponse.getBenefitId());
        }

        @Test
        @DisplayName("Should get and set benefitName")
        void shouldGetAndSetBenefitName() {
            redemptionResponse.setBenefitName("New Benefit");
            assertEquals("New Benefit", redemptionResponse.getBenefitName());
        }

        @Test
        @DisplayName("Should get and set pointsSpent")
        void shouldGetAndSetPointsSpent() {
            redemptionResponse.setPointsSpent(500);
            assertEquals(500, redemptionResponse.getPointsSpent());
        }

        @Test
        @DisplayName("Should get and set status")
        void shouldGetAndSetStatus() {
            redemptionResponse.setStatus(RedemptionStatus.CANCELLED);
            assertEquals(RedemptionStatus.CANCELLED, redemptionResponse.getStatus());
        }

        @Test
        @DisplayName("Should get and set redeemedAt")
        void shouldGetAndSetRedeemedAt() {
            LocalDateTime newDate = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
            redemptionResponse.setRedeemedAt(newDate);
            assertEquals(newDate, redemptionResponse.getRedeemedAt());
        }

        @Test
        @DisplayName("Should get and set remainingPoints")
        void shouldGetAndSetRemainingPoints() {
            redemptionResponse.setRemainingPoints(200);
            assertEquals(200, redemptionResponse.getRemainingPoints());
        }

        @Test
        @DisplayName("Should get and set benefitDescription")
        void shouldGetAndSetBenefitDescription() {
            redemptionResponse.setBenefitDescription("New Description");
            assertEquals("New Description", redemptionResponse.getBenefitDescription());
        }

        @Test
        @DisplayName("Should get and set benefitProvider")
        void shouldGetAndSetBenefitProvider() {
            redemptionResponse.setBenefitProvider("New Provider");
            assertEquals("New Provider", redemptionResponse.getBenefitProvider());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            LocalDateTime date = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
            RedemptionResponse response1 = new RedemptionResponse(
                    1L, 1L, "Joao", "joao@example.com",
                    1L, "Benefit", "Desc", "Provider",
                    100, RedemptionStatus.COMPLETED, date, 400
            );
            RedemptionResponse response2 = new RedemptionResponse(
                    1L, 1L, "Joao", "joao@example.com",
                    1L, "Benefit", "Desc", "Provider",
                    100, RedemptionStatus.COMPLETED, date, 400
            );

            assertEquals(response1, response2);
            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            LocalDateTime date = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
            RedemptionResponse response1 = new RedemptionResponse(
                    1L, 1L, "Joao", "joao@example.com",
                    1L, "Benefit", "Desc", "Provider",
                    100, RedemptionStatus.COMPLETED, date, 400
            );
            RedemptionResponse response2 = new RedemptionResponse(
                    2L, 1L, "Joao", "joao@example.com",
                    1L, "Benefit", "Desc", "Provider",
                    100, RedemptionStatus.COMPLETED, date, 400
            );

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(redemptionResponse, redemptionResponse);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, redemptionResponse);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", redemptionResponse);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = redemptionResponse.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Joao Silva"));
            assertTrue(toString.contains("Desconto Cantina UA"));
            assertTrue(toString.contains("100"));
            assertTrue(toString.contains("COMPLETED"));
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            RedemptionResponse response = new RedemptionResponse();
            String toString = response.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum integer value for points")
        void shouldHandleMaximumIntegerValueForPoints() {
            redemptionResponse.setPointsSpent(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, redemptionResponse.getPointsSpent());
        }

        @Test
        @DisplayName("Should handle zero points")
        void shouldHandleZeroPoints() {
            redemptionResponse.setPointsSpent(0);
            assertEquals(0, redemptionResponse.getPointsSpent());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            redemptionResponse.setVolunteerName("");
            redemptionResponse.setBenefitName("");
            assertEquals("", redemptionResponse.getVolunteerName());
            assertEquals("", redemptionResponse.getBenefitName());
        }

        @Test
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            String specialName = "Maria da Conceicao @#$%";
            redemptionResponse.setVolunteerName(specialName);
            assertEquals(specialName, redemptionResponse.getVolunteerName());
        }

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for id")
        void shouldHandleLongMaxValueForId() {
            redemptionResponse.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, redemptionResponse.getId());
        }

        @Test
        @DisplayName("Should handle minimum date")
        void shouldHandleMinimumDate() {
            LocalDateTime minDate = LocalDateTime.MIN;
            redemptionResponse.setRedeemedAt(minDate);
            assertEquals(minDate, redemptionResponse.getRedeemedAt());
        }

        @Test
        @DisplayName("Should handle zero remaining points")
        void shouldHandleZeroRemainingPoints() {
            redemptionResponse.setRemainingPoints(0);
            assertEquals(0, redemptionResponse.getRemainingPoints());
        }
    }
}
