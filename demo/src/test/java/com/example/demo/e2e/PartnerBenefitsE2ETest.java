package com.example.demo.e2e;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.integration.AbstractIntegrationTest;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("E2E: Partner Benefits Management")
class PartnerBenefitsE2ETest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete partner journey: Register -> Create Benefits -> Track Redemptions -> View Stats")
    void completePartnerJourney() {
        // === STEP 1: Register as Partner ===
        RegisterRequest partnerRegister = new RegisterRequest();
        partnerRegister.setName("Coffee Shop");
        partnerRegister.setEmail("coffee.shop@test.com");
        partnerRegister.setPassword("password123");
        partnerRegister.setUserType(UserType.PARTNER);

        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                "/api/auth/register", partnerRegister, AuthResponse.class);

        assertThat(authResponse.getBody().isSuccess()).isTrue();

        // === STEP 2: Create Multiple Partner Benefits ===
        CreateBenefitRequest benefit1 = createBenefitRequest("Free Coffee", "One free coffee", 50);
        CreateBenefitRequest benefit2 = createBenefitRequest("20% Discount", "20% off any purchase", 100);
        CreateBenefitRequest benefit3 = createBenefitRequest("Free Meal", "One free meal", 200);

        ResponseEntity<BenefitResponse> b1Response = restTemplate.postForEntity(
                "/api/benefits/partner", benefit1, BenefitResponse.class);
        ResponseEntity<BenefitResponse> b2Response = restTemplate.postForEntity(
                "/api/benefits/partner", benefit2, BenefitResponse.class);
        ResponseEntity<BenefitResponse> b3Response = restTemplate.postForEntity(
                "/api/benefits/partner", benefit3, BenefitResponse.class);

        Long benefitId1 = b1Response.getBody().getId();
        Long benefitId2 = b2Response.getBody().getId();
        Long benefitId3 = b3Response.getBody().getId();

        assertThat(b1Response.getBody().getCategory()).isEqualTo(BenefitCategory.PARTNER);

        // === STEP 3: Verify Partner Benefits Listing ===
        ResponseEntity<List<BenefitResponse>> partnerBenefits = restTemplate.exchange(
                "/api/benefits/partner",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(partnerBenefits.getBody()).hasSize(3);

        // === STEP 4: Update a Benefit ===
        UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
        updateRequest.setName("Premium Free Coffee");
        updateRequest.setDescription("One premium coffee on us");
        updateRequest.setPointsRequired(60);

        HttpEntity<UpdateBenefitRequest> updateEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<BenefitResponse> updateResponse = restTemplate.exchange(
                "/api/benefits/partner/" + benefitId1,
                HttpMethod.PUT,
                updateEntity,
                BenefitResponse.class);

        assertThat(updateResponse.getBody().getName()).isEqualTo("Premium Free Coffee");
        assertThat(updateResponse.getBody().getPointsRequired()).isEqualTo(60);

        // === STEP 5: Create Volunteers with Different Points ===
        Volunteer v1 = createVolunteer("rich@test.com", 500);
        Volunteer v2 = createVolunteer("medium@test.com", 150);
        Volunteer v3 = createVolunteer("poor@test.com", 30);

        // === STEP 6: Test Affordable Benefits for Each Volunteer ===
        ResponseEntity<List<BenefitResponse>> affordableForRich = restTemplate.exchange(
                "/api/benefits/volunteer/" + v1.getId() + "/affordable",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(affordableForRich.getBody()).hasSize(3); // Can afford all

        ResponseEntity<List<BenefitResponse>> affordableForMedium = restTemplate.exchange(
                "/api/benefits/volunteer/" + v2.getId() + "/affordable",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(affordableForMedium.getBody()).hasSize(2); // Can afford 60 and 100 point benefits

        ResponseEntity<List<BenefitResponse>> affordableForPoor = restTemplate.exchange(
                "/api/benefits/volunteer/" + v3.getId() + "/affordable",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(affordableForPoor.getBody()).isEmpty(); // Cannot afford any

        // === STEP 7: Volunteers Redeem Benefits ===
        // Rich volunteer redeems expensive benefit
        RedeemPointsRequest redeem1 = new RedeemPointsRequest();
        redeem1.setVolunteerId(v1.getId());
        redeem1.setBenefitId(benefitId3); // 200 points

        ResponseEntity<RedemptionResponse> redemption1 = restTemplate.postForEntity(
                "/api/redemptions", redeem1, RedemptionResponse.class);

        assertThat(redemption1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Rich volunteer redeems another benefit
        RedeemPointsRequest redeem2 = new RedeemPointsRequest();
        redeem2.setVolunteerId(v1.getId());
        redeem2.setBenefitId(benefitId2); // 100 points

        ResponseEntity<RedemptionResponse> redemption2 = restTemplate.postForEntity(
                "/api/redemptions", redeem2, RedemptionResponse.class);

        assertThat(redemption2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Medium volunteer redeems a benefit
        RedeemPointsRequest redeem3 = new RedeemPointsRequest();
        redeem3.setVolunteerId(v2.getId());
        redeem3.setBenefitId(benefitId1); // 60 points

        ResponseEntity<RedemptionResponse> redemption3 = restTemplate.postForEntity(
                "/api/redemptions", redeem3, RedemptionResponse.class);

        assertThat(redemption3.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // === STEP 8: Partner Views Redemption Stats ===
        ResponseEntity<PartnerRedemptionStatsResponse> stats = restTemplate.getForEntity(
                "/api/redemptions/partner/Coffee Shop/stats",
                PartnerRedemptionStatsResponse.class);

        assertThat(stats.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stats.getBody().getTotalBenefits()).isEqualTo(3);
        assertThat(stats.getBody().getTotalRedemptions()).isEqualTo(3L);
        assertThat(stats.getBody().getTotalPointsRedeemed()).isEqualTo(360L); // 200 + 100 + 60

        // === STEP 9: Deactivate a Benefit ===
        restTemplate.delete("/api/benefits/partner/" + benefitId3);

        // Verify it's no longer active
        ResponseEntity<List<BenefitResponse>> activeBenefits = restTemplate.exchange(
                "/api/benefits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(activeBenefits.getBody()).hasSize(2);

        // === STEP 10: Verify Sorted Benefits ===
        ResponseEntity<List<BenefitResponse>> sortedAsc = restTemplate.exchange(
                "/api/benefits/sorted/points-asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(sortedAsc.getBody().get(0).getPointsRequired())
                .isLessThan(sortedAsc.getBody().get(1).getPointsRequired());
    }

    @Test
    @DisplayName("Benefits catalog shows all benefits sorted for volunteer")
    void benefitsCatalogForVolunteer() {
        // Create benefits with different point requirements
        createBenefit("Cheap", 50);
        createBenefit("Medium", 100);
        createBenefit("Expensive", 200);

        Volunteer volunteer = createVolunteer("catalog@test.com", 150);

        // Get catalog
        ResponseEntity<List<BenefitResponse>> catalog = restTemplate.exchange(
                "/api/benefits/volunteer/" + volunteer.getId() + "/catalog",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(catalog.getBody()).hasSize(3);
        // Should be sorted by points required
        assertThat(catalog.getBody().get(0).getPointsRequired()).isEqualTo(50);
        assertThat(catalog.getBody().get(1).getPointsRequired()).isEqualTo(100);
        assertThat(catalog.getBody().get(2).getPointsRequired()).isEqualTo(200);
    }

    @Test
    @DisplayName("UA and Partner benefits are separated by category")
    void benefitsCategorySeparation() {
        // Create UA benefits (manually since createPartnerBenefit forces PARTNER category)
        Benefit uaBenefit = new Benefit();
        uaBenefit.setName("UA Discount");
        uaBenefit.setDescription("Discount at UA facilities");
        uaBenefit.setPointsRequired(50);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("UA");
        uaBenefit.setActive(true);
        benefitRepository.save(uaBenefit);

        // Create Partner benefit via API
        CreateBenefitRequest partnerRequest = createBenefitRequest("Partner Discount", "External partner", 100);
        restTemplate.postForEntity("/api/benefits/partner", partnerRequest, BenefitResponse.class);

        // Get UA benefits
        ResponseEntity<List<BenefitResponse>> uaBenefits = restTemplate.exchange(
                "/api/benefits/category/UA",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(uaBenefits.getBody()).hasSize(1);
        assertThat(uaBenefits.getBody().get(0).getCategory()).isEqualTo(BenefitCategory.UA);

        // Get Partner benefits
        ResponseEntity<List<BenefitResponse>> partnerBenefits = restTemplate.exchange(
                "/api/benefits/category/PARTNER",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BenefitResponse>>() {});

        assertThat(partnerBenefits.getBody()).hasSize(1);
        assertThat(partnerBenefits.getBody().get(0).getCategory()).isEqualTo(BenefitCategory.PARTNER);
    }

    private CreateBenefitRequest createBenefitRequest(String name, String description, int points) {
        CreateBenefitRequest request = new CreateBenefitRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPointsRequired(points);
        request.setProvider("Coffee Shop");
        return request;
    }

    private void createBenefit(String name, int points) {
        Benefit benefit = new Benefit();
        benefit.setName(name);
        benefit.setDescription("Description for " + name);
        benefit.setPointsRequired(points);
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider("Test Provider");
        benefit.setActive(true);
        benefitRepository.save(benefit);
    }

    private Volunteer createVolunteer(String email, int points) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Test");
        volunteer.setEmail(email);
        volunteer.setTotalPoints(points);
        return volunteerRepository.save(volunteer);
    }
}
