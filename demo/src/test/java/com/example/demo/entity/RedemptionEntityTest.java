package com.example.demo.entity;

import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption.RedemptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Redemption Entity Tests")
class RedemptionEntityTest {

    private Redemption redemption;
    private Volunteer volunteer;
    private Benefit benefit;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Joao Silva");
        volunteer.setEmail("joao@example.com");
        volunteer.setTotalPoints(500);

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
        redemption.setRedeemedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with no-args constructor")
        void shouldCreateInstanceWithNoArgsConstructor() {
            Redemption newRedemption = new Redemption();
            assertNotNull(newRedemption);
        }

        @Test
        @DisplayName("Should create instance with all-args constructor")
        void shouldCreateInstanceWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            Redemption newRedemption = new Redemption(
                    1L, volunteer, benefit, 100,
                    RedemptionStatus.COMPLETED, now
            );

            assertEquals(1L, newRedemption.getId());
            assertEquals(volunteer, newRedemption.getVolunteer());
            assertEquals(benefit, newRedemption.getBenefit());
            assertEquals(100, newRedemption.getPointsSpent());
            assertEquals(RedemptionStatus.COMPLETED, newRedemption.getStatus());
            assertEquals(now, newRedemption.getRedeemedAt());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            redemption.setId(100L);
            assertEquals(100L, redemption.getId());
        }

        @Test
        @DisplayName("Should get and set volunteer")
        void shouldGetAndSetVolunteer() {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setId(2L);
            newVolunteer.setName("Maria Santos");
            redemption.setVolunteer(newVolunteer);
            assertEquals(newVolunteer, redemption.getVolunteer());
            assertEquals("Maria Santos", redemption.getVolunteer().getName());
        }

        @Test
        @DisplayName("Should get and set benefit")
        void shouldGetAndSetBenefit() {
            Benefit newBenefit = new Benefit();
            newBenefit.setId(2L);
            newBenefit.setName("Desconto Cinema");
            redemption.setBenefit(newBenefit);
            assertEquals(newBenefit, redemption.getBenefit());
            assertEquals("Desconto Cinema", redemption.getBenefit().getName());
        }

        @Test
        @DisplayName("Should get and set pointsSpent")
        void shouldGetAndSetPointsSpent() {
            redemption.setPointsSpent(500);
            assertEquals(500, redemption.getPointsSpent());
        }

        @Test
        @DisplayName("Should get and set status")
        void shouldGetAndSetStatus() {
            redemption.setStatus(RedemptionStatus.CANCELLED);
            assertEquals(RedemptionStatus.CANCELLED, redemption.getStatus());
        }

        @Test
        @DisplayName("Should get and set redeemedAt")
        void shouldGetAndSetRedeemedAt() {
            LocalDateTime newDate = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
            redemption.setRedeemedAt(newDate);
            assertEquals(newDate, redemption.getRedeemedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set redeemedAt on prePersist when null")
        void shouldSetRedeemedAtOnPrePersistWhenNull() {
            Redemption newRedemption = new Redemption();
            newRedemption.setRedeemedAt(null);
            newRedemption.onCreate();
            assertNotNull(newRedemption.getRedeemedAt());
        }

        @Test
        @DisplayName("Should not override redeemedAt on prePersist when already set")
        void shouldNotOverrideRedeemedAtOnPrePersistWhenAlreadySet() {
            LocalDateTime originalDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
            Redemption newRedemption = new Redemption();
            newRedemption.setRedeemedAt(originalDate);
            newRedemption.onCreate();
            assertEquals(originalDate, newRedemption.getRedeemedAt());
        }

        @Test
        @DisplayName("Should set status to COMPLETED on prePersist when null")
        void shouldSetStatusToCompletedOnPrePersistWhenNull() {
            Redemption newRedemption = new Redemption();
            newRedemption.setStatus(null);
            newRedemption.onCreate();
            assertEquals(RedemptionStatus.COMPLETED, newRedemption.getStatus());
        }

        @Test
        @DisplayName("Should not override status on prePersist when already set")
        void shouldNotOverrideStatusOnPrePersistWhenAlreadySet() {
            Redemption newRedemption = new Redemption();
            newRedemption.setStatus(RedemptionStatus.CANCELLED);
            newRedemption.onCreate();
            assertEquals(RedemptionStatus.CANCELLED, newRedemption.getStatus());
        }
    }

    @Nested
    @DisplayName("RedemptionStatus Enum Tests")
    class RedemptionStatusEnumTests {

        @Test
        @DisplayName("Should have COMPLETED status")
        void shouldHaveCompletedStatus() {
            assertEquals(RedemptionStatus.COMPLETED, RedemptionStatus.valueOf("COMPLETED"));
        }

        @Test
        @DisplayName("Should have CANCELLED status")
        void shouldHaveCancelledStatus() {
            assertEquals(RedemptionStatus.CANCELLED, RedemptionStatus.valueOf("CANCELLED"));
        }

        @Test
        @DisplayName("Should have exactly two statuses")
        void shouldHaveExactlyTwoStatuses() {
            RedemptionStatus[] statuses = RedemptionStatus.values();
            assertEquals(2, statuses.length);
        }

        @Test
        @DisplayName("Should return correct name for COMPLETED")
        void shouldReturnCorrectNameForCompleted() {
            assertEquals("COMPLETED", RedemptionStatus.COMPLETED.name());
        }

        @Test
        @DisplayName("Should return correct name for CANCELLED")
        void shouldReturnCorrectNameForCancelled() {
            assertEquals("CANCELLED", RedemptionStatus.CANCELLED.name());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(redemption, redemption);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, redemption);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", redemption);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = redemption.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("100"));
            assertTrue(toString.contains("COMPLETED"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum integer value for pointsSpent")
        void shouldHandleMaximumIntegerValueForPointsSpent() {
            redemption.setPointsSpent(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, redemption.getPointsSpent());
        }

        @Test
        @DisplayName("Should handle zero points spent")
        void shouldHandleZeroPointsSpent() {
            redemption.setPointsSpent(0);
            assertEquals(0, redemption.getPointsSpent());
        }

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for id")
        void shouldHandleLongMaxValueForId() {
            redemption.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, redemption.getId());
        }

        @Test
        @DisplayName("Should handle null volunteer")
        void shouldHandleNullVolunteer() {
            redemption.setVolunteer(null);
            assertNull(redemption.getVolunteer());
        }

        @Test
        @DisplayName("Should handle null benefit")
        void shouldHandleNullBenefit() {
            redemption.setBenefit(null);
            assertNull(redemption.getBenefit());
        }
    }
}
