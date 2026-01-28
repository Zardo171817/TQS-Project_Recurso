package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
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

@DisplayName("Benefit Controller Integration Tests")
class BenefitControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @BeforeEach
    void setUp() {
        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Nested
    @DisplayName("Get Benefits Tests")
    class GetBenefitsTests {

        @Test
        @DisplayName("Should get all active benefits")
        void shouldGetAllActiveBenefits() {
            createAndSaveBenefit("Active 1", BenefitCategory.UA, 100, true);
            createAndSaveBenefit("Active 2", BenefitCategory.PARTNER, 200, true);
            createAndSaveBenefit("Inactive", BenefitCategory.UA, 50, false);

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should get benefit by ID")
        void shouldGetBenefitById() {
            Benefit benefit = createAndSaveBenefit("Test", BenefitCategory.UA, 100, true);

            ResponseEntity<BenefitResponse> response = restTemplate.getForEntity(
                    "/api/benefits/" + benefit.getId(), BenefitResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo("Test");
        }

        @Test
        @DisplayName("Should get benefits by category")
        void shouldGetBenefitsByCategory() {
            createAndSaveBenefit("UA Benefit", BenefitCategory.UA, 100, true);
            createAndSaveBenefit("Partner Benefit", BenefitCategory.PARTNER, 200, true);

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits/category/UA",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getCategory()).isEqualTo(BenefitCategory.UA);
        }

        @Test
        @DisplayName("Should get affordable benefits for volunteer")
        void shouldGetAffordableBenefitsForVolunteer() {
            Volunteer volunteer = new Volunteer();
            volunteer.setName("Test");
            volunteer.setEmail("affordable@test.com");
            volunteer.setTotalPoints(150);
            volunteer = volunteerRepository.save(volunteer);

            createAndSaveBenefit("Affordable", BenefitCategory.UA, 100, true);
            createAndSaveBenefit("Expensive", BenefitCategory.UA, 200, true);

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits/volunteer/" + volunteer.getId() + "/affordable",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getPointsRequired()).isLessThanOrEqualTo(150);
        }

        @Test
        @DisplayName("Should get benefits by provider")
        void shouldGetBenefitsByProvider() {
            createAndSaveBenefit("UA Discount", BenefitCategory.UA, 100, true, "UA");
            createAndSaveBenefit("Partner Discount", BenefitCategory.PARTNER, 200, true, "Partner Store");

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits/provider/UA",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }

        @Test
        @DisplayName("Should get all providers")
        void shouldGetAllProviders() {
            createAndSaveBenefit("B1", BenefitCategory.UA, 100, true, "UA");
            createAndSaveBenefit("B2", BenefitCategory.PARTNER, 200, true, "Partner Store");

            ResponseEntity<List<String>> response = restTemplate.exchange(
                    "/api/benefits/providers",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("UA", "Partner Store");
        }
    }

    @Nested
    @DisplayName("Sorted Benefits Tests")
    class SortedBenefitsTests {

        @Test
        @DisplayName("Should get benefits sorted by points ascending")
        void shouldGetBenefitsSortedByPointsAsc() {
            createAndSaveBenefit("High", BenefitCategory.UA, 200, true);
            createAndSaveBenefit("Low", BenefitCategory.UA, 100, true);

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits/sorted/points-asc",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
            assertThat(response.getBody().get(0).getPointsRequired()).isEqualTo(100);
            assertThat(response.getBody().get(1).getPointsRequired()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should get benefits sorted by points descending")
        void shouldGetBenefitsSortedByPointsDesc() {
            createAndSaveBenefit("Low", BenefitCategory.UA, 100, true);
            createAndSaveBenefit("High", BenefitCategory.UA, 200, true);

            ResponseEntity<List<BenefitResponse>> response = restTemplate.exchange(
                    "/api/benefits/sorted/points-desc",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BenefitResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().get(0).getPointsRequired()).isEqualTo(200);
            assertThat(response.getBody().get(1).getPointsRequired()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("Partner Benefit CRUD Tests")
    class PartnerBenefitCrudTests {

        @Test
        @DisplayName("Should create partner benefit")
        void shouldCreatePartnerBenefit() {
            CreateBenefitRequest request = new CreateBenefitRequest();
            request.setName("New Partner Benefit");
            request.setDescription("Description");
            request.setPointsRequired(150);
            request.setProvider("New Partner");
            request.setImageUrl("http://example.com/image.jpg");

            ResponseEntity<BenefitResponse> response = restTemplate.postForEntity(
                    "/api/benefits/partner", request, BenefitResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getCategory()).isEqualTo(BenefitCategory.PARTNER);
            assertThat(response.getBody().getName()).isEqualTo("New Partner Benefit");
        }

        @Test
        @DisplayName("Should update partner benefit")
        void shouldUpdatePartnerBenefit() {
            Benefit benefit = createAndSaveBenefit("Partner", BenefitCategory.PARTNER, 100, true, "Partner");

            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Updated Name");
            request.setPointsRequired(200);

            HttpEntity<UpdateBenefitRequest> entity = new HttpEntity<>(request);
            ResponseEntity<BenefitResponse> response = restTemplate.exchange(
                    "/api/benefits/partner/" + benefit.getId(),
                    HttpMethod.PUT,
                    entity,
                    BenefitResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("Should fail to update UA benefit")
        void shouldFailToUpdateUaBenefit() {
            Benefit uaBenefit = createAndSaveBenefit("UA", BenefitCategory.UA, 100, true, "UA");

            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Updated");

            HttpEntity<UpdateBenefitRequest> entity = new HttpEntity<>(request);
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/benefits/partner/" + uaBenefit.getId(),
                    HttpMethod.PUT,
                    entity,
                    String.class);

            // IllegalStateException returns CONFLICT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("Should deactivate partner benefit")
        void shouldDeactivatePartnerBenefit() {
            Benefit benefit = createAndSaveBenefit("Deactivate", BenefitCategory.PARTNER, 100, true, "Partner");

            restTemplate.delete("/api/benefits/partner/" + benefit.getId());

            Benefit updated = benefitRepository.findById(benefit.getId()).orElseThrow();
            assertThat(updated.getActive()).isFalse();
        }
    }

    private Benefit createAndSaveBenefit(String name, BenefitCategory category, int points, boolean active) {
        return createAndSaveBenefit(name, category, points, active, "Default Provider");
    }

    private Benefit createAndSaveBenefit(String name, BenefitCategory category, int points, boolean active, String provider) {
        Benefit benefit = new Benefit();
        benefit.setName(name);
        benefit.setDescription("Description for " + name);
        benefit.setPointsRequired(points);
        benefit.setCategory(category);
        benefit.setProvider(provider);
        benefit.setActive(active);
        return benefitRepository.save(benefit);
    }
}
