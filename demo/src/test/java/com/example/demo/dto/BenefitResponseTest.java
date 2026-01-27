package com.example.demo.dto;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BenefitResponse DTO Tests")
class BenefitResponseTest {

    @Nested
    @DisplayName("fromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should create response from entity with all fields")
        void shouldCreateResponseFromEntityWithAllFields() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Desconto Cinema");
            benefit.setDescription("20% desconto em bilhetes");
            benefit.setPointsRequired(150);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Cinema NOS");
            benefit.setImageUrl("http://example.com/img.jpg");
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 0));

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Desconto Cinema", response.getName());
            assertEquals("20% desconto em bilhetes", response.getDescription());
            assertEquals(150, response.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
            assertEquals("Cinema NOS", response.getProvider());
            assertEquals("http://example.com/img.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertEquals(LocalDateTime.of(2024, 6, 15, 10, 0), response.getCreatedAt());
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
            benefit.setDescription("desc");
            benefit.setPointsRequired(100);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Provider");
            benefit.setImageUrl(null);
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.now());

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertNotNull(response);
            assertNull(response.getImageUrl());
        }

        @Test
        @DisplayName("Should map UA category correctly")
        void shouldMapUaCategoryCorrectly() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("UA Benefit");
            benefit.setDescription("desc");
            benefit.setPointsRequired(50);
            benefit.setCategory(BenefitCategory.UA);
            benefit.setProvider("UA");
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.now());

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertEquals(BenefitCategory.UA, response.getCategory());
        }

        @Test
        @DisplayName("Should map PARTNER category correctly")
        void shouldMapPartnerCategoryCorrectly() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Partner Benefit");
            benefit.setDescription("desc");
            benefit.setPointsRequired(100);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Partner");
            benefit.setActive(true);
            benefit.setCreatedAt(LocalDateTime.now());

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertEquals(BenefitCategory.PARTNER, response.getCategory());
        }

        @Test
        @DisplayName("Should handle inactive benefit")
        void shouldHandleInactiveBenefit() {
            Benefit benefit = new Benefit();
            benefit.setId(1L);
            benefit.setName("Inactive");
            benefit.setDescription("desc");
            benefit.setPointsRequired(100);
            benefit.setCategory(BenefitCategory.PARTNER);
            benefit.setProvider("Provider");
            benefit.setActive(false);
            benefit.setCreatedAt(LocalDateTime.now());

            BenefitResponse response = BenefitResponse.fromEntity(benefit);

            assertFalse(response.getActive());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should create with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            BenefitResponse response = new BenefitResponse();

            assertNull(response.getId());
            assertNull(response.getName());
            assertNull(response.getDescription());
        }

        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            BenefitResponse response = new BenefitResponse(
                    1L, "Name", "Desc", 100, BenefitCategory.PARTNER,
                    "Provider", "http://img.jpg", true, now
            );

            assertEquals(1L, response.getId());
            assertEquals("Name", response.getName());
            assertEquals("Desc", response.getDescription());
            assertEquals(100, response.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
            assertEquals("Provider", response.getProvider());
            assertEquals("http://img.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertEquals(now, response.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            BenefitResponse response = new BenefitResponse();
            LocalDateTime now = LocalDateTime.now();

            response.setId(5L);
            response.setName("Test Name");
            response.setDescription("Test Desc");
            response.setPointsRequired(250);
            response.setCategory(BenefitCategory.PARTNER);
            response.setProvider("Test Provider");
            response.setImageUrl("http://test.jpg");
            response.setActive(true);
            response.setCreatedAt(now);

            assertEquals(5L, response.getId());
            assertEquals("Test Name", response.getName());
            assertEquals("Test Desc", response.getDescription());
            assertEquals(250, response.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, response.getCategory());
            assertEquals("Test Provider", response.getProvider());
            assertEquals("http://test.jpg", response.getImageUrl());
            assertTrue(response.getActive());
            assertEquals(now, response.getCreatedAt());
        }

        @Test
        @DisplayName("Should implement equals")
        void shouldImplementEquals() {
            LocalDateTime now = LocalDateTime.now();
            BenefitResponse r1 = new BenefitResponse(1L, "A", "B", 1, BenefitCategory.PARTNER, "C", "D", true, now);
            BenefitResponse r2 = new BenefitResponse(1L, "A", "B", 1, BenefitCategory.PARTNER, "C", "D", true, now);

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("Should implement toString")
        void shouldImplementToString() {
            BenefitResponse response = new BenefitResponse();
            response.setName("Test");

            assertNotNull(response.toString());
            assertTrue(response.toString().contains("Test"));
        }
    }
}
