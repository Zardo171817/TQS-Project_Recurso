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

    // Feature 1: Criar Oportunidade - Integration Tests
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
                .andExpect(jsonPath("$.category").value("Tecnologia"))
                .andExpect(jsonPath("$.duration").value(15))
                .andExpect(jsonPath("$.vacancies").value(10))
                .andExpect(jsonPath("$.points").value(200));
    }

    @Test
    void whenCreateOpportunityWithInvalidPromoter_thenReturnNotFound() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test Opportunity");
        request.setDescription("Test Description Here");
        request.setSkills("Java");
        request.setCategory("Tecnologia");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(99999L);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateOpportunityWithInvalidData_thenReturnBadRequest() throws Exception {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle(""); // Invalid - blank
        request.setDescription("Short"); // Invalid - too short
        request.setSkills("");
        request.setCategory("");
        request.setDuration(-1); // Invalid - negative
        request.setVacancies(-1); // Invalid - negative
        request.setPoints(-1); // Invalid - negative
        request.setPromoterId(null);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // Get Opportunity by ID - Integration Tests
    @Test
    void whenGetOpportunityById_thenReturnOpportunity() throws Exception {
        Opportunity opp = createOpportunity("Get By ID Test", "Java", "Tecnologia", 10);

        mockMvc.perform(get("/api/opportunities/" + opp.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opp.getId()))
                .andExpect(jsonPath("$.title").value("Get By ID Test"))
                .andExpect(jsonPath("$.skills").value("Java"))
                .andExpect(jsonPath("$.category").value("Tecnologia"));
    }

    @Test
    void whenGetOpportunityByIdNotFound_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/opportunities/99999"))
                .andExpect(status().isNotFound());
    }

    // Get All Opportunities - Integration Tests
    @Test
    void whenGetAllOpportunities_thenReturnList() throws Exception {
        createOpportunity("Opportunity 1", "Java", "Tecnologia", 10);
        createOpportunity("Opportunity 2", "Python", "Educacao", 15);
        createOpportunity("Opportunity 3", "First Aid", "Saude", 5);

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void whenGetAllOpportunitiesEmpty_thenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Get Opportunities by Promoter - Integration Tests
    @Test
    void whenGetOpportunitiesByPromoter_thenReturnList() throws Exception {
        createOpportunity("Promoter Opp 1", "Java", "Tecnologia", 10);
        createOpportunity("Promoter Opp 2", "Python", "Tecnologia", 15);

        mockMvc.perform(get("/api/opportunities/promoter/" + testPromoter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenGetOpportunitiesByPromoterNotFound_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/opportunities/promoter/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOpportunitiesByPromoterEmpty_thenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/opportunities/promoter/" + testPromoter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Feature 2: Ver/Filtrar Oportunidades - Integration Tests
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

    @Test
    void whenFilterOpportunitiesBySkills_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Java Developer", "Java, Spring Boot", "Tecnologia", 10);
        createOpportunity("Python Developer", "Python, Django", "Tecnologia", 15);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("skills", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java Developer"));
    }

    @Test
    void whenFilterOpportunitiesByDurationRange_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Short Opportunity", "Skill", "Tecnologia", 5);
        createOpportunity("Medium Opportunity", "Skill", "Tecnologia", 15);
        createOpportunity("Long Opportunity", "Skill", "Tecnologia", 30);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("minDuration", "10")
                        .param("maxDuration", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Medium Opportunity"));
    }

    @Test
    void whenFilterOpportunitiesByMinDurationOnly_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Short Opportunity", "Skill", "Tecnologia", 5);
        createOpportunity("Long Opportunity", "Skill", "Tecnologia", 30);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("minDuration", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Long Opportunity"));
    }

    @Test
    void whenFilterOpportunitiesByMaxDurationOnly_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Short Opportunity", "Skill", "Tecnologia", 5);
        createOpportunity("Long Opportunity", "Skill", "Tecnologia", 30);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("maxDuration", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Short Opportunity"));
    }

    @Test
    void whenFilterOpportunitiesWithAllParams_thenReturnMatchingOpportunities() throws Exception {
        createOpportunity("Java Dev Short", "Java, Spring", "Tecnologia", 10);
        createOpportunity("Java Dev Long", "Java, Spring", "Tecnologia", 30);
        createOpportunity("Python Dev", "Python, Django", "Tecnologia", 15);
        createOpportunity("Health Helper", "First Aid", "Saude", 10);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "Tecnologia")
                        .param("skills", "Java")
                        .param("minDuration", "5")
                        .param("maxDuration", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java Dev Short"));
    }

    @Test
    void whenFilterOpportunitiesNoParams_thenReturnAllOpportunities() throws Exception {
        createOpportunity("Opportunity 1", "Java", "Tecnologia", 10);
        createOpportunity("Opportunity 2", "Python", "Saude", 15);

        mockMvc.perform(get("/api/opportunities/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenFilterOpportunitiesNoMatch_thenReturnEmptyList() throws Exception {
        createOpportunity("Tech Opportunity", "Java", "Tecnologia", 10);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Get All Categories - Integration Tests
    @Test
    void whenGetAllCategories_thenReturnDistinctCategories() throws Exception {
        createOpportunity("Tech 1", "Java", "Tecnologia", 10);
        createOpportunity("Tech 2", "Python", "Tecnologia", 15);
        createOpportunity("Health 1", "First Aid", "Saude", 5);
        createOpportunity("Education 1", "Teaching", "Educacao", 20);

        mockMvc.perform(get("/api/opportunities/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", containsInAnyOrder("Educacao", "Saude", "Tecnologia")));
    }

    @Test
    void whenGetAllCategoriesEmpty_thenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/opportunities/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Promoter Endpoint Integration Tests
    @Test
    void whenGetAllPromoters_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/promoters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Integration Test Promoter"))
                .andExpect(jsonPath("$[0].email").value("integration@test.com"))
                .andExpect(jsonPath("$[0].organization").value("Integration Test Org"));
    }

    // Feature: Editar/Cancelar Oportunidades - Integration Tests
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
                .andExpect(jsonPath("$.id").value(opp.getId()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description here for testing"))
                .andExpect(jsonPath("$.skills").value("Python, Django"))
                .andExpect(jsonPath("$.category").value("Educacao"))
                .andExpect(jsonPath("$.duration").value(20))
                .andExpect(jsonPath("$.vacancies").value(15))
                .andExpect(jsonPath("$.points").value(250));
    }

    @Test
    void whenUpdateOpportunityNotFound_thenReturnNotFound() throws Exception {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description here for testing");
        updateRequest.setSkills("Python, Django");
        updateRequest.setCategory("Educacao");
        updateRequest.setDuration(20);
        updateRequest.setVacancies(15);
        updateRequest.setPoints(250);

        mockMvc.perform(put("/api/opportunities/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateOpportunityWithInvalidData_thenReturnBadRequest() throws Exception {
        Opportunity opp = createOpportunity("Original Title", "Java", "Tecnologia", 10);

        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle(""); // Invalid - blank
        updateRequest.setDescription("Short"); // Invalid - too short
        updateRequest.setSkills("");
        updateRequest.setCategory("");
        updateRequest.setDuration(-1); // Invalid - negative
        updateRequest.setVacancies(-1); // Invalid - negative
        updateRequest.setPoints(-1); // Invalid - negative

        mockMvc.perform(put("/api/opportunities/" + opp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteOpportunity_thenOpportunityShouldBeRemoved() throws Exception {
        Opportunity opp = createOpportunity("To Delete", "Java", "Tecnologia", 10);
        Long oppId = opp.getId();

        mockMvc.perform(delete("/api/opportunities/" + oppId))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/api/opportunities/" + oppId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteOpportunityNotFound_thenReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/opportunities/99999"))
                .andExpect(status().isNotFound());
    }

    // Helper method
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
