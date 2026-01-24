package com.example.demo.integration;

import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
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
class ApplicationIntegrationTest {

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
    private ApplicationRepository applicationRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private Opportunity testOpportunity;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        volunteerRepository.deleteAll();
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        Promoter promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        testOpportunity = new Opportunity();
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("Test Description for integration");
        testOpportunity.setSkills("Java, Spring");
        testOpportunity.setCategory("Tecnologia");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setPromoter(promoter);
        testOpportunity = opportunityRepository.save(testOpportunity);
    }

    @Test
    void whenCreateApplication_thenApplicationShouldBePersisted() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Test Volunteer");
        request.setVolunteerEmail("volunteer@test.com");
        request.setVolunteerPhone("123456789");
        request.setMotivation("I want to help the community");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.volunteerName").value("Test Volunteer"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void whenCreateDuplicateApplication_thenReturnConflict() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Test Volunteer");
        request.setVolunteerEmail("duplicate@test.com");

        // First application
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate application
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenGetApplicationsByOpportunity_thenReturnList() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Volunteer");
        request.setVolunteerEmail("vol@test.com");

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/applications/opportunity/" + testOpportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenGetApplicationById_thenReturnApplication() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Test Vol");
        request.setVolunteerEmail("testbyid@test.com");

        String responseJson = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(responseJson).get("id").asLong();

        mockMvc.perform(get("/api/applications/" + applicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.volunteerEmail").value("testbyid@test.com"));
    }

    @Test
    void whenGetApplicationsByVolunteer_thenReturnList() throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(testOpportunity.getId());
        request.setVolunteerName("Volunteer By Id");
        request.setVolunteerEmail("volbyid@test.com");

        String responseJson = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long volunteerId = objectMapper.readTree(responseJson).get("volunteerId").asLong();

        mockMvc.perform(get("/api/applications/volunteer/" + volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].volunteerName").value("Volunteer By Id"));
    }
}
