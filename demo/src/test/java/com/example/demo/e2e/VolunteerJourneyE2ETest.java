package com.example.demo.e2e;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.integration.AbstractIntegrationTest;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("E2E: Complete Volunteer Journey")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VolunteerJourneyE2ETest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @BeforeEach
    void setUp() {
        redemptionRepository.deleteAll();
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Complete volunteer journey: Register -> Apply -> Participate -> Earn Points -> Redeem Benefit")
    void completeVolunteerJourney() {
        // === STEP 1: Register as Volunteer ===
        RegisterRequest volunteerRegister = new RegisterRequest();
        volunteerRegister.setName("John Volunteer");
        volunteerRegister.setEmail("john.volunteer@test.com");
        volunteerRegister.setPassword("password123");
        volunteerRegister.setUserType(UserType.VOLUNTEER);

        ResponseEntity<AuthResponse> volunteerAuth = restTemplate.postForEntity(
                "/api/auth/register", volunteerRegister, AuthResponse.class);

        assertThat(volunteerAuth.getBody().isSuccess()).isTrue();

        // === STEP 2: Create Volunteer Profile ===
        CreateVolunteerProfileRequest profileRequest = new CreateVolunteerProfileRequest();
        profileRequest.setName("John Volunteer");
        profileRequest.setEmail("john.volunteer@test.com");
        profileRequest.setPhone("123456789");
        profileRequest.setSkills("Java, Python, Teamwork");
        profileRequest.setInterests("Environment, Education");
        profileRequest.setAvailability("Weekends");
        profileRequest.setBio("Passionate about helping the community");

        ResponseEntity<VolunteerProfileResponse> profileResponse = restTemplate.postForEntity(
                "/api/volunteers/profile", profileRequest, VolunteerProfileResponse.class);

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long volunteerId = profileResponse.getBody().getId();
        assertThat(profileResponse.getBody().getTotalPoints()).isEqualTo(0);

        // === STEP 3: Promoter Creates Opportunity ===
        // First register promoter
        RegisterRequest promoterRegister = new RegisterRequest();
        promoterRegister.setName("Green NGO");
        promoterRegister.setEmail("green.ngo@test.com");
        promoterRegister.setPassword("password123");
        promoterRegister.setUserType(UserType.PROMOTER);

        restTemplate.postForEntity("/api/auth/register", promoterRegister, AuthResponse.class);

        // Create promoter profile
        CreatePromoterProfileRequest promoterProfileRequest = new CreatePromoterProfileRequest();
        promoterProfileRequest.setName("Green NGO");
        promoterProfileRequest.setEmail("green.ngo@test.com");
        promoterProfileRequest.setOrganization("Green Earth NGO");
        promoterProfileRequest.setDescription("Environmental protection organization");
        promoterProfileRequest.setAreaOfActivity("Environment");
        promoterProfileRequest.setOrganizationType("Non-Profit");

        ResponseEntity<PromoterProfileResponse> promoterProfile = restTemplate.postForEntity(
                "/api/promoters/profile", promoterProfileRequest, PromoterProfileResponse.class);

        Long promoterId = promoterProfile.getBody().getId();

        // Create opportunity
        CreateOpportunityRequest oppRequest = new CreateOpportunityRequest();
        oppRequest.setTitle("Beach Cleanup Campaign");
        oppRequest.setDescription("Help us clean the local beach");
        oppRequest.setSkills("Teamwork");
        oppRequest.setCategory("Environment");
        oppRequest.setDuration(4);
        oppRequest.setVacancies(20);
        oppRequest.setPoints(100);
        oppRequest.setPromoterId(promoterId);

        ResponseEntity<OpportunityResponse> oppResponse = restTemplate.postForEntity(
                "/api/opportunities", oppRequest, OpportunityResponse.class);

        assertThat(oppResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long opportunityId = oppResponse.getBody().getId();

        // === STEP 4: Volunteer Applies to Opportunity ===
        CreateApplicationRequest appRequest = new CreateApplicationRequest();
        appRequest.setOpportunityId(opportunityId);
        appRequest.setVolunteerEmail("john.volunteer@test.com");
        appRequest.setVolunteerName("John Volunteer");
        appRequest.setVolunteerPhone("123456789");
        appRequest.setMotivation("I care deeply about our environment");

        ResponseEntity<ApplicationResponse> appResponse = restTemplate.postForEntity(
                "/api/applications", appRequest, ApplicationResponse.class);

        assertThat(appResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(appResponse.getBody().getStatus()).isEqualTo(ApplicationStatus.PENDING);
        Long applicationId = appResponse.getBody().getId();

        // === STEP 5: Promoter Accepts Application ===
        // Use query parameter for status (not request body)
        ResponseEntity<ApplicationResponse> acceptResponse = restTemplate.exchange(
                "/api/applications/" + applicationId + "/status?status=ACCEPTED",
                HttpMethod.PATCH,
                null,
                ApplicationResponse.class);

        assertThat(acceptResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // === STEP 6: Promoter Concludes Opportunity (Awards Points) ===
        ConfirmParticipationRequest concludeRequest = new ConfirmParticipationRequest();
        concludeRequest.setPromoterId(promoterId);
        concludeRequest.setApplicationIds(Collections.singletonList(applicationId));

        ResponseEntity<ConcludeOpportunityResponse> concludeResponse = restTemplate.postForEntity(
                "/api/opportunities/" + opportunityId + "/conclude",
                concludeRequest,
                ConcludeOpportunityResponse.class);

        assertThat(concludeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(concludeResponse.getBody().getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        assertThat(concludeResponse.getBody().getTotalPointsAwarded()).isEqualTo(100);

        // Verify volunteer has points
        ResponseEntity<VolunteerPointsResponse> pointsResponse = restTemplate.getForEntity(
                "/api/volunteers/" + volunteerId + "/points", VolunteerPointsResponse.class);

        assertThat(pointsResponse.getBody().getTotalPoints()).isEqualTo(100);

        // === STEP 7: Partner Creates Benefit ===
        RegisterRequest partnerRegister = new RegisterRequest();
        partnerRegister.setName("Local Cafe");
        partnerRegister.setEmail("cafe@test.com");
        partnerRegister.setPassword("password123");
        partnerRegister.setUserType(UserType.PARTNER);

        restTemplate.postForEntity("/api/auth/register", partnerRegister, AuthResponse.class);

        CreateBenefitRequest benefitRequest = new CreateBenefitRequest();
        benefitRequest.setName("Free Coffee");
        benefitRequest.setDescription("One free coffee at Local Cafe");
        benefitRequest.setPointsRequired(50);
        benefitRequest.setProvider("Local Cafe");

        ResponseEntity<BenefitResponse> benefitResponse = restTemplate.postForEntity(
                "/api/benefits/partner", benefitRequest, BenefitResponse.class);

        Long benefitId = benefitResponse.getBody().getId();

        // === STEP 8: Volunteer Redeems Benefit ===
        RedeemPointsRequest redeemRequest = new RedeemPointsRequest();
        redeemRequest.setVolunteerId(volunteerId);
        redeemRequest.setBenefitId(benefitId);

        ResponseEntity<RedemptionResponse> redemptionResponse = restTemplate.postForEntity(
                "/api/redemptions", redeemRequest, RedemptionResponse.class);

        assertThat(redemptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(redemptionResponse.getBody().getStatus()).isEqualTo(Redemption.RedemptionStatus.COMPLETED);
        assertThat(redemptionResponse.getBody().getPointsSpent()).isEqualTo(50);

        // Verify volunteer's points were deducted
        ResponseEntity<VolunteerPointsResponse> finalPointsResponse = restTemplate.getForEntity(
                "/api/volunteers/" + volunteerId + "/points", VolunteerPointsResponse.class);

        assertThat(finalPointsResponse.getBody().getTotalPoints()).isEqualTo(50); // 100 - 50

        // === STEP 9: Verify Points History ===
        ResponseEntity<List<PointsHistoryResponse>> historyResponse = restTemplate.exchange(
                "/api/volunteers/" + volunteerId + "/points-history",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PointsHistoryResponse>>() {});

        assertThat(historyResponse.getBody()).hasSize(1);
        assertThat(historyResponse.getBody().get(0).getPointsAwarded()).isEqualTo(100);

        // === STEP 10: Verify Volunteer Ranking ===
        ResponseEntity<List<VolunteerPointsResponse>> rankingResponse = restTemplate.exchange(
                "/api/volunteers/ranking",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VolunteerPointsResponse>>() {});

        assertThat(rankingResponse.getBody()).isNotEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Multiple volunteers competing in ranking")
    void multipleVolunteersRanking() {
        // Create promoter
        Promoter promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("ranking.promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        // Create opportunity worth 100 points
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Description");
        opportunity.setSkills("skills");
        opportunity.setCategory("Test");
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity = opportunityRepository.save(opportunity);

        // Create 3 volunteers with different initial points
        Volunteer v1 = createVolunteer("ranking1@test.com", 50);
        Volunteer v2 = createVolunteer("ranking2@test.com", 150);
        Volunteer v3 = createVolunteer("ranking3@test.com", 100);

        // Check ranking order
        ResponseEntity<List<VolunteerPointsResponse>> rankingResponse = restTemplate.exchange(
                "/api/volunteers/ranking",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VolunteerPointsResponse>>() {});

        assertThat(rankingResponse.getBody()).hasSize(3);
        assertThat(rankingResponse.getBody().get(0).getTotalPoints()).isEqualTo(150);
        assertThat(rankingResponse.getBody().get(1).getTotalPoints()).isEqualTo(100);
        assertThat(rankingResponse.getBody().get(2).getTotalPoints()).isEqualTo(50);

        // Get top 2
        ResponseEntity<List<VolunteerPointsResponse>> top2Response = restTemplate.exchange(
                "/api/volunteers/top/2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VolunteerPointsResponse>>() {});

        assertThat(top2Response.getBody()).hasSize(2);
        assertThat(top2Response.getBody().get(0).getTotalPoints()).isEqualTo(150);
    }

    private Volunteer createVolunteer(String email, int points) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Test");
        volunteer.setEmail(email);
        volunteer.setTotalPoints(points);
        return volunteerRepository.save(volunteer);
    }
}
