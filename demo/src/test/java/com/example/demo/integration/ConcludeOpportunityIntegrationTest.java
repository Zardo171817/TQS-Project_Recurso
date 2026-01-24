package com.example.demo.integration;

import com.example.demo.dto.ConfirmParticipationRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
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

    private Promoter testPromoter;
    private Opportunity testOpportunity;
    private Volunteer testVolunteer;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();

        testPromoter = new Promoter();
        testPromoter.setName("Integration Test Promoter");
        testPromoter.setEmail("integration@test.com");
        testPromoter.setOrganization("Integration Test Org");
        testPromoter = promoterRepository.save(testPromoter);

        testOpportunity = new Opportunity();
        testOpportunity.setTitle("Test Opportunity for Conclusion");
        testOpportunity.setDescription("This opportunity will be concluded");
        testOpportunity.setSkills("Java, Testing");
        testOpportunity.setCategory("Tecnologia");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(150);
        testOpportunity.setStatus(OpportunityStatus.OPEN);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity = opportunityRepository.save(testOpportunity);

        testVolunteer = new Volunteer();
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@integration.com");
        testVolunteer.setTotalPoints(0);
        testVolunteer = volunteerRepository.save(testVolunteer);

        testApplication = new Application();
        testApplication.setVolunteer(testVolunteer);
        testApplication.setOpportunity(testOpportunity);
        testApplication.setStatus(ApplicationStatus.ACCEPTED);
        testApplication.setParticipationConfirmed(false);
        testApplication.setPointsAwarded(0);
        testApplication.setMotivation("I want to participate");
        testApplication = applicationRepository.save(testApplication);
    }

    @Test
    void whenConcludeOpportunity_thenOpportunityConcludedAndPointsAwarded() throws Exception {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(testPromoter.getId());
        request.setApplicationIds(Arrays.asList(testApplication.getId()));

        mockMvc.perform(post("/api/opportunities/" + testOpportunity.getId() + "/conclude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUDED"))
                .andExpect(jsonPath("$.totalParticipantsConfirmed").value(1))
                .andExpect(jsonPath("$.totalPointsAwarded").value(150));

        Opportunity concluded = opportunityRepository.findById(testOpportunity.getId()).orElseThrow();
        assertEquals(OpportunityStatus.CONCLUDED, concluded.getStatus());
        assertNotNull(concluded.getConcludedAt());

        Volunteer updated = volunteerRepository.findById(testVolunteer.getId()).orElseThrow();
        assertEquals(150, updated.getTotalPoints());

        Application updatedApp = applicationRepository.findById(testApplication.getId()).orElseThrow();
        assertTrue(updatedApp.getParticipationConfirmed());
        assertEquals(150, updatedApp.getPointsAwarded());
    }

    @Test
    void whenGetOpenOpportunities_thenReturnOnlyOpen() throws Exception {
        Opportunity concludedOpp = new Opportunity();
        concludedOpp.setTitle("Concluded Opportunity");
        concludedOpp.setDescription("Already concluded");
        concludedOpp.setSkills("Python");
        concludedOpp.setCategory("Data");
        concludedOpp.setDuration(5);
        concludedOpp.setVacancies(3);
        concludedOpp.setPoints(100);
        concludedOpp.setStatus(OpportunityStatus.CONCLUDED);
        concludedOpp.setPromoter(testPromoter);
        opportunityRepository.save(concludedOpp);

        mockMvc.perform(get("/api/opportunities/status/OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Opportunity for Conclusion"));
    }

    @Test
    void whenGetConcludedOpportunities_thenReturnOnlyConcluded() throws Exception {
        testOpportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunityRepository.save(testOpportunity);

        mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("CONCLUDED"));
    }

    @Test
    void whenGetAcceptedApplications_thenReturnOnlyAccepted() throws Exception {
        Volunteer secondVolunteer = new Volunteer();
        secondVolunteer.setName("Pending Volunteer");
        secondVolunteer.setEmail("pending@test.com");
        secondVolunteer.setTotalPoints(0);
        secondVolunteer = volunteerRepository.save(secondVolunteer);

        Application pendingApp = new Application();
        pendingApp.setVolunteer(secondVolunteer);
        pendingApp.setOpportunity(testOpportunity);
        pendingApp.setStatus(ApplicationStatus.PENDING);
        pendingApp.setParticipationConfirmed(false);
        pendingApp.setPointsAwarded(0);
        applicationRepository.save(pendingApp);

        mockMvc.perform(get("/api/opportunities/" + testOpportunity.getId() + "/accepted-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    @Test
    void whenConfirmParticipation_thenPointsAwarded() throws Exception {
        mockMvc.perform(post("/api/opportunities/applications/" + testApplication.getId() + "/confirm-participation")
                        .param("promoterId", testPromoter.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participationConfirmed").value(true))
                .andExpect(jsonPath("$.pointsAwarded").value(150));

        Volunteer updated = volunteerRepository.findById(testVolunteer.getId()).orElseThrow();
        assertEquals(150, updated.getTotalPoints());
    }

    @Test
    void whenConcludeWithMultipleVolunteers_thenAllGetPoints() throws Exception {
        Volunteer secondVolunteer = new Volunteer();
        secondVolunteer.setName("Second Volunteer");
        secondVolunteer.setEmail("second@integration.com");
        secondVolunteer.setTotalPoints(50);
        secondVolunteer = volunteerRepository.save(secondVolunteer);

        Application secondApp = new Application();
        secondApp.setVolunteer(secondVolunteer);
        secondApp.setOpportunity(testOpportunity);
        secondApp.setStatus(ApplicationStatus.ACCEPTED);
        secondApp.setParticipationConfirmed(false);
        secondApp.setPointsAwarded(0);
        secondApp = applicationRepository.save(secondApp);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(testPromoter.getId());
        request.setApplicationIds(Arrays.asList(testApplication.getId(), secondApp.getId()));

        mockMvc.perform(post("/api/opportunities/" + testOpportunity.getId() + "/conclude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalParticipantsConfirmed").value(2))
                .andExpect(jsonPath("$.totalPointsAwarded").value(300));

        Volunteer first = volunteerRepository.findById(testVolunteer.getId()).orElseThrow();
        assertEquals(150, first.getTotalPoints());

        Volunteer second = volunteerRepository.findById(secondVolunteer.getId()).orElseThrow();
        assertEquals(200, second.getTotalPoints());
    }

    @Test
    void whenCountConcludedByPromoter_thenReturnCorrectCount() throws Exception {
        testOpportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunityRepository.save(testOpportunity);

        Opportunity secondConcluded = new Opportunity();
        secondConcluded.setTitle("Second Concluded");
        secondConcluded.setDescription("Also concluded");
        secondConcluded.setSkills("Java");
        secondConcluded.setCategory("Tech");
        secondConcluded.setDuration(5);
        secondConcluded.setVacancies(2);
        secondConcluded.setPoints(100);
        secondConcluded.setStatus(OpportunityStatus.CONCLUDED);
        secondConcluded.setPromoter(testPromoter);
        opportunityRepository.save(secondConcluded);

        mockMvc.perform(get("/api/opportunities/promoter/" + testPromoter.getId() + "/concluded-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void whenConcludeOpportunityWrongPromoter_thenReturnConflict() throws Exception {
        Promoter otherPromoter = new Promoter();
        otherPromoter.setName("Other Promoter");
        otherPromoter.setEmail("other@test.com");
        otherPromoter.setOrganization("Other Org");
        otherPromoter = promoterRepository.save(otherPromoter);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(otherPromoter.getId());
        request.setApplicationIds(Arrays.asList(testApplication.getId()));

        mockMvc.perform(post("/api/opportunities/" + testOpportunity.getId() + "/conclude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenConcludeAlreadyConcluded_thenReturnConflict() throws Exception {
        testOpportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunityRepository.save(testOpportunity);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(testPromoter.getId());
        request.setApplicationIds(Arrays.asList(testApplication.getId()));

        mockMvc.perform(post("/api/opportunities/" + testOpportunity.getId() + "/conclude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
