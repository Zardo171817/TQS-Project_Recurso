package com.example.demo.dto;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BenefitResponse DTO Tests")
class BenefitResponseTest {

    private Benefit benefit;
    private BenefitResponse benefitResponse;

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
        benefit.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        benefitResponse = new BenefitResponse();
        benefitResponse.setId(1L);
        benefitResponse.setName("Desconto Cantina UA");
        benefitResponse.setDescription("10% desconto na cantina da UA");
        benefitResponse.setPointsRequired(100);
        benefitResponse.setCategory(BenefitCategory.UA);
        benefitResponse.setProvider("Universidade de Aveiro");
        benefitResponse.setImageUrl("http://example.com/cantina.jpg");
        benefitResponse.setActive(true);
        benefitResponse.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with no-args constructor")
        void shouldCreateInstanceWithNoArgsConstructor() {
            BenefitResponse response = new BenefitResponse();
            assertNotNull(response);
        }

        @Test
        @DisplayName("Should create instance with all-args constructor")
        void shouldCreateInstanceWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            BenefitResponse response = new BenefitResponse(
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

            assertEquals(1L, response.getId());
            assertEquals("Test Benefit", response.getName());
            assertEquals("Test Description", response.getDescription());
            assertEquals(100, response.getPointsRequired());
            assertEquals(BenefitCategory.UA, response.getCategory());
            assertEquals("Test Provider", response.getProvider());
            assertEquals("http://example.com/image.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertEquals(now, response.getCreatedAt());
        }

        @Test
        @DisplayName("Should create instance with null values in all-args constructor")
        void shouldCreateInstanceWithNullValuesInAllArgsConstructor() {
            BenefitResponse response = new BenefitResponse(
                    null, null, null, null, null, null, null, null, null
            );

            assertNull(response.getId());
            assertNull(response.getName());
            assertNull(response.getDescription());
            assertNull(response.getPointsRequired());
            assertNull(response.getCategory());
            assertNull(response.getProvider());
            assertNull(response.getImageUrl());
            assertNull(response.getActive());
            assertNull(response.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("fromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should correctly map entity to response")
        void shouldCorrectlyMapEntityToResponse() {
            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertNotNull(response);
            assertEquals(benefit.getId(), response.getId());
            assertEquals(benefit.getName(), response.getName());
            assertEquals(benefit.getDescription(), response.getDescription());
            assertEquals(benefit.getPointsRequired(), response.getPointsRequired());
            assertEquals(benefit.getCategory(), response.getCategory());
            assertEquals(benefit.getProvider(), response.getProvider());
            assertEquals(benefit.getImageUrl(), response.getImageUrl());
            assertEquals(benefit.getActive(), response.getActive());
            assertEquals(benefit.getCreatedAt(), response.getCreatedAt());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            BenefitResponse response = BenefitResponse.fromEntity(null);
            assertNull(response);
        }

        @Test
        @DisplayName("Should handle entity with null fields")
        void shouldHandleEntityWithNullFields() {
            Benefit emptyBenefit = new Benefit();
            emptyBenefit.setId(1L);

            BenefitResponse response = BenefitResponse.fromEntity(emptyBenefit);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertNull(response.getName());
            assertNull(response.getDescription());
            assertNull(response.getPointsRequired());
            assertNull(response.getCategory());
            assertNull(response.getProvider());
            assertNull(response.getImageUrl());
            // Active has default value true in entity
            assertEquals(true, response.getActive());
            assertNull(response.getCreatedAt());
        }

        @Test
        @DisplayName("Should map UA category correctly")
        void shouldMapUACategoryCorrectly() {
            benefit.setCategory(BenefitCategory.UA);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertEquals(BenefitCategory.UA, response.getCategory());
        }

        @Test
        @DisplayName("Should map PARTNER category correctly")
        void shouldMapPartnerCategoryCorrectly() {
            benefit.setCategory(BenefitCategory.PARTNER);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
        }

        @Test
        @DisplayName("Should map active status correctly")
        void shouldMapActiveStatusCorrectly() {
            benefit.setActive(false);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertFalse(response.getActive());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            benefitResponse.setId(100L);
            assertEquals(100L, benefitResponse.getId());
        }

        @Test
        @DisplayName("Should get and set name")
        void shouldGetAndSetName() {
            benefitResponse.setName("New Name");
            assertEquals("New Name", benefitResponse.getName());
        }

        @Test
        @DisplayName("Should get and set description")
        void shouldGetAndSetDescription() {
            benefitResponse.setDescription("New Description");
            assertEquals("New Description", benefitResponse.getDescription());
        }

        @Test
        @DisplayName("Should get and set pointsRequired")
        void shouldGetAndSetPointsRequired() {
            benefitResponse.setPointsRequired(500);
            assertEquals(500, benefitResponse.getPointsRequired());
        }

        @Test
        @DisplayName("Should get and set category")
        void shouldGetAndSetCategory() {
            benefitResponse.setCategory(BenefitCategory.PARTNER);
            assertEquals(BenefitCategory.PARTNER, benefitResponse.getCategory());
        }

        @Test
        @DisplayName("Should get and set provider")
        void shouldGetAndSetProvider() {
            benefitResponse.setProvider("New Provider");
            assertEquals("New Provider", benefitResponse.getProvider());
        }

        @Test
        @DisplayName("Should get and set imageUrl")
        void shouldGetAndSetImageUrl() {
            benefitResponse.setImageUrl("http://new-url.com/image.jpg");
            assertEquals("http://new-url.com/image.jpg", benefitResponse.getImageUrl());
        }

        @Test
        @DisplayName("Should get and set active")
        void shouldGetAndSetActive() {
            benefitResponse.setActive(false);
            assertFalse(benefitResponse.getActive());
        }

        @Test
        @DisplayName("Should get and set createdAt")
        void shouldGetAndSetCreatedAt() {
            LocalDateTime newDate = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
            benefitResponse.setCreatedAt(newDate);
            assertEquals(newDate, benefitResponse.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            BenefitResponse response1 = new BenefitResponse(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );
            BenefitResponse response2 = new BenefitResponse(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );

            assertEquals(response1, response2);
            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            BenefitResponse response1 = new BenefitResponse(
                    1L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );
            BenefitResponse response2 = new BenefitResponse(
                    2L, "Name", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should not be equal when name is different")
        void shouldNotBeEqualWhenNameIsDifferent() {
            BenefitResponse response1 = new BenefitResponse(
                    1L, "Name1", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );
            BenefitResponse response2 = new BenefitResponse(
                    1L, "Name2", "Description", 100, BenefitCategory.UA,
                    "Provider", "http://url.com", true, LocalDateTime.of(2024, 1, 1, 0, 0)
            );

            assertNotEquals(response1, response2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(benefitResponse, benefitResponse);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, benefitResponse);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", benefitResponse);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include all fields in toString")
        void shouldIncludeAllFieldsInToString() {
            String toString = benefitResponse.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Desconto Cantina UA"));
            assertTrue(toString.contains("10% desconto na cantina da UA"));
            assertTrue(toString.contains("100"));
            assertTrue(toString.contains("UA"));
            assertTrue(toString.contains("Universidade de Aveiro"));
            assertTrue(toString.contains("http://example.com/cantina.jpg"));
            assertTrue(toString.contains("true"));
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            BenefitResponse response = new BenefitResponse();
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
            benefitResponse.setPointsRequired(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, benefitResponse.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle zero points")
        void shouldHandleZeroPoints() {
            benefitResponse.setPointsRequired(0);
            assertEquals(0, benefitResponse.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle very long name")
        void shouldHandleVeryLongName() {
            String longName = "A".repeat(1000);
            benefitResponse.setName(longName);
            assertEquals(longName, benefitResponse.getName());
        }

        @Test
        @DisplayName("Should handle very long description")
        void shouldHandleVeryLongDescription() {
            String longDescription = "B".repeat(5000);
            benefitResponse.setDescription(longDescription);
            assertEquals(longDescription, benefitResponse.getDescription());
        }

        @Test
        @DisplayName("Should handle special characters in name")
        void shouldHandleSpecialCharactersInName() {
            String specialName = "Desconto 50% - Promoção Especial! @#$%^&*()";
            benefitResponse.setName(specialName);
            assertEquals(specialName, benefitResponse.getName());
        }

        @Test
        @DisplayName("Should handle unicode characters in description")
        void shouldHandleUnicodeCharactersInDescription() {
            String unicodeDescription = "Desconto válido para estudantes 学生 студенты مطالب";
            benefitResponse.setDescription(unicodeDescription);
            assertEquals(unicodeDescription, benefitResponse.getDescription());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            benefitResponse.setName("");
            benefitResponse.setDescription("");
            benefitResponse.setProvider("");
            benefitResponse.setImageUrl("");

            assertEquals("", benefitResponse.getName());
            assertEquals("", benefitResponse.getDescription());
            assertEquals("", benefitResponse.getProvider());
            assertEquals("", benefitResponse.getImageUrl());
        }

        @Test
        @DisplayName("Should handle minimum date")
        void shouldHandleMinimumDate() {
            LocalDateTime minDate = LocalDateTime.MIN;
            benefitResponse.setCreatedAt(minDate);
            assertEquals(minDate, benefitResponse.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle maximum date")
        void shouldHandleMaximumDate() {
            LocalDateTime maxDate = LocalDateTime.MAX;
            benefitResponse.setCreatedAt(maxDate);
            assertEquals(maxDate, benefitResponse.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for id")
        void shouldHandleLongMaxValueForId() {
            benefitResponse.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, benefitResponse.getId());
        }

        @Test
        @DisplayName("Should handle Long.MIN_VALUE for id")
        void shouldHandleLongMinValueForId() {
            benefitResponse.setId(Long.MIN_VALUE);
            assertEquals(Long.MIN_VALUE, benefitResponse.getId());
        }
    }

    @Nested
    @DisplayName("Category Enum Tests")
    class CategoryEnumTests {

        @Test
        @DisplayName("Should correctly set UA category")
        void shouldCorrectlySetUACategory() {
            benefitResponse.setCategory(BenefitCategory.UA);
            assertEquals(BenefitCategory.UA, benefitResponse.getCategory());
        }

        @Test
        @DisplayName("Should correctly set PARTNER category")
        void shouldCorrectlySetPartnerCategory() {
            benefitResponse.setCategory(BenefitCategory.PARTNER);
            assertEquals(BenefitCategory.PARTNER, benefitResponse.getCategory());
        }

        @Test
        @DisplayName("Should handle null category")
        void shouldHandleNullCategory() {
            benefitResponse.setCategory(null);
            assertNull(benefitResponse.getCategory());
        }
    }

    @Nested
    @DisplayName("fromEntity Edge Cases Tests")
    class FromEntityEdgeCasesTests {

        @Test
        @DisplayName("Should handle entity with maximum id value")
        void shouldHandleEntityWithMaximumIdValue() {
            benefit.setId(Long.MAX_VALUE);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertEquals(Long.MAX_VALUE, response.getId());
        }

        @Test
        @DisplayName("Should handle entity with maximum points value")
        void shouldHandleEntityWithMaximumPointsValue() {
            benefit.setPointsRequired(Integer.MAX_VALUE);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertEquals(Integer.MAX_VALUE, response.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle entity with special characters in all string fields")
        void shouldHandleEntityWithSpecialCharactersInAllStringFields() {
            benefit.setName("Name!@#$%");
            benefit.setDescription("Desc<>&\"'");
            benefit.setProvider("Provider[];{}");
            benefit.setImageUrl("http://example.com/image?param=value&other=123");

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertEquals("Name!@#$%", response.getName());
            assertEquals("Desc<>&\"'", response.getDescription());
            assertEquals("Provider[];{}", response.getProvider());
            assertEquals("http://example.com/image?param=value&other=123", response.getImageUrl());
        }

        @Test
        @DisplayName("Should handle entity with inactive status")
        void shouldHandleEntityWithInactiveStatus() {
            benefit.setActive(false);
            BenefitResponse response = BenefitResponse.fromEntity(benefit);
            assertFalse(response.getActive());
        }
    }
}
