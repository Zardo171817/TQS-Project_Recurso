package com.example.demo.integration;

import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class VolunteerIntegrationTest {

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
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private Volunteer testVolunteer;
    private Opportunity testOpportunity;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        volunteerRepository.deleteAll();
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        // Create promoter
        Promoter promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        // Create opportunity
        testOpportunity = new Opportunity();
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("Test Description");
        testOpportunity.setSkills("Java, Spring");
        testOpportunity.setCategory("Tecnologia");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setPromoter(promoter);
        testOpportunity = opportunityRepository.save(testOpportunity);

        // Create volunteer
        testVolunteer = new Volunteer();
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");
        testVolunteer.setPhone("123456789");
        testVolunteer.setSkills("Java, Python");
        testVolunteer = volunteerRepository.save(testVolunteer);
    }

    // Feature: Ver Candidaturas Voluntario - Get volunteer by ID
    @Test
    void whenGetVolunteerById_thenReturnVolunteer() throws Exception {
        mockMvc.perform(get("/api/volunteers/" + testVolunteer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testVolunteer.getId()))
                .andExpect(jsonPath("$.name").value("Test Volunteer"))
                .andExpect(jsonPath("$.email").value("volunteer@test.com"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.skills").value("Java, Python"));
    }

    @Test
    void whenGetVolunteerByIdNotFound_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/volunteers/999"))
                .andExpect(status().isNotFound());
    }

    // Feature: Ver Candidaturas Voluntario - Get volunteer by email
    @Test
    void whenGetVolunteerByEmail_thenReturnVolunteer() throws Exception {
        mockMvc.perform(get("/api/volunteers/email/volunteer@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("volunteer@test.com"))
                .andExpect(jsonPath("$.name").value("Test Volunteer"));
    }

    @Test
    void whenGetVolunteerByEmailNotFound_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/volunteers/email/notfound@test.com"))
                .andExpect(status().isNotFound());
    }

    // Feature: Ver Candidaturas Voluntario - Get all volunteers
    @Test
    void whenGetAllVolunteers_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].email").value("volunteer@test.com"));
    }

    // Feature: Ver Candidaturas Voluntario - Check if volunteer exists by email
    @Test
    void whenExistsByEmail_thenReturnTrue() throws Exception {
        mockMvc.perform(get("/api/volunteers/exists/volunteer@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void whenExistsByEmailNotFound_thenReturnFalse() throws Exception {
        mockMvc.perform(get("/api/volunteers/exists/notfound@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // Feature: Ver Candidaturas Voluntario - Integration with Applications
    @Test
    void whenVolunteerViewsApplications_thenReturnApplicationsList() throws Exception {
        // Create application
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Test Volunteer");
        request.setVolunteerEmail("volunteer@test.com");
        request.setMotivation("I want to help");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Get volunteer by email
        String volunteerJson = mockMvc.perform(get("/api/volunteers/email/volunteer@test.com"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long volunteerId = objectMapper.readTree(volunteerJson).get("id").asLong();

        // Get applications by volunteer
        mockMvc.perform(get("/api/applications/volunteer/" + volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].volunteerEmail").value("volunteer@test.com"))
                .andExpect(jsonPath("$[0].opportunityTitle").value("Test Opportunity"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void whenVolunteerViewsMultipleApplications_thenReturnAllApplications() throws Exception {
        // Create second opportunity
        Opportunity secondOpportunity = new Opportunity();
        secondOpportunity.setTitle("Second Opportunity");
        secondOpportunity.setDescription("Second Description");
        secondOpportunity.setSkills("Python, Django");
        secondOpportunity.setCategory("Educacao");
        secondOpportunity.setDuration(15);
        secondOpportunity.setVacancies(3);
        secondOpportunity.setPoints(150);
        secondOpportunity.setPromoter(testOpportunity.getPromoter());
        secondOpportunity = opportunityRepository.save(secondOpportunity);

        // Create first application
        CreateApplicationRequest request1 = new CreateApplicationRequest();
        request1.setOpportunityId(testOpportunity.getId());
        request1.setVolunteerName("Multi App Volunteer");
        request1.setVolunteerEmail("multiapp@test.com");
        request1.setMotivation("First application");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second application (same volunteer, different opportunity)
        CreateApplicationRequest request2 = new CreateApplicationRequest();
        request2.setOpportunityId(secondOpportunity.getId());
        request2.setVolunteerName("Multi App Volunteer");
        request2.setVolunteerEmail("multiapp@test.com");
        request2.setMotivation("Second application");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Get volunteer and verify applications
        String volunteerJson = mockMvc.perform(get("/api/volunteers/email/multiapp@test.com"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long volunteerId = objectMapper.readTree(volunteerJson).get("id").asLong();

        mockMvc.perform(get("/api/applications/volunteer/" + volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenVolunteerViewsApplicationsWithDifferentStatuses_thenShowAllStatuses() throws Exception {
        // Create application
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Status Test Volunteer");
        request.setVolunteerEmail("statustest@test.com");
        request.setMotivation("Testing status");

        String responseJson = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(responseJson).get("id").asLong();
        Long volunteerId = objectMapper.readTree(responseJson).get("volunteerId").asLong();

        // Verify initial PENDING status
        mockMvc.perform(get("/api/applications/volunteer/" + volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        // Update to ACCEPTED
        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .param("status", "ACCEPTED"))
                .andExpect(status().isOk());

        // Verify ACCEPTED status
        mockMvc.perform(get("/api/applications/volunteer/" + volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    @Test
    void whenNewVolunteerApplies_thenVolunteerIsCreatedAndCanViewApplications() throws Exception {
        // Create application with new volunteer
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("New Volunteer");
        request.setVolunteerEmail("newvolunteer@test.com");
        request.setVolunteerPhone("999888777");
        request.setVolunteerSkills("C++, Rust");
        request.setMotivation("I am new and want to help");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify volunteer was created
        mockMvc.perform(get("/api/volunteers/email/newvolunteer@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Volunteer"))
                .andExpect(jsonPath("$.phone").value("999888777"))
                .andExpect(jsonPath("$.skills").value("C++, Rust"));
    }
}
