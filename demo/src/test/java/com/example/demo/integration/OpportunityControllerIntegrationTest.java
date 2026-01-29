package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Opportunity Controller Integration Tests")
class OpportunityControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    private Promoter promoter;

    @BeforeEach
    void setUp() {
        // Delete in correct order respecting foreign key constraints
        redemptionRepository.deleteAll();
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();

        promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should create opportunity")
        void shouldCreateOpportunity() {
            CreateOpportunityRequest request = createOpportunityRequest();

            ResponseEntity<OpportunityResponse> response = restTemplate.postForEntity(
                    "/api/opportunities", request, OpportunityResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Beach Cleanup");
            assertThat(response.getBody().getStatus()).isEqualTo(OpportunityStatus.OPEN);
        }

        @Test
        @DisplayName("Should get opportunity by ID")
        void shouldGetOpportunityById() {
            Opportunity opportunity = createAndSaveOpportunity("Get Test", "Environment");

            ResponseEntity<OpportunityResponse> response = restTemplate.getForEntity(
                    "/api/opportunities/" + opportunity.getId(), OpportunityResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(opportunity.getId());
        }

        @Test
        @DisplayName("Should get all opportunities")
        void shouldGetAllOpportunities() {
            createAndSaveOpportunity("Opp 1", "Environment");
            createAndSaveOpportunity("Opp 2", "Social");

            ResponseEntity<List<OpportunityResponse>> response = restTemplate.exchange(
                    "/api/opportunities",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OpportunityResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should update opportunity")
        void shouldUpdateOpportunity() {
            Opportunity opportunity = createAndSaveOpportunity("Original", "Environment");

            UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
            updateRequest.setTitle("Updated Title");
            updateRequest.setDescription("Updated Description");
            updateRequest.setSkills("new skills");
            updateRequest.setCategory("Social");
            updateRequest.setDuration(8);
            updateRequest.setVacancies(20);
            updateRequest.setPoints(100);

            HttpEntity<UpdateOpportunityRequest> entity = new HttpEntity<>(updateRequest);
            ResponseEntity<OpportunityResponse> response = restTemplate.exchange(
                    "/api/opportunities/" + opportunity.getId(),
                    HttpMethod.PUT,
                    entity,
                    OpportunityResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
        }

        @Test
        @DisplayName("Should delete opportunity")
        void shouldDeleteOpportunity() {
            Opportunity opportunity = createAndSaveOpportunity("Delete Test", "Environment");

            restTemplate.delete("/api/opportunities/" + opportunity.getId());

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "/api/opportunities/" + opportunity.getId(), String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Filter and Search Tests")
    class FilterAndSearchTests {

        @Test
        @DisplayName("Should get opportunities by promoter")
        void shouldGetOpportunitiesByPromoter() {
            createAndSaveOpportunity("Opp 1", "Environment");
            createAndSaveOpportunity("Opp 2", "Social");

            ResponseEntity<List<OpportunityResponse>> response = restTemplate.exchange(
                    "/api/opportunities/promoter/" + promoter.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OpportunityResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should filter opportunities by category")
        void shouldFilterByCategory() {
            createAndSaveOpportunity("Env Opp", "Environment");
            createAndSaveOpportunity("Social Opp", "Social");

            ResponseEntity<List<OpportunityResponse>> response = restTemplate.exchange(
                    "/api/opportunities/filter?category=Environment",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OpportunityResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getCategory()).isEqualTo("Environment");
        }

        @Test
        @DisplayName("Should get all categories")
        void shouldGetAllCategories() {
            createAndSaveOpportunity("Opp 1", "Environment");
            createAndSaveOpportunity("Opp 2", "Social");
            createAndSaveOpportunity("Opp 3", "Education");

            ResponseEntity<List<String>> response = restTemplate.exchange(
                    "/api/opportunities/categories",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(3);
            assertThat(response.getBody()).contains("Environment", "Social", "Education");
        }

        @Test
        @DisplayName("Should get opportunities by status")
        void shouldGetOpportunitiesByStatus() {
            Opportunity open = createAndSaveOpportunity("Open", "Environment");
            Opportunity concluded = createAndSaveOpportunity("Concluded", "Social");
            concluded.setStatus(OpportunityStatus.CONCLUDED);
            opportunityRepository.save(concluded);

            ResponseEntity<List<OpportunityResponse>> response = restTemplate.exchange(
                    "/api/opportunities/status/OPEN",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OpportunityResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getStatus()).isEqualTo(OpportunityStatus.OPEN);
        }
    }

    @Nested
    @DisplayName("Conclude Opportunity Tests")
    class ConcludeOpportunityTests {

        @Test
        @DisplayName("Should conclude opportunity and award points")
        void shouldConcludeOpportunityAndAwardPoints() {
            // Create opportunity
            Opportunity opportunity = createAndSaveOpportunity("Conclude Test", "Environment");
            opportunity.setPoints(50);
            opportunity = opportunityRepository.save(opportunity);

            // Create volunteer
            Volunteer volunteer = new Volunteer();
            volunteer.setName("Volunteer");
            volunteer.setEmail("volunteer@test.com");
            volunteer.setTotalPoints(0);
            volunteer = volunteerRepository.save(volunteer);

            // Create accepted application
            Application application = new Application();
            application.setVolunteer(volunteer);
            application.setOpportunity(opportunity);
            application.setStatus(ApplicationStatus.ACCEPTED);
            application.setParticipationConfirmed(false);
            application = applicationRepository.save(application);

            // Conclude
            ConfirmParticipationRequest request = new ConfirmParticipationRequest();
            request.setPromoterId(promoter.getId());
            request.setApplicationIds(Collections.singletonList(application.getId()));

            ResponseEntity<ConcludeOpportunityResponse> response = restTemplate.postForEntity(
                    "/api/opportunities/" + opportunity.getId() + "/conclude",
                    request,
                    ConcludeOpportunityResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
            assertThat(response.getBody().getTotalPointsAwarded()).isEqualTo(50);

            // Verify volunteer points updated
            Volunteer updatedVolunteer = volunteerRepository.findById(volunteer.getId()).orElseThrow();
            assertThat(updatedVolunteer.getTotalPoints()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should fail to conclude with wrong promoter")
        void shouldFailToConcludeWithWrongPromoter() {
            Opportunity opportunity = createAndSaveOpportunity("Wrong Promoter", "Environment");

            // Create another promoter
            Promoter otherPromoter = new Promoter();
            otherPromoter.setName("Other");
            otherPromoter.setEmail("other@test.com");
            otherPromoter.setOrganization("Other Org");
            otherPromoter = promoterRepository.save(otherPromoter);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest();
            request.setPromoterId(otherPromoter.getId());
            request.setApplicationIds(Collections.emptyList());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/opportunities/" + opportunity.getId() + "/conclude",
                    request,
                    String.class);

            // IllegalStateException returns CONFLICT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    private CreateOpportunityRequest createOpportunityRequest() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Beach Cleanup");
        request.setDescription("Clean the beach");
        request.setSkills("teamwork");
        request.setCategory("Environment");
        request.setDuration(4);
        request.setVacancies(10);
        request.setPoints(50);
        request.setPromoterId(promoter.getId());
        return request;
    }

    private Opportunity createAndSaveOpportunity(String title, String category) {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle(title);
        opportunity.setDescription("Description");
        opportunity.setSkills("skills");
        opportunity.setCategory(category);
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(50);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        return opportunityRepository.save(opportunity);
    }
}
