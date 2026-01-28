package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Redemption Controller Integration Tests")
class RedemptionControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private Volunteer volunteer;
    private Benefit benefit;

    @BeforeEach
    void setUp() {
        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();

        volunteer = new Volunteer();
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("volunteer@test.com");
        volunteer.setTotalPoints(500);
        volunteer = volunteerRepository.save(volunteer);

        benefit = new Benefit();
        benefit.setName("Test Benefit");
        benefit.setDescription("Test Description");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider("Test Provider");
        benefit.setActive(true);
        benefit = benefitRepository.save(benefit);
    }

    @Nested
    @DisplayName("Redeem Points Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should redeem points successfully")
        void shouldRedeemPointsSuccessfully() {
            RedeemPointsRequest request = new RedeemPointsRequest();
            request.setVolunteerId(volunteer.getId());
            request.setBenefitId(benefit.getId());

            ResponseEntity<RedemptionResponse> response = restTemplate.postForEntity(
                    "/api/redemptions", request, RedemptionResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPointsSpent()).isEqualTo(100);
            assertThat(response.getBody().getStatus()).isEqualTo(RedemptionStatus.COMPLETED);

            // Verify volunteer points were deducted
            Volunteer updated = volunteerRepository.findById(volunteer.getId()).orElseThrow();
            assertThat(updated.getTotalPoints()).isEqualTo(400); // 500 - 100
        }

        @Test
        @DisplayName("Should fail when insufficient points")
        void shouldFailWhenInsufficientPoints() {
            volunteer.setTotalPoints(50); // Less than required 100
            volunteerRepository.save(volunteer);

            RedeemPointsRequest request = new RedeemPointsRequest();
            request.setVolunteerId(volunteer.getId());
            request.setBenefitId(benefit.getId());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/redemptions", request, String.class);

            // IllegalStateException returns CONFLICT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("Should fail when benefit is inactive")
        void shouldFailWhenBenefitIsInactive() {
            benefit.setActive(false);
            benefitRepository.save(benefit);

            RedeemPointsRequest request = new RedeemPointsRequest();
            request.setVolunteerId(volunteer.getId());
            request.setBenefitId(benefit.getId());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/redemptions", request, String.class);

            // IllegalStateException returns CONFLICT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("Should fail when volunteer not found")
        void shouldFailWhenVolunteerNotFound() {
            RedeemPointsRequest request = new RedeemPointsRequest();
            request.setVolunteerId(99999L);
            request.setBenefitId(benefit.getId());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/redemptions", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Get Redemptions Tests")
    class GetRedemptionsTests {

        @Test
        @DisplayName("Should get redemption by ID")
        void shouldGetRedemptionById() {
            Redemption redemption = createAndSaveRedemption();

            ResponseEntity<RedemptionResponse> response = restTemplate.getForEntity(
                    "/api/redemptions/" + redemption.getId(), RedemptionResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(redemption.getId());
        }

        @Test
        @DisplayName("Should get redemptions by volunteer")
        void shouldGetRedemptionsByVolunteer() {
            createAndSaveRedemption();
            createAndSaveRedemption();

            ResponseEntity<List<RedemptionResponse>> response = restTemplate.exchange(
                    "/api/redemptions/volunteer/" + volunteer.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RedemptionResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should get completed redemptions by volunteer")
        void shouldGetCompletedRedemptionsByVolunteer() {
            createAndSaveRedemption();

            ResponseEntity<List<RedemptionResponse>> response = restTemplate.exchange(
                    "/api/redemptions/volunteer/" + volunteer.getId() + "/completed",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RedemptionResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should get total points spent")
        void shouldGetTotalPointsSpent() {
            createAndSaveRedemption();
            createAndSaveRedemption();

            ResponseEntity<Integer> response = restTemplate.getForEntity(
                    "/api/redemptions/volunteer/" + volunteer.getId() + "/total-spent",
                    Integer.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(200); // 2 x 100
        }

        @Test
        @DisplayName("Should get redemption count")
        void shouldGetRedemptionCount() {
            createAndSaveRedemption();
            createAndSaveRedemption();
            createAndSaveRedemption();

            ResponseEntity<Long> response = restTemplate.getForEntity(
                    "/api/redemptions/volunteer/" + volunteer.getId() + "/count",
                    Long.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(3L);
        }

        @Test
        @DisplayName("Should get redemptions by provider")
        void shouldGetRedemptionsByProvider() {
            createAndSaveRedemption();

            ResponseEntity<List<RedemptionResponse>> response = restTemplate.exchange(
                    "/api/redemptions/partner/Test Provider",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RedemptionResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should get partner redemption stats")
        void shouldGetPartnerRedemptionStats() {
            createAndSaveRedemption();
            createAndSaveRedemption();

            ResponseEntity<PartnerRedemptionStatsResponse> response = restTemplate.getForEntity(
                    "/api/redemptions/partner/Test Provider/stats",
                    PartnerRedemptionStatsResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getProvider()).isEqualTo("Test Provider");
            assertThat(response.getBody().getTotalRedemptions()).isEqualTo(2L);
            assertThat(response.getBody().getTotalPointsRedeemed()).isEqualTo(200L);
        }
    }

    private Redemption createAndSaveRedemption() {
        Redemption redemption = new Redemption();
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefit);
        redemption.setPointsSpent(100);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        return redemptionRepository.save(redemption);
    }
}
