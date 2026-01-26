package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@DisplayName("Conclude Opportunity Integration Tests")
class ConcludeOpportunityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Promoter promoter;
    private Opportunity opportunity;
    private Volunteer volunteer;
    private Application application;

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
        opportunity.setTitle("Beach Cleanup");
        opportunity.setDescription("Help clean the beach");
        opportunity.setSkills("Cleaning");
        opportunity.setCategory("Environment");
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());
        opportunity = opportunityRepository.save(opportunity);

        volunteer = new Volunteer();
        volunteer.setName("John Doe");
        volunteer.setEmail("john@test.com");
        volunteer.setPhone("987654321");
        volunteer.setSkills("Cleaning");
        volunteer.setTotalPoints(0);
        volunteer = volunteerRepository.save(volunteer);

        application = new Application();
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("I want to help");
        application.setAppliedAt(LocalDateTime.now());
        application.setParticipationConfirmed(false);
        application.setPointsAwarded(0);
        application = applicationRepository.save(application);
    }

    @Nested
    @DisplayName("Conclude Opportunity Full Flow Tests")
    class ConcludeOpportunityFlowTests {

        @Test
        @DisplayName("Should conclude opportunity and award points to volunteer")
        void concludeOpportunity_FullFlow_Success() throws Exception {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    promoter.getId(), Collections.singletonList(application.getId()));

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CONCLUDED"))
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(1))
                    .andExpect(jsonPath("$.totalPointsAwarded").value(100));

            Opportunity updatedOpportunity = opportunityRepository.findById(opportunity.getId()).orElseThrow();
            assertThat(updatedOpportunity.getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
            assertThat(updatedOpportunity.getConcludedAt()).isNotNull();

            Volunteer updatedVolunteer = volunteerRepository.findById(volunteer.getId()).orElseThrow();
            assertThat(updatedVolunteer.getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should conclude opportunity with multiple volunteers")
        void concludeOpportunity_MultipleVolunteers_Success() throws Exception {
            Volunteer volunteer2 = new Volunteer();
            volunteer2.setName("Jane Doe");
            volunteer2.setEmail("jane@test.com");
            volunteer2.setPhone("123123123");
            volunteer2.setSkills("Organizing");
            volunteer2.setTotalPoints(50);
            volunteer2 = volunteerRepository.save(volunteer2);

            Application application2 = new Application();
            application2.setVolunteer(volunteer2);
            application2.setOpportunity(opportunity);
            application2.setStatus(ApplicationStatus.ACCEPTED);
            application2.setMotivation("Love the environment");
            application2.setAppliedAt(LocalDateTime.now());
            application2.setParticipationConfirmed(false);
            application2.setPointsAwarded(0);
            application2 = applicationRepository.save(application2);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    promoter.getId(), Arrays.asList(application.getId(), application2.getId()));

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(2))
                    .andExpect(jsonPath("$.totalPointsAwarded").value(200))
                    .andExpect(jsonPath("$.confirmedParticipants", hasSize(2)));

            Volunteer updatedVolunteer2 = volunteerRepository.findById(volunteer2.getId()).orElseThrow();
            assertThat(updatedVolunteer2.getTotalPoints()).isEqualTo(150);
        }

        @Test
        @DisplayName("Should fail to conclude when wrong promoter")
        void concludeOpportunity_WrongPromoter_Fails() throws Exception {
            Promoter otherPromoter = new Promoter();
            otherPromoter.setName("Other Promoter");
            otherPromoter.setEmail("other@test.com");
            otherPromoter.setOrganization("Other Org");
            otherPromoter = promoterRepository.save(otherPromoter);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    otherPromoter.getId(), Collections.singletonList(application.getId()));

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should fail to conclude already concluded opportunity")
        void concludeOpportunity_AlreadyConcluded_Fails() throws Exception {
            opportunity.setStatus(OpportunityStatus.CONCLUDED);
            opportunity.setConcludedAt(LocalDateTime.now());
            opportunityRepository.save(opportunity);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    promoter.getId(), Collections.singletonList(application.getId()));

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should conclude with empty application list")
        void concludeOpportunity_EmptyList_Success() throws Exception {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    promoter.getId(), Collections.emptyList());

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(0))
                    .andExpect(jsonPath("$.totalPointsAwarded").value(0))
                    .andExpect(jsonPath("$.status").value("CONCLUDED"));
        }

        @Test
        @DisplayName("Should fail when application is rejected")
        void concludeOpportunity_RejectedApplication_Fails() throws Exception {
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                    promoter.getId(), Collections.singletonList(application.getId()));

            mockMvc.perform(post("/api/opportunities/{id}/conclude", opportunity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Confirm Individual Participation Tests")
    class ConfirmParticipationFlowTests {

        @Test
        @DisplayName("Should confirm individual participation")
        void confirmParticipation_Success() throws Exception {
            mockMvc.perform(post("/api/opportunities/applications/{id}/confirm-participation", application.getId())
                            .param("promoterId", promoter.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.participationConfirmed").value(true))
                    .andExpect(jsonPath("$.pointsAwarded").value(100));

            Application updatedApplication = applicationRepository.findById(application.getId()).orElseThrow();
            assertThat(updatedApplication.getParticipationConfirmed()).isTrue();
            assertThat(updatedApplication.getConfirmedAt()).isNotNull();

            Volunteer updatedVolunteer = volunteerRepository.findById(volunteer.getId()).orElseThrow();
            assertThat(updatedVolunteer.getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should fail when application is pending")
        void confirmParticipation_Pending_Fails() throws Exception {
            application.setStatus(ApplicationStatus.PENDING);
            applicationRepository.save(application);

            mockMvc.perform(post("/api/opportunities/applications/{id}/confirm-participation", application.getId())
                            .param("promoterId", promoter.getId().toString()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should fail when application is rejected")
        void confirmParticipation_Rejected_Fails() throws Exception {
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);

            mockMvc.perform(post("/api/opportunities/applications/{id}/confirm-participation", application.getId())
                            .param("promoterId", promoter.getId().toString()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should fail when already confirmed")
        void confirmParticipation_AlreadyConfirmed_Fails() throws Exception {
            application.setParticipationConfirmed(true);
            application.setPointsAwarded(100);
            application.setConfirmedAt(LocalDateTime.now());
            applicationRepository.save(application);

            mockMvc.perform(post("/api/opportunities/applications/{id}/confirm-participation", application.getId())
                            .param("promoterId", promoter.getId().toString()))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Get Opportunities By Status Tests")
    class GetByStatusFlowTests {

        @Test
        @DisplayName("Should return open opportunities")
        void getByStatus_Open_Success() throws Exception {
            mockMvc.perform(get("/api/opportunities/status/OPEN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("OPEN"));
        }

        @Test
        @DisplayName("Should return concluded opportunities")
        void getByStatus_Concluded_Success() throws Exception {
            opportunity.setStatus(OpportunityStatus.CONCLUDED);
            opportunity.setConcludedAt(LocalDateTime.now());
            opportunityRepository.save(opportunity);

            mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("CONCLUDED"));
        }

        @Test
        @DisplayName("Should return empty list when no concluded opportunities")
        void getByStatus_NoConcluded_Empty() throws Exception {
            mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Get By Promoter And Status Tests")
    class GetByPromoterAndStatusFlowTests {

        @Test
        @DisplayName("Should return open opportunities for promoter")
        void getByPromoterAndStatus_Open_Success() throws Exception {
            mockMvc.perform(get("/api/opportunities/promoter/{promoterId}/status/{status}", promoter.getId(), "OPEN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("OPEN"));
        }

        @Test
        @DisplayName("Should return empty list when no matching opportunities")
        void getByPromoterAndStatus_NoMatch_Empty() throws Exception {
            mockMvc.perform(get("/api/opportunities/promoter/{promoterId}/status/{status}", promoter.getId(), "CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Get Accepted Applications Tests")
    class GetAcceptedApplicationsFlowTests {

        @Test
        @DisplayName("Should return accepted applications for opportunity")
        void getAcceptedApplications_Success() throws Exception {
            mockMvc.perform(get("/api/opportunities/{id}/accepted-applications", opportunity.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent opportunity")
        void getAcceptedApplications_NotFound() throws Exception {
            mockMvc.perform(get("/api/opportunities/99999/accepted-applications"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty when no accepted applications")
        void getAcceptedApplications_Empty() throws Exception {
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);

            mockMvc.perform(get("/api/opportunities/{id}/accepted-applications", opportunity.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Count Concluded Opportunities Tests")
    class CountConcludedFlowTests {

        @Test
        @DisplayName("Should count concluded opportunities by promoter")
        void countConcluded_Success() throws Exception {
            opportunity.setStatus(OpportunityStatus.CONCLUDED);
            opportunity.setConcludedAt(LocalDateTime.now());
            opportunityRepository.save(opportunity);

            mockMvc.perform(get("/api/opportunities/promoter/{id}/concluded-count", promoter.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1"));
        }

        @Test
        @DisplayName("Should return zero when no concluded opportunities")
        void countConcluded_Zero() throws Exception {
            mockMvc.perform(get("/api/opportunities/promoter/{id}/concluded-count", promoter.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent promoter")
        void countConcluded_PromoterNotFound() throws Exception {
            mockMvc.perform(get("/api/opportunities/promoter/99999/concluded-count"))
                    .andExpect(status().isNotFound());
        }
    }
}
