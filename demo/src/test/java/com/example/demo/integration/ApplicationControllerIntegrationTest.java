package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application Controller Integration Tests")
class ApplicationControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private Promoter promoter;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();

        promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        opportunity = new Opportunity();
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(4);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity = opportunityRepository.save(opportunity);
    }

    @Nested
    @DisplayName("Create Application Tests")
    class CreateApplicationTests {

        @Test
        @DisplayName("Should create application with new volunteer")
        void shouldCreateApplicationWithNewVolunteer() {
            CreateApplicationRequest request = new CreateApplicationRequest();
            request.setOpportunityId(opportunity.getId());
            request.setVolunteerEmail("newvolunteer@test.com");
            request.setVolunteerName("New Volunteer");
            request.setVolunteerPhone("123456789");
            request.setVolunteerSkills("Java");
            request.setMotivation("I want to help");

            ResponseEntity<ApplicationResponse> response = restTemplate.postForEntity(
                    "/api/applications", request, ApplicationResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(ApplicationStatus.PENDING);

            // Verify volunteer was created
            assertThat(volunteerRepository.findByEmail("newvolunteer@test.com")).isPresent();
        }

        @Test
        @DisplayName("Should create application with existing volunteer")
        void shouldCreateApplicationWithExistingVolunteer() {
            // Create volunteer first
            Volunteer volunteer = new Volunteer();
            volunteer.setName("Existing Volunteer");
            volunteer.setEmail("existing@test.com");
            volunteer.setTotalPoints(0);
            volunteer = volunteerRepository.save(volunteer);

            CreateApplicationRequest request = new CreateApplicationRequest();
            request.setOpportunityId(opportunity.getId());
            request.setVolunteerEmail("existing@test.com");
            request.setVolunteerName("Existing Volunteer");
            request.setMotivation("I want to help");

            ResponseEntity<ApplicationResponse> response = restTemplate.postForEntity(
                    "/api/applications", request, ApplicationResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            // Verify no duplicate volunteer was created
            assertThat(volunteerRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should fail for duplicate application")
        void shouldFailForDuplicateApplication() {
            CreateApplicationRequest request = new CreateApplicationRequest();
            request.setOpportunityId(opportunity.getId());
            request.setVolunteerEmail("duplicate@test.com");
            request.setVolunteerName("Volunteer");
            request.setMotivation("Motivation");

            // First application
            restTemplate.postForEntity("/api/applications", request, ApplicationResponse.class);

            // Second application (same volunteer, same opportunity)
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/applications", request, String.class);

            // IllegalStateException returns CONFLICT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("Get Applications Tests")
    class GetApplicationsTests {

        @Test
        @DisplayName("Should get application by ID")
        void shouldGetApplicationById() {
            Application application = createAndSaveApplication("get@test.com");

            ResponseEntity<ApplicationResponse> response = restTemplate.getForEntity(
                    "/api/applications/" + application.getId(), ApplicationResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(application.getId());
        }

        @Test
        @DisplayName("Should get applications by opportunity")
        void shouldGetApplicationsByOpportunity() {
            createAndSaveApplication("app1@test.com");
            createAndSaveApplication("app2@test.com");

            ResponseEntity<List<ApplicationResponse>> response = restTemplate.exchange(
                    "/api/applications/opportunity/" + opportunity.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ApplicationResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should get applications by volunteer")
        void shouldGetApplicationsByVolunteer() {
            Application application = createAndSaveApplication("byvolunteer@test.com");

            ResponseEntity<List<ApplicationResponse>> response = restTemplate.exchange(
                    "/api/applications/volunteer/" + application.getVolunteer().getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ApplicationResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }

        @Test
        @DisplayName("Should get applications by promoter")
        void shouldGetApplicationsByPromoter() {
            createAndSaveApplication("bypromoter@test.com");

            ResponseEntity<List<ApplicationResponse>> response = restTemplate.exchange(
                    "/api/applications/promoter/" + promoter.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ApplicationResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update application status to ACCEPTED")
        void shouldUpdateStatusToAccepted() {
            Application application = createAndSaveApplication("accept@test.com");

            // Use query parameter for status (not request body)
            ResponseEntity<ApplicationResponse> response = restTemplate.exchange(
                    "/api/applications/" + application.getId() + "/status?status=ACCEPTED",
                    HttpMethod.PATCH,
                    null,
                    ApplicationResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // Verify in database
            Application updated = applicationRepository.findById(application.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        }

        @Test
        @DisplayName("Should update application status to REJECTED")
        void shouldUpdateStatusToRejected() {
            Application application = createAndSaveApplication("reject@test.com");

            ResponseEntity<ApplicationResponse> response = restTemplate.exchange(
                    "/api/applications/" + application.getId() + "/status?status=REJECTED",
                    HttpMethod.PATCH,
                    null,
                    ApplicationResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            Application updated = applicationRepository.findById(application.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        }
    }

    private Application createAndSaveApplication(String volunteerEmail) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Test Volunteer");
        volunteer.setEmail(volunteerEmail);
        volunteer.setTotalPoints(0);
        volunteer = volunteerRepository.save(volunteer);

        Application application = new Application();
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.PENDING);
        application.setMotivation("Test motivation");
        return applicationRepository.save(application);
    }
}
