package com.example.demo.entity;

import com.example.demo.entity.Benefit.BenefitCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Benefit Entity Tests")
class BenefitEntityTest {

    @Nested
    @DisplayName("Entity Creation Tests")
    class EntityCreationTests {

        @Test
        @DisplayName("Should create benefit with no-args constructor")
        void shouldCreateBenefitWithNoArgsConstructor() {
            Benefit benefit = new Benefit();

            assertNull(benefit.getId());
            assertNull(benefit.getName());
            assertNull(benefit.getDescription());
            assertNull(benefit.getPointsRequired());
            assertNull(benefit.getCategory());
            assertNull(benefit.getProvider());
            assertNull(benefit.getImageUrl());
        }

        @Test
        @DisplayName("Should create benefit with all-args constructor")
        void shouldCreateBenefitWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            Benefit benefit = new Benefit(
                    1L, "Test", "Description", 100,
                    BenefitCategory.PARTNER, "Provider", "http://img.jpg",
                    true, now
            );

            assertEquals(1L, benefit.getId());
            assertEquals("Test", benefit.getName());
            assertEquals("Description", benefit.getDescription());
            assertEquals(100, benefit.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
            assertEquals("Provider", benefit.getProvider());
            assertEquals("http://img.jpg", benefit.getImageUrl());
            assertTrue(benefit.getActive());
            assertEquals(now, benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            Benefit benefit = new Benefit();
            LocalDateTime now = LocalDateTime.now();

            benefit.setId(5L);
            benefit.setName("Desconto Cinema");
            benefit.setDescription("20% desconto");
            benefit.setPointsRequired(150);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Cinema NOS");
            benefit.setImageUrl("http://cinema.jpg");
            benefit.setActive(true);
            benefit.setCreatedAt(now);

            assertEquals(5L, benefit.getId());
            assertEquals("Desconto Cinema", benefit.getName());
            assertEquals("20% desconto", benefit.getDescription());
            assertEquals(150, benefit.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
            assertEquals("Cinema NOS", benefit.getProvider());
            assertEquals("http://cinema.jpg", benefit.getImageUrl());
            assertTrue(benefit.getActive());
            assertEquals(now, benefit.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on PrePersist when null")
        void shouldSetCreatedAtOnPrePersist() {
            Benefit benefit = new Benefit();
            assertNull(benefit.getCreatedAt());

            benefit.onCreate();

            assertNotNull(benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should not override createdAt if already set")
        void shouldNotOverrideCreatedAt() {
            Benefit benefit = new Benefit();
            LocalDateTime originalTime = LocalDateTime.of(2024, 1, 1, 0, 0);
            benefit.setCreatedAt(originalTime);

            benefit.onCreate();

            assertEquals(originalTime, benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should set active to true on PrePersist when null")
        void shouldSetActiveToTrueOnPrePersist() {
            Benefit benefit = new Benefit();
            benefit.setActive(null);

            benefit.onCreate();

            assertTrue(benefit.getActive());
        }

        @Test
        @DisplayName("Should not override active if already set to false")
        void shouldNotOverrideActiveIfAlreadySet() {
            Benefit benefit = new Benefit();
            benefit.setActive(false);

            benefit.onCreate();

            assertFalse(benefit.getActive());
        }

        @Test
        @DisplayName("Should preserve active true value on PrePersist")
        void shouldPreserveActiveTrueValue() {
            Benefit benefit = new Benefit();
            benefit.setActive(true);

            benefit.onCreate();

            assertTrue(benefit.getActive());
        }
    }

    @Nested
    @DisplayName("BenefitCategory Enum Tests")
    class BenefitCategoryEnumTests {

        @Test
        @DisplayName("Should have UA category")
        void shouldHaveUaCategory() {
            assertEquals("UA", BenefitCategory.UA.name());
        }

        @Test
        @DisplayName("Should have PARTNER category")
        void shouldHavePartnerCategory() {
            assertEquals("PARTNER", BenefitCategory.PARTNER.name());
        }

        @Test
        @DisplayName("Should have exactly 2 categories")
        void shouldHaveExactlyTwoCategories() {
            BenefitCategory[] categories = BenefitCategory.values();
            assertEquals(2, categories.length);
        }

        @Test
        @DisplayName("Should parse UA from string")
        void shouldParseUaFromString() {
            BenefitCategory category = BenefitCategory.valueOf("UA");
            assertEquals(BenefitCategory.UA, category);
        }

        @Test
        @DisplayName("Should parse PARTNER from string")
        void shouldParsePartnerFromString() {
            BenefitCategory category = BenefitCategory.valueOf("PARTNER");
            assertEquals(BenefitCategory.PARTNER, category);
        }

        @Test
        @DisplayName("Should throw exception for invalid category")
        void shouldThrowExceptionForInvalidCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> BenefitCategory.valueOf("INVALID"));
        }
    }

    @Nested
    @DisplayName("Partner Benefit Specific Tests")
    class PartnerBenefitTests {

        @Test
        @DisplayName("Should create a partner benefit with all required fields")
        void shouldCreatePartnerBenefitWithRequiredFields() {
            Benefit benefit = new Benefit();
            benefit.setName("Desconto Restaurante");
            benefit.setDescription("15% desconto em refeicoes");
            benefit.setPointsRequired(200);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Restaurante Sabor");

            assertEquals("Desconto Restaurante", benefit.getName());
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
            assertEquals("Restaurante Sabor", benefit.getProvider());
            assertEquals(200, benefit.getPointsRequired());
        }

        @Test
        @DisplayName("Should differentiate partner from UA benefits")
        void shouldDifferentiatePartnerFromUa() {
            Benefit partnerBenefit = new Benefit();
            partnerBenefit.setCategory(BenefitCategory.PARTNER);

            Benefit uaBenefit = new Benefit();
            uaBenefit.setCategory(BenefitCategory.UA);

            assertNotEquals(partnerBenefit.getCategory(), uaBenefit.getCategory());
        }

        @Test
        @DisplayName("Should handle benefit with all optional fields")
        void shouldHandleBenefitWithAllOptionalFields() {
            Benefit benefit = new Benefit();
            benefit.setName("Test");
            benefit.setDescription("Test desc");
            benefit.setPointsRequired(100);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Provider");
            benefit.setImageUrl("http://img.jpg");
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.now());

            assertNotNull(benefit.getImageUrl());
            assertNotNull(benefit.getActive());
            assertNotNull(benefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle benefit without optional imageUrl")
        void shouldHandleBenefitWithoutImageUrl() {
            Benefit benefit = new Benefit();
            benefit.setName("Test");
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Provider");

            assertNull(benefit.getImageUrl());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode, and ToString Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should implement equals correctly")
        void shouldImplementEquals() {
            LocalDateTime now = LocalDateTime.now();
            Benefit b1 = new Benefit(1L, "A", "B", 100, BenefitCategory.PARTNER, "C", "D", true, now);
            Benefit b2 = new Benefit(1L, "A", "B", 100, BenefitCategory.PARTNER, "C", "D", true, now);

            assertEquals(b1, b2);
        }

        @Test
        @DisplayName("Should detect inequality")
        void shouldDetectInequality() {
            Benefit b1 = new Benefit();
            b1.setId(1L);

            Benefit b2 = new Benefit();
            b2.setId(2L);

            assertNotEquals(b1, b2);
        }

        @Test
        @DisplayName("Should implement hashCode")
        void shouldImplementHashCode() {
            LocalDateTime now = LocalDateTime.now();
            Benefit b1 = new Benefit(1L, "A", "B", 100, BenefitCategory.PARTNER, "C", "D", true, now);
            Benefit b2 = new Benefit(1L, "A", "B", 100, BenefitCategory.PARTNER, "C", "D", true, now);

            assertEquals(b1.hashCode(), b2.hashCode());
        }

        @Test
        @DisplayName("Should implement toString")
        void shouldImplementToString() {
            Benefit benefit = new Benefit();
            benefit.setName("Test Benefit");

            assertNotNull(benefit.toString());
            assertTrue(benefit.toString().contains("Test Benefit"));
        }
    }
}
