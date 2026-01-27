package com.example.demo.entity;

import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption.RedemptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ver Resgates Feitos Parceiro - Entity Tests")
class VerResgatesFeitosParceirosEntityTest {

    @Nested
    @DisplayName("Benefit Entity Tests")
    class BenefitEntityTests {

        @Test
        @DisplayName("Should create benefit with no-args constructor")
        void shouldCreateBenefitWithNoArgsConstructor() {
            Benefit benefit = new Benefit();
            assertNotNull(benefit);
        }

        @Test
        @DisplayName("Should create benefit with all-args constructor")
        void shouldCreateBenefitWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            Benefit benefit = new Benefit(
                    1L, "Test Benefit", "Test Description", 100,
                    BenefitCategory.PARTNER, "Test Provider", "http://img.jpg",
                    true, now
            );

            assertEquals(1L, benefit.getId());
            assertEquals("Test Benefit", benefit.getName());
            assertEquals("Test Description", benefit.getDescription());
            assertEquals(100, benefit.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
            assertEquals("Test Provider", benefit.getProvider());
            assertEquals("http://img.jpg", benefit.getImageUrl());
            assertTrue(benefit.getActive());
            assertEquals(now, benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Desconto Cinema");
            benefit.setDescription("20% desconto");
            benefit.setPointsRequired(150);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Cinema NOS");
            benefit.setImageUrl("http://cinema.jpg");
            benefit.setActive(true);
            LocalDateTime now = LocalDateTime.now();
            benefit.setCreatedAt(now);

            assertEquals(1L, benefit.getId());
            assertEquals("Desconto Cinema", benefit.getName());
            assertEquals("20% desconto", benefit.getDescription());
            assertEquals(150, benefit.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
            assertEquals("Cinema NOS", benefit.getProvider());
            assertEquals("http://cinema.jpg", benefit.getImageUrl());
            assertTrue(benefit.getActive());
            assertEquals(now, benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should have PARTNER and UA categories")
        void shouldHavePartnerAndUaCategories() {
            assertEquals(2, BenefitCategory.values().length);
            assertNotNull(BenefitCategory.valueOf("PARTNER"));
            assertNotNull(BenefitCategory.valueOf("UA"));
        }

        @Test
        @DisplayName("Should set active to true on prePersist when null")
        void shouldSetActiveTrueOnPrePersistWhenNull() {
            Benefit benefit = new Benefit();
            benefit.setActive(null);
            benefit.onCreate();

            assertTrue(benefit.getActive());
            assertNotNull(benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should set createdAt on prePersist when null")
        void shouldSetCreatedAtOnPrePersistWhenNull() {
            Benefit benefit = new Benefit();
            benefit.setCreatedAt(null);
            benefit.onCreate();

            assertNotNull(benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should not override createdAt on prePersist when already set")
        void shouldNotOverrideCreatedAtWhenAlreadySet() {
            Benefit benefit = new Benefit();
            LocalDateTime existingTime = LocalDateTime.of(2024, 1, 1, 10, 0);
            benefit.setCreatedAt(existingTime);
            benefit.setActive(true);
            benefit.onCreate();

            assertEquals(existingTime, benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should not override active on prePersist when already set")
        void shouldNotOverrideActiveWhenAlreadySet() {
            Benefit benefit = new Benefit();
            benefit.setActive(false);
            benefit.setCreatedAt(LocalDateTime.now());
            benefit.onCreate();

            assertFalse(benefit.getActive());
        }

        @Test
        @DisplayName("Should support equals and hashCode")
        void shouldSupportEqualsAndHashCode() {
            Benefit b1 = new Benefit();
            b1.setId(1L);
            b1.setName("Test");

            Benefit b2 = new Benefit();
            b2.setId(1L);
            b2.setName("Test");

            assertEquals(b1, b2);
            assertEquals(b1.hashCode(), b2.hashCode());
        }

        @Test
        @DisplayName("Should support toString")
        void shouldSupportToString() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Test");

            String str = benefit.toString();
            assertNotNull(str);
            assertTrue(str.contains("Test"));
        }

        @Test
        @DisplayName("Should handle null imageUrl")
        void shouldHandleNullImageUrl() {
            Benefit benefit = new Benefit();
            benefit.setImageUrl(null);
            assertNull(benefit.getImageUrl());
        }

        @Test
        @DisplayName("Should default active to true")
        void shouldDefaultActiveToTrue() {
            Benefit benefit = new Benefit();
            assertTrue(benefit.getActive());
        }
    }

    @Nested
    @DisplayName("Redemption Entity Tests")
    class RedemptionEntityTests {

        @Test
        @DisplayName("Should create redemption with no-args constructor")
        void shouldCreateRedemptionWithNoArgsConstructor() {
            Redemption redemption = new Redemption();
            assertNotNull(redemption);
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);

            Benefit benefit = new Benefit();
            benefit.setId(2L);

            Redemption redemption = new Redemption();
            redemption.setId(10L);
            redemption.setVolunteer(volunteer);
            redemption.setBenefit(benefit);
            redemption.setPointsSpent(150);
            redemption.setStatus(RedemptionStatus.COMPLETED);
            LocalDateTime now = LocalDateTime.now();
            redemption.setRedeemedAt(now);

            assertEquals(10L, redemption.getId());
            assertEquals(1L, redemption.getVolunteer().getId());
            assertEquals(2L, redemption.getBenefit().getId());
            assertEquals(150, redemption.getPointsSpent());
            assertEquals(RedemptionStatus.COMPLETED, redemption.getStatus());
            assertEquals(now, redemption.getRedeemedAt());
        }

        @Test
        @DisplayName("Should have COMPLETED and CANCELLED statuses")
        void shouldHaveCompletedAndCancelledStatuses() {
            assertEquals(2, RedemptionStatus.values().length);
            assertNotNull(RedemptionStatus.valueOf("COMPLETED"));
            assertNotNull(RedemptionStatus.valueOf("CANCELLED"));
        }

        @Test
        @DisplayName("Should set defaults on prePersist")
        void shouldSetDefaultsOnPrePersist() {
            Redemption redemption = new Redemption();
            redemption.onCreate();

            assertNotNull(redemption.getRedeemedAt());
            assertEquals(RedemptionStatus.COMPLETED, redemption.getStatus());
        }

        @Test
        @DisplayName("Should not override redeemedAt when already set")
        void shouldNotOverrideRedeemedAtWhenSet() {
            Redemption redemption = new Redemption();
            LocalDateTime existing = LocalDateTime.of(2024, 1, 1, 10, 0);
            redemption.setRedeemedAt(existing);
            redemption.setStatus(RedemptionStatus.CANCELLED);
            redemption.onCreate();

            assertEquals(existing, redemption.getRedeemedAt());
        }

        @Test
        @DisplayName("Should not override status when already set")
        void shouldNotOverrideStatusWhenSet() {
            Redemption redemption = new Redemption();
            redemption.setStatus(RedemptionStatus.CANCELLED);
            redemption.setRedeemedAt(LocalDateTime.now());
            redemption.onCreate();

            assertEquals(RedemptionStatus.CANCELLED, redemption.getStatus());
        }
    }

    @Nested
    @DisplayName("Volunteer Entity Tests")
    class VolunteerEntityTests {

        @Test
        @DisplayName("Should create volunteer with no-args constructor")
        void shouldCreateVolunteerWithNoArgsConstructor() {
            Volunteer volunteer = new Volunteer();
            assertNotNull(volunteer);
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setName("Maria");
            volunteer.setEmail("maria@email.com");
            volunteer.setPhone("912345678");
            volunteer.setSkills("Java, Python");
            volunteer.setTotalPoints(500);

            assertEquals(1L, volunteer.getId());
            assertEquals("Maria", volunteer.getName());
            assertEquals("maria@email.com", volunteer.getEmail());
            assertEquals("912345678", volunteer.getPhone());
            assertEquals("Java, Python", volunteer.getSkills());
            assertEquals(500, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should default totalPoints to 0")
        void shouldDefaultTotalPointsToZero() {
            Volunteer volunteer = new Volunteer();
            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should set defaults on prePersist when null")
        void shouldSetDefaultsOnPrePersist() {
            Volunteer volunteer = new Volunteer();
            volunteer.setTotalPoints(null);
            volunteer.onCreate();

            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should not override totalPoints on prePersist when already set")
        void shouldNotOverrideTotalPointsWhenSet() {
            Volunteer volunteer = new Volunteer();
            volunteer.setTotalPoints(500);
            volunteer.onCreate();

            assertEquals(500, volunteer.getTotalPoints());
        }
    }
}
