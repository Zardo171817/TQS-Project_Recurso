package com.example.demo.dto;

import com.example.demo.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PointsHistoryResponse DTO Tests")
class PointsHistoryResponseTest {

    private Volunteer volunteer;
    private Promoter promoter;
    private Opportunity opportunity;
    private Application application;

    @BeforeEach
    void setUp() {
        // Setup Volunteer
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Spring");
        volunteer.setTotalPoints(100);

        // Setup Promoter
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Organization ABC");
        promoter.setEmail("org@example.com");
        promoter.setOrganization("ABC Org");

        // Setup Opportunity
        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Help at Community Center");
        opportunity.setDescription("Assist with community activities and events");
        opportunity.setSkills("Communication, Teamwork");
        opportunity.setCategory("Community");
        opportunity.setDuration(4);
        opportunity.setVacancies(5);
        opportunity.setPoints(50);
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now().minusDays(10));
        opportunity.setConcludedAt(LocalDateTime.now().minusDays(1));

        // Setup Application
        application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("I want to help");
        application.setAppliedAt(LocalDateTime.now().minusDays(5));
        application.setParticipationConfirmed(true);
        application.setPointsAwarded(50);
        application.setConfirmedAt(LocalDateTime.now().minusDays(1));
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create PointsHistoryResponse with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            PointsHistoryResponse response = new PointsHistoryResponse();

            assertNotNull(response);
            assertNull(response.getApplicationId());
            assertNull(response.getOpportunityId());
            assertNull(response.getOpportunityTitle());
            assertNull(response.getOpportunityCategory());
            assertNull(response.getPointsAwarded());
            assertNull(response.getConfirmedAt());
            assertNull(response.getOpportunityDescription());
        }

        @Test
        @DisplayName("Should create PointsHistoryResponse with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            LocalDateTime confirmedAt = LocalDateTime.now();
            PointsHistoryResponse response = new PointsHistoryResponse(
                    1L, 2L, "Test Title", "Test Category", 100, confirmedAt, "Test Description"
            );

            assertNotNull(response);
            assertEquals(1L, response.getApplicationId());
            assertEquals(2L, response.getOpportunityId());
            assertEquals("Test Title", response.getOpportunityTitle());
            assertEquals("Test Category", response.getOpportunityCategory());
            assertEquals(100, response.getPointsAwarded());
            assertEquals(confirmedAt, response.getConfirmedAt());
            assertEquals("Test Description", response.getOpportunityDescription());
        }
    }

    @Nested
    @DisplayName("fromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should correctly map Application entity to PointsHistoryResponse")
        void shouldMapApplicationToResponse() {
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertNotNull(response);
            assertEquals(application.getId(), response.getApplicationId());
            assertEquals(opportunity.getId(), response.getOpportunityId());
            assertEquals(opportunity.getTitle(), response.getOpportunityTitle());
            assertEquals(opportunity.getCategory(), response.getOpportunityCategory());
            assertEquals(application.getPointsAwarded(), response.getPointsAwarded());
            assertEquals(application.getConfirmedAt(), response.getConfirmedAt());
            assertEquals(opportunity.getDescription(), response.getOpportunityDescription());
        }

        @Test
        @DisplayName("Should handle application with zero points")
        void shouldHandleZeroPoints() {
            application.setPointsAwarded(0);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(0, response.getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle application with null confirmedAt")
        void shouldHandleNullConfirmedAt() {
            application.setConfirmedAt(null);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertNull(response.getConfirmedAt());
        }

        @Test
        @DisplayName("Should handle opportunity with empty description")
        void shouldHandleEmptyDescription() {
            opportunity.setDescription("");
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals("", response.getOpportunityDescription());
        }

        @Test
        @DisplayName("Should handle opportunity with long description")
        void shouldHandleLongDescription() {
            String longDescription = "A".repeat(1000);
            opportunity.setDescription(longDescription);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(longDescription, response.getOpportunityDescription());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should set and get applicationId")
        void shouldSetAndGetApplicationId() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setApplicationId(999L);
            assertEquals(999L, response.getApplicationId());
        }

        @Test
        @DisplayName("Should set and get opportunityId")
        void shouldSetAndGetOpportunityId() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setOpportunityId(888L);
            assertEquals(888L, response.getOpportunityId());
        }

        @Test
        @DisplayName("Should set and get opportunityTitle")
        void shouldSetAndGetOpportunityTitle() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setOpportunityTitle("New Title");
            assertEquals("New Title", response.getOpportunityTitle());
        }

        @Test
        @DisplayName("Should set and get opportunityCategory")
        void shouldSetAndGetOpportunityCategory() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setOpportunityCategory("Education");
            assertEquals("Education", response.getOpportunityCategory());
        }

        @Test
        @DisplayName("Should set and get pointsAwarded")
        void shouldSetAndGetPointsAwarded() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setPointsAwarded(250);
            assertEquals(250, response.getPointsAwarded());
        }

        @Test
        @DisplayName("Should set and get confirmedAt")
        void shouldSetAndGetConfirmedAt() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            LocalDateTime now = LocalDateTime.now();
            response.setConfirmedAt(now);
            assertEquals(now, response.getConfirmedAt());
        }

        @Test
        @DisplayName("Should set and get opportunityDescription")
        void shouldSetAndGetOpportunityDescription() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            response.setOpportunityDescription("Detailed description");
            assertEquals("Detailed description", response.getOpportunityDescription());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenFieldsAreSame() {
            LocalDateTime confirmedAt = LocalDateTime.now();
            PointsHistoryResponse response1 = new PointsHistoryResponse(
                    1L, 2L, "Title", "Category", 100, confirmedAt, "Description"
            );
            PointsHistoryResponse response2 = new PointsHistoryResponse(
                    1L, 2L, "Title", "Category", 100, confirmedAt, "Description"
            );

            assertEquals(response1, response2);
            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when applicationId differs")
        void shouldNotBeEqualWhenApplicationIdDiffers() {
            LocalDateTime confirmedAt = LocalDateTime.now();
            PointsHistoryResponse response1 = new PointsHistoryResponse(
                    1L, 2L, "Title", "Category", 100, confirmedAt, "Description"
            );
            PointsHistoryResponse response2 = new PointsHistoryResponse(
                    2L, 2L, "Title", "Category", 100, confirmedAt, "Description"
            );

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should not be equal when pointsAwarded differs")
        void shouldNotBeEqualWhenPointsAwardedDiffers() {
            LocalDateTime confirmedAt = LocalDateTime.now();
            PointsHistoryResponse response1 = new PointsHistoryResponse(
                    1L, 2L, "Title", "Category", 100, confirmedAt, "Description"
            );
            PointsHistoryResponse response2 = new PointsHistoryResponse(
                    1L, 2L, "Title", "Category", 200, confirmedAt, "Description"
            );

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should handle equals with null")
        void shouldHandleEqualsWithNull() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            assertNotEquals(null, response);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            assertEquals(response, response);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return string representation with all fields")
        void shouldReturnStringWithAllFields() {
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);
            String toString = response.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("applicationId"));
            assertTrue(toString.contains("opportunityId"));
            assertTrue(toString.contains("opportunityTitle"));
            assertTrue(toString.contains("opportunityCategory"));
            assertTrue(toString.contains("pointsAwarded"));
        }

        @Test
        @DisplayName("Should return string representation for empty object")
        void shouldReturnStringForEmptyObject() {
            PointsHistoryResponse response = new PointsHistoryResponse();
            String toString = response.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("PointsHistoryResponse"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum integer points")
        void shouldHandleMaxPoints() {
            application.setPointsAwarded(Integer.MAX_VALUE);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(Integer.MAX_VALUE, response.getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle negative points")
        void shouldHandleNegativePoints() {
            application.setPointsAwarded(-50);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(-50, response.getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle special characters in title")
        void shouldHandleSpecialCharactersInTitle() {
            opportunity.setTitle("Test <script>alert('xss')</script>");
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals("Test <script>alert('xss')</script>", response.getOpportunityTitle());
        }

        @Test
        @DisplayName("Should handle unicode characters in description")
        void shouldHandleUnicodeInDescription() {
            opportunity.setDescription("Descrição com acentos e símbolos: áéíóú ñ ç");
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals("Descrição com acentos e símbolos: áéíóú ñ ç", response.getOpportunityDescription());
        }

        @Test
        @DisplayName("Should handle very old date")
        void shouldHandleVeryOldDate() {
            LocalDateTime oldDate = LocalDateTime.of(2000, 1, 1, 0, 0);
            application.setConfirmedAt(oldDate);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(oldDate, response.getConfirmedAt());
        }

        @Test
        @DisplayName("Should handle future date")
        void shouldHandleFutureDate() {
            LocalDateTime futureDate = LocalDateTime.now().plusYears(10);
            application.setConfirmedAt(futureDate);
            PointsHistoryResponse response = PointsHistoryResponse.fromEntity(application);

            assertEquals(futureDate, response.getConfirmedAt());
        }
    }
}
