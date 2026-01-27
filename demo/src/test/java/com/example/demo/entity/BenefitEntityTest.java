package com.example.demo.entity;

import com.example.demo.entity.Benefit.BenefitCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Benefit Entity Tests")
class BenefitEntityTest {

    private Benefit benefit;

    @BeforeEach
    void setUp() {
        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setName("Desconto Cantina UA");
        benefit.setDescription("10% desconto na cantina da UA");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.UA);
        benefit.setProvider("Universidade de Aveiro");
        benefit.setImageUrl("http://example.com/cantina.jpg");
        benefit.setActive(true);
        benefit.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with no-args constructor")
        void shouldCreateInstanceWithNoArgsConstructor() {
            Benefit newBenefit = new Benefit();
            assertNotNull(newBenefit);
        }

        @Test
        @DisplayName("Should create instance with all-args constructor")
        void shouldCreateInstanceWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            Benefit newBenefit = new Benefit(
                    1L,
                    "Test Benefit",
                    "Test Description",
                    100,
                    BenefitCategory.UA,
                    "Test Provider",
                    "http://example.com/image.jpg",
                    true,
                    now
            );

            assertEquals(1L, newBenefit.getId());
            assertEquals("Test Benefit", newBenefit.getName());
            assertEquals("Test Description", newBenefit.getDescription());
            assertEquals(100, newBenefit.getPointsRequired());
            assertEquals(BenefitCategory.UA, newBenefit.getCategory());
            assertEquals("Test Provider", newBenefit.getProvider());
            assertEquals("http://example.com/image.jpg", newBenefit.getImageUrl());
            assertTrue(newBenefit.getActive());
            assertEquals(now, newBenefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should have default active value as true")
        void shouldHaveDefaultActiveValueAsTrue() {
            Benefit newBenefit = new Benefit();
            assertEquals(true, newBenefit.getActive());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            benefit.setId(100L);
            assertEquals(100L, benefit.getId());
        }

        @Test
        @DisplayName("Should get and set name")
        void shouldGetAndSetName() {
            benefit.setName("New Name");
            assertEquals("New Name", benefit.getName());
        }

        @Test
        @DisplayName("Should get and set description")
        void shouldGetAndSetDescription() {
            benefit.setDescription("New Description");
            assertEquals("New Description", benefit.getDescription());
        }

        @Test
        @DisplayName("Should get and set pointsRequired")
        void shouldGetAndSetPointsRequired() {
            benefit.setPointsRequired(500);
            assertEquals(500, benefit.getPointsRequired());
        }

        @Test
        @DisplayName("Should get and set category")
        void shouldGetAndSetCategory() {
            benefit.setCategory(BenefitCategory.PARTNER);
            assertEquals(BenefitCategory.PARTNER, benefit.getCategory());
        }

        @Test
        @DisplayName("Should get and set provider")
        void shouldGetAndSetProvider() {
            benefit.setProvider("New Provider");
            assertEquals("New Provider", benefit.getProvider());
        }

        @Test
        @DisplayName("Should get and set imageUrl")
        void shouldGetAndSetImageUrl() {
            benefit.setImageUrl("http://new-url.com/image.jpg");
            assertEquals("http://new-url.com/image.jpg", benefit.getImageUrl());
        }

        @Test
        @DisplayName("Should get and set active")
        void shouldGetAndSetActive() {
            benefit.setActive(false);
            assertFalse(benefit.getActive());
        }

        @Test
        @DisplayName("Should get and set createdAt")
        void shouldGetAndSetCreatedAt() {
            LocalDateTime newDate = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
            benefit.setCreatedAt(newDate);
            assertEquals(newDate, benefit.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on prePersist when null")
        void shouldSetCreatedAtOnPrePersistWhenNull() {
            Benefit newBenefit = new Benefit();
            newBenefit.setCreatedAt(null);
            newBenefit.onCreate();
            assertNotNull(newBenefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should not override createdAt on prePersist when already set")
        void shouldNotOverrideCreatedAtOnPrePersistWhenAlreadySet() {
            LocalDateTime originalDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
            Benefit newBenefit = new Benefit();
            newBenefit.setCreatedAt(originalDate);
            newBenefit.onCreate();
            assertEquals(originalDate, newBenefit.getCreatedAt());
        }

        @Test
        @DisplayName("Should set active to true on prePersist when null")
        void shouldSetActiveToTrueOnPrePersistWhenNull() {
            Benefit newBenefit = new Benefit();
            newBenefit.setActive(null);
            newBenefit.onCreate();
            assertTrue(newBenefit.getActive());
        }

        @Test
        @DisplayName("Should not override active on prePersist when already set")
        void shouldNotOverrideActiveOnPrePersistWhenAlreadySet() {
            Benefit newBenefit = new Benefit();
            newBenefit.setActive(false);
            newBenefit.onCreate();
            assertFalse(newBenefit.getActive());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            LocalDateTime now = LocalDateTime.now();
            Benefit benefit1 = new Benefit(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, now
            );
            Benefit benefit2 = new Benefit(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, now
            );

            assertEquals(benefit1, benefit2);
            assertEquals(benefit1.hashCode(), benefit2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            LocalDateTime now = LocalDateTime.now();
            Benefit benefit1 = new Benefit(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, now
            );
            Benefit benefit2 = new Benefit(
                    2L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, now
            );

            assertNotEquals(benefit1, benefit2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(benefit, benefit);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, benefit);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", benefit);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include all fields in toString")
        void shouldIncludeAllFieldsInToString() {
            String toString = benefit.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Desconto Cantina UA"));
            assertTrue(toString.contains("10% desconto na cantina da UA"));
            assertTrue(toString.contains("100"));
            assertTrue(toString.contains("UA"));
            assertTrue(toString.contains("Universidade de Aveiro"));
        }
    }

    @Nested
    @DisplayName("BenefitCategory Enum Tests")
    class BenefitCategoryEnumTests {

        @Test
        @DisplayName("Should have UA category")
        void shouldHaveUACategory() {
            assertEquals(BenefitCategory.UA, BenefitCategory.valueOf("UA"));
        }

        @Test
        @DisplayName("Should have PARTNER category")
        void shouldHavePartnerCategory() {
            assertEquals(BenefitCategory.PARTNER, BenefitCategory.valueOf("PARTNER"));
        }

        @Test
        @DisplayName("Should have exactly two categories")
        void shouldHaveExactlyTwoCategories() {
            BenefitCategory[] categories = BenefitCategory.values();
            assertEquals(2, categories.length);
        }

        @Test
        @DisplayName("Should return correct name for UA")
        void shouldReturnCorrectNameForUA() {
            assertEquals("UA", BenefitCategory.UA.name());
        }

        @Test
        @DisplayName("Should return correct name for PARTNER")
        void shouldReturnCorrectNameForPartner() {
            assertEquals("PARTNER", BenefitCategory.PARTNER.name());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum integer value for points")
        void shouldHandleMaximumIntegerValueForPoints() {
            benefit.setPointsRequired(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, benefit.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle zero points")
        void shouldHandleZeroPoints() {
            benefit.setPointsRequired(0);
            assertEquals(0, benefit.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle very long name")
        void shouldHandleVeryLongName() {
            String longName = "A".repeat(500);
            benefit.setName(longName);
            assertEquals(longName, benefit.getName());
        }

        @Test
        @DisplayName("Should handle special characters in name")
        void shouldHandleSpecialCharactersInName() {
            String specialName = "Desconto 50% - Promoção Especial! @#$%^&*()";
            benefit.setName(specialName);
            assertEquals(specialName, benefit.getName());
        }

        @Test
        @DisplayName("Should handle unicode characters in description")
        void shouldHandleUnicodeCharactersInDescription() {
            String unicodeDescription = "Desconto válido para estudantes 学生 студенты مطالب";
            benefit.setDescription(unicodeDescription);
            assertEquals(unicodeDescription, benefit.getDescription());
        }

        @Test
        @DisplayName("Should handle null imageUrl")
        void shouldHandleNullImageUrl() {
            benefit.setImageUrl(null);
            assertNull(benefit.getImageUrl());
        }

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for id")
        void shouldHandleLongMaxValueForId() {
            benefit.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, benefit.getId());
        }
    }
}
