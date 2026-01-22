package com.example.demo.integration;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class OpportunityIntegrationTest {

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

    private Promoter testPromoter;

    @BeforeEach
    void setUp() {
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        testPromoter = new Promoter();
        testPromoter.setName("Integration Test Promoter");
        testPromoter.setEmail("integration@test.com");
        testPromoter.setOrganization("Integration Test Org");
        testPromoter = promoterRepository.save(testPromoter);
    }

    @Test
    void whenCreateOpportunity_thenOpportunityShouldBePersistedInDatabase() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Integration Test Opportunity");
        request.setDescription("This is an integration test opportunity");
        request.setSkills("Java, Spring Boot, Testing");
        request.setDuration(15);
        request.setVacancies(10);
        request.setPoints(200);
        request.setPromoterId(testPromoter.getId());

        String responseBody = mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Opportunity"))
                .andExpect(jsonPath("$.description").value("This is an integration test opportunity"))
                .andExpect(jsonPath("$.skills").value("Java, Spring Boot, Testing"))
                .andExpect(jsonPath("$.duration").value(15))
                .andExpect(jsonPath("$.vacancies").value(10))
                .andExpect(jsonPath("$.points").value(200))
                .andExpect(jsonPath("$.promoterId").value(testPromoter.getId()))
                .andExpect(jsonPath("$.promoterName").value("Integration Test Promoter"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OpportunityResponse response = objectMapper.readValue(responseBody, OpportunityResponse.class);

        Opportunity savedOpportunity = opportunityRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedOpportunity);
        assertEquals("Integration Test Opportunity", savedOpportunity.getTitle());
        assertEquals(testPromoter.getId(), savedOpportunity.getPromoter().getId());
    }

    @Test
    void whenCreateOpportunityWithInvalidPromoterId_thenReturn404() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test");
        request.setDescription("Test description for opportunity");
        request.setSkills("Test skills");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(9999L);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promoter not found with id: 9999"));

        assertEquals(0, opportunityRepository.count());
    }

    @Test
    void whenCreateOpportunityWithInvalidData_thenReturn400AndNotPersist() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("");
        request.setDescription("short");
        request.setSkills("");
        request.setDuration(-5);
        request.setVacancies(0);
        request.setPoints(-100);
        request.setPromoterId(testPromoter.getId());

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        assertEquals(0, opportunityRepository.count());
    }

    @Test
    void whenGetOpportunityById_thenReturnCorrectOpportunity() throws Exception {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Existing Opportunity");
        opportunity.setDescription("This opportunity already exists");
        opportunity.setSkills("Existing Skills");
        opportunity.setDuration(20);
        opportunity.setVacancies(8);
        opportunity.setPoints(150);
        opportunity.setPromoter(testPromoter);
        opportunity = opportunityRepository.save(opportunity);

        mockMvc.perform(get("/api/opportunities/" + opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunity.getId()))
                .andExpect(jsonPath("$.title").value("Existing Opportunity"))
                .andExpect(jsonPath("$.description").value("This opportunity already exists"))
                .andExpect(jsonPath("$.promoterId").value(testPromoter.getId()));
    }

    @Test
    void whenGetAllOpportunities_thenReturnAllOpportunities() throws Exception {
        Opportunity opp1 = new Opportunity();
        opp1.setTitle("Opportunity 1");
        opp1.setDescription("Description 1 for testing");
        opp1.setSkills("Skills 1");
        opp1.setDuration(10);
        opp1.setVacancies(5);
        opp1.setPoints(100);
        opp1.setPromoter(testPromoter);
        opportunityRepository.save(opp1);

        Opportunity opp2 = new Opportunity();
        opp2.setTitle("Opportunity 2");
        opp2.setDescription("Description 2 for testing");
        opp2.setSkills("Skills 2");
        opp2.setDuration(20);
        opp2.setVacancies(10);
        opp2.setPoints(200);
        opp2.setPromoter(testPromoter);
        opportunityRepository.save(opp2);

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Opportunity 1"))
                .andExpect(jsonPath("$[1].title").value("Opportunity 2"));
    }

    @Test
    void whenGetOpportunitiesByPromoter_thenReturnOnlyPromoterOpportunities() throws Exception {
        Promoter anotherPromoter = new Promoter();
        anotherPromoter.setName("Another Promoter");
        anotherPromoter.setEmail("another@test.com");
        anotherPromoter.setOrganization("Another Org");
        anotherPromoter = promoterRepository.save(anotherPromoter);

        Opportunity opp1 = new Opportunity();
        opp1.setTitle("Promoter 1 Opportunity");
        opp1.setDescription("Description for promoter 1");
        opp1.setSkills("Skills 1");
        opp1.setDuration(10);
        opp1.setVacancies(5);
        opp1.setPoints(100);
        opp1.setPromoter(testPromoter);
        opportunityRepository.save(opp1);

        Opportunity opp2 = new Opportunity();
        opp2.setTitle("Promoter 2 Opportunity");
        opp2.setDescription("Description for promoter 2");
        opp2.setSkills("Skills 2");
        opp2.setDuration(20);
        opp2.setVacancies(10);
        opp2.setPoints(200);
        opp2.setPromoter(anotherPromoter);
        opportunityRepository.save(opp2);

        mockMvc.perform(get("/api/opportunities/promoter/" + testPromoter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Promoter 1 Opportunity"))
                .andExpect(jsonPath("$[0].promoterId").value(testPromoter.getId()));
    }

    @Test
    void whenGetOpportunitiesByInvalidPromoter_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/opportunities/promoter/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promoter not found with id: 9999"));
    }
}
