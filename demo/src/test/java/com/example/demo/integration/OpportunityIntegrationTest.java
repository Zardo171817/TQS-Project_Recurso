package com.example.demo.integration;

import com.example.demo.dto.CreateOpportunityRequest;
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

    // Feature 1: Criar Oportunidade - Integration Test
    @Test
    void whenCreateOpportunity_thenOpportunityShouldBePersistedInDatabase() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Integration Test Opportunity");
        request.setDescription("This is an integration test opportunity");
        request.setSkills("Java, Spring Boot, Testing");
        request.setCategory("Tecnologia");
        request.setDuration(15);
        request.setVacancies(10);
        request.setPoints(200);
        request.setPromoterId(testPromoter.getId());

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Opportunity"))
                .andExpect(jsonPath("$.category").value("Tecnologia"));
    }

    // Feature 2: Ver/Filtrar Oportunidades - Integration Test
    @Test
    void whenFilterOpportunitiesByCategory_thenReturnMatchingOpportunities() throws Exception {
        Opportunity opp1 = createOpportunity("Tech Opportunity", "Java", "Tecnologia", 10);
        createOpportunity("Health Opportunity", "First Aid", "Saude", 15);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "Tecnologia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Tech Opportunity"));
    }

    private Opportunity createOpportunity(String title, String skills, String category, int duration) {
        Opportunity opp = new Opportunity();
        opp.setTitle(title);
        opp.setDescription("Description for " + title);
        opp.setSkills(skills);
        opp.setCategory(category);
        opp.setDuration(duration);
        opp.setVacancies(5);
        opp.setPoints(100);
        opp.setPromoter(testPromoter);
        return opportunityRepository.save(opp);
    }
}
