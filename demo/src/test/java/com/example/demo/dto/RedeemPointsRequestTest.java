package com.example.demo.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RedeemPointsRequest DTO Tests")
class RedeemPointsRequestTest {

    private RedeemPointsRequest request;

    @BeforeEach
    void setUp() {
        request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with no-args constructor")
        void shouldCreateInstanceWithNoArgsConstructor() {
            RedeemPointsRequest newRequest = new RedeemPointsRequest();
            assertNotNull(newRequest);
        }

        @Test
        @DisplayName("Should create instance with all-args constructor")
        void shouldCreateInstanceWithAllArgsConstructor() {
            RedeemPointsRequest newRequest = new RedeemPointsRequest(1L, 2L);

            assertEquals(1L, newRequest.getVolunteerId());
            assertEquals(2L, newRequest.getBenefitId());
        }

        @Test
        @DisplayName("Should create instance with null values")
        void shouldCreateInstanceWithNullValues() {
            RedeemPointsRequest newRequest = new RedeemPointsRequest(null, null);

            assertNull(newRequest.getVolunteerId());
            assertNull(newRequest.getBenefitId());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {

        @Test
        @DisplayName("Should get and set volunteerId")
        void shouldGetAndSetVolunteerId() {
            request.setVolunteerId(100L);
            assertEquals(100L, request.getVolunteerId());
        }

        @Test
        @DisplayName("Should get and set benefitId")
        void shouldGetAndSetBenefitId() {
            request.setBenefitId(200L);
            assertEquals(200L, request.getBenefitId());
        }

        @Test
        @DisplayName("Should handle null volunteerId")
        void shouldHandleNullVolunteerId() {
            request.setVolunteerId(null);
            assertNull(request.getVolunteerId());
        }

        @Test
        @DisplayName("Should handle null benefitId")
        void shouldHandleNullBenefitId() {
            request.setBenefitId(null);
            assertNull(request.getBenefitId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            RedeemPointsRequest request1 = new RedeemPointsRequest(1L, 1L);
            RedeemPointsRequest request2 = new RedeemPointsRequest(1L, 1L);

            assertEquals(request1, request2);
            assertEquals(request1.hashCode(), request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when volunteerId is different")
        void shouldNotBeEqualWhenVolunteerIdIsDifferent() {
            RedeemPointsRequest request1 = new RedeemPointsRequest(1L, 1L);
            RedeemPointsRequest request2 = new RedeemPointsRequest(2L, 1L);

            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("Should not be equal when benefitId is different")
        void shouldNotBeEqualWhenBenefitIdIsDifferent() {
            RedeemPointsRequest request1 = new RedeemPointsRequest(1L, 1L);
            RedeemPointsRequest request2 = new RedeemPointsRequest(1L, 2L);

            assertNotEquals(request1, request2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(request, request);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, request);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", request);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include all fields in toString")
        void shouldIncludeAllFieldsInToString() {
            String toString = request.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("1"));
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            RedeemPointsRequest emptyRequest = new RedeemPointsRequest();
            String toString = emptyRequest.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for volunteerId")
        void shouldHandleLongMaxValueForVolunteerId() {
            request.setVolunteerId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, request.getVolunteerId());
        }

        @Test
        @DisplayName("Should handle Long.MAX_VALUE for benefitId")
        void shouldHandleLongMaxValueForBenefitId() {
            request.setBenefitId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, request.getBenefitId());
        }

        @Test
        @DisplayName("Should handle Long.MIN_VALUE for volunteerId")
        void shouldHandleLongMinValueForVolunteerId() {
            request.setVolunteerId(Long.MIN_VALUE);
            assertEquals(Long.MIN_VALUE, request.getVolunteerId());
        }

        @Test
        @DisplayName("Should handle Long.MIN_VALUE for benefitId")
        void shouldHandleLongMinValueForBenefitId() {
            request.setBenefitId(Long.MIN_VALUE);
            assertEquals(Long.MIN_VALUE, request.getBenefitId());
        }
    }
}
