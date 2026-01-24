package com.example.demo.integration;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.UpdateOpportunityRequest;
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

import static org.hamcrest.Matchers.*;
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

    // Feature 1: Criar Oportunidade
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
                .andExpect(jsonPath("$.title").value("Integration Test Opportunity"));
    }

    // Feature 2: Ver/Filtrar Oportunidades
    @Test
    void whenFilterOpportunitiesByCategory_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Tech Opportunity", "Java", "Tecnologia", 10);
        createOpportunity("Health Opportunity", "First Aid", "Saude", 15);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "Tecnologia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Tech Opportunity"));
    }

    // Feature 3: Ver Detalhes
    @Test
    void whenGetOpportunityById_thenReturnOpportunity() throws Exception {
        Opportunity opp = createOpportunity("Get By ID Test", "Java", "Tecnologia", 10);

        mockMvc.perform(get("/api/opportunities/" + opp.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Get By ID Test"));
    }

    // Feature 4: Editar/Cancelar Oportunidades
    @Test
    void whenUpdateOpportunity_thenOpportunityShouldBeUpdated() throws Exception {
        Opportunity opp = createOpportunity("Original Title", "Java", "Tecnologia", 10);

        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description here for testing");
        updateRequest.setSkills("Python, Django");
        updateRequest.setCategory("Educacao");
        updateRequest.setDuration(20);
        updateRequest.setVacancies(15);
        updateRequest.setPoints(250);

        mockMvc.perform(put("/api/opportunities/" + opp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void whenDeleteOpportunity_thenOpportunityShouldBeRemoved() throws Exception {
        Opportunity opp = createOpportunity("To Delete", "Java", "Tecnologia", 10);

        mockMvc.perform(delete("/api/opportunities/" + opp.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/opportunities/" + opp.getId()))
                .andExpect(status().isNotFound());
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
