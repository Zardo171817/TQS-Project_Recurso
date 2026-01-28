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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("E2E: Complete Promoter Journey")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PromoterJourneyE2ETest extends AbstractIntegrationTest {

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

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete promoter journey: Register -> Create Opportunities -> Manage Applications -> Conclude")
    void completePromoterJourney() {
        // === STEP 1: Register as Promoter ===
        RegisterRequest promoterRegister = new RegisterRequest();
        promoterRegister.setName("Community Center");
        promoterRegister.setEmail("community.center@test.com");
        promoterRegister.setPassword("password123");
        promoterRegister.setUserType(UserType.PROMOTER);

        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                "/api/auth/register", promoterRegister, AuthResponse.class);

        assertThat(authResponse.getBody().isSuccess()).isTrue();

        // === STEP 2: Create Promoter Profile ===
        CreatePromoterProfileRequest profileRequest = new CreatePromoterProfileRequest();
        profileRequest.setName("Community Center");
        profileRequest.setEmail("community.center@test.com");
        profileRequest.setOrganization("Local Community Center");
        profileRequest.setDescription("Serving the community since 1990");
        profileRequest.setPhone("123456789");
        profileRequest.setWebsite("https://community-center.org");
        profileRequest.setAreaOfActivity("Social, Education");
        profileRequest.setOrganizationType("Non-Profit");
        profileRequest.setFoundedYear("1990");

        ResponseEntity<PromoterProfileResponse> profileResponse = restTemplate.postForEntity(
                "/api/promoters/profile", profileRequest, PromoterProfileResponse.class);

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long promoterId = profileResponse.getBody().getId();

        // === STEP 3: Create Multiple Opportunities ===
        // Opportunity 1 - Education
        CreateOpportunityRequest eduOpp = createOpportunityRequest(
                "Tutoring Program", "Help students with homework",
                "Teaching, Patience", "Education", 50, promoterId);

        ResponseEntity<OpportunityResponse> eduResponse = restTemplate.postForEntity(
                "/api/opportunities", eduOpp, OpportunityResponse.class);

        Long eduOppId = eduResponse.getBody().getId();

        // Opportunity 2 - Social
        CreateOpportunityRequest socialOpp = createOpportunityRequest(
                "Elderly Visit", "Visit elderly at care home",
                "Communication", "Social", 30, promoterId);

        ResponseEntity<OpportunityResponse> socialResponse = restTemplate.postForEntity(
                "/api/opportunities", socialOpp, OpportunityResponse.class);

        Long socialOppId = socialResponse.getBody().getId();

        // === STEP 4: Verify Opportunities by Promoter ===
        ResponseEntity<List<OpportunityResponse>> promoterOpps = restTemplate.exchange(
                "/api/opportunities/promoter/" + promoterId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OpportunityResponse>>() {});

        assertThat(promoterOpps.getBody()).hasSize(2);

        // === STEP 5: Verify Filter by Category Works ===
        ResponseEntity<List<OpportunityResponse>> eduFiltered = restTemplate.exchange(
                "/api/opportunities/filter?category=Education",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OpportunityResponse>>() {});

        assertThat(eduFiltered.getBody()).hasSize(1);
        assertThat(eduFiltered.getBody().get(0).getTitle()).isEqualTo("Tutoring Program");

        // === STEP 6: Create Volunteers and Applications ===
        Volunteer v1 = createVolunteer("student1@test.com", "Student One");
        Volunteer v2 = createVolunteer("student2@test.com", "Student Two");
        Volunteer v3 = createVolunteer("helper@test.com", "Helper");

        // Applications for Education opportunity
        Application app1 = createApplication(v1, eduOppId, "I want to help students");
        Application app2 = createApplication(v2, eduOppId, "I have tutoring experience");

        // Application for Social opportunity
        Application app3 = createApplication(v3, socialOppId, "I love helping elderly");

        // === STEP 7: Get Applications by Promoter ===
        ResponseEntity<List<ApplicationResponse>> promoterApps = restTemplate.exchange(
                "/api/applications/promoter/" + promoterId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ApplicationResponse>>() {});

        assertThat(promoterApps.getBody()).hasSize(3);

        // === STEP 8: Accept Some Applications, Reject Others ===
        // Accept app1 and app3, reject app2
        updateApplicationStatus(app1.getId(), "ACCEPTED");
        updateApplicationStatus(app2.getId(), "REJECTED");
        updateApplicationStatus(app3.getId(), "ACCEPTED");

        // Verify accepted applications for education opportunity
        ResponseEntity<List<ApplicationResponse>> acceptedApps = restTemplate.exchange(
                "/api/opportunities/" + eduOppId + "/accepted-applications",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ApplicationResponse>>() {});

        assertThat(acceptedApps.getBody()).hasSize(1);

        // === STEP 9: Conclude Education Opportunity ===
        ConfirmParticipationRequest concludeRequest = new ConfirmParticipationRequest();
        concludeRequest.setPromoterId(promoterId);
        concludeRequest.setApplicationIds(Arrays.asList(app1.getId()));

        ResponseEntity<ConcludeOpportunityResponse> concludeResponse = restTemplate.postForEntity(
                "/api/opportunities/" + eduOppId + "/conclude",
                concludeRequest,
                ConcludeOpportunityResponse.class);

        assertThat(concludeResponse.getBody().getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        assertThat(concludeResponse.getBody().getTotalPointsAwarded()).isEqualTo(50);

        // === STEP 10: Verify Statistics ===
        // Count concluded opportunities
        ResponseEntity<Long> concludedCount = restTemplate.getForEntity(
                "/api/opportunities/promoter/" + promoterId + "/concluded-count",
                Long.class);

        assertThat(concludedCount.getBody()).isEqualTo(1L);

        // Get open opportunities
        ResponseEntity<List<OpportunityResponse>> openOpps = restTemplate.exchange(
                "/api/opportunities/promoter/" + promoterId + "/status/OPEN",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OpportunityResponse>>() {});

        assertThat(openOpps.getBody()).hasSize(1); // Social opportunity still open

        // Verify volunteer received points
        ResponseEntity<VolunteerPointsResponse> volunteerPoints = restTemplate.getForEntity(
                "/api/volunteers/" + v1.getId() + "/points",
                VolunteerPointsResponse.class);

        assertThat(volunteerPoints.getBody().getTotalPoints()).isEqualTo(50);
    }

    @Test
    @DisplayName("Promoter updates opportunity details")
    void promoterUpdatesOpportunity() {
        // Create promoter and opportunity
        Promoter promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("update.promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        CreateOpportunityRequest request = createOpportunityRequest(
                "Original Title", "Original Description",
                "Skills", "Category", 50, promoter.getId());

        ResponseEntity<OpportunityResponse> createResponse = restTemplate.postForEntity(
                "/api/opportunities", request, OpportunityResponse.class);

        Long opportunityId = createResponse.getBody().getId();

        // Update opportunity
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setSkills("New Skills");
        updateRequest.setCategory("New Category");
        updateRequest.setDuration(8);
        updateRequest.setVacancies(20);
        updateRequest.setPoints(100);

        HttpEntity<UpdateOpportunityRequest> entity = new HttpEntity<>(updateRequest);
        ResponseEntity<OpportunityResponse> updateResponse = restTemplate.exchange(
                "/api/opportunities/" + opportunityId,
                HttpMethod.PUT,
                entity,
                OpportunityResponse.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getTitle()).isEqualTo("Updated Title");
        assertThat(updateResponse.getBody().getPoints()).isEqualTo(100);
    }

    private CreateOpportunityRequest createOpportunityRequest(String title, String description,
            String skills, String category, int points, Long promoterId) {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setSkills(skills);
        request.setCategory(category);
        request.setDuration(4);
        request.setVacancies(10);
        request.setPoints(points);
        request.setPromoterId(promoterId);
        return request;
    }

    private Volunteer createVolunteer(String email, String name) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName(name);
        volunteer.setEmail(email);
        volunteer.setTotalPoints(0);
        return volunteerRepository.save(volunteer);
    }

    private Application createApplication(Volunteer volunteer, Long opportunityId, String motivation) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElseThrow();
        Application application = new Application();
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.PENDING);
        application.setMotivation(motivation);
        return applicationRepository.save(application);
    }

    private void updateApplicationStatus(Long applicationId, String status) {
        // Use query parameter for status
        restTemplate.exchange(
                "/api/applications/" + applicationId + "/status?status=" + status,
                HttpMethod.PATCH,
                null,
                ApplicationResponse.class);
    }
}
