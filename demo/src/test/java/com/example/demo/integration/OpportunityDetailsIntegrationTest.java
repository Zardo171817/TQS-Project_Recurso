package com.example.demo.integration;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

/**
 * Integration tests for Feature: Ver Detalhes de Oportunidades (Voluntario)
 * Tests the complete flow from HTTP request to database retrieval
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@DisplayName("Feature: Ver Detalhes de Oportunidades - Integration Tests")
class OpportunityDetailsIntegrationTest {

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
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private Promoter testPromoter;

    @BeforeEach
    void setUp() {
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        testPromoter = new Promoter();
        testPromoter.setName("Caritas Portuguesa");
        testPromoter.setEmail("voluntariado@caritas.pt");
        testPromoter.setOrganization("Caritas Diocesana");
        testPromoter = promoterRepository.save(testPromoter);
    }

    @Test
    @DisplayName("Voluntario deve conseguir ver todos os detalhes de uma oportunidade existente")
    void whenVolunteerRequestsOpportunityDetails_thenReturnAllDetails() throws Exception {
        Opportunity opportunity = createOpportunity(
                "Distribuicao de Alimentos",
                "Ajude-nos a distribuir alimentos para familias carenciadas. O trabalho inclui organizacao dos pacotes, atendimento ao publico e entrega em domicilio quando necessario.",
                "Organizacao, Trabalho em Equipa, Carta de Conducao",
                "Assistencia Social",
                7
        );

        mockMvc.perform(get("/api/opportunities/" + opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunity.getId()))
                .andExpect(jsonPath("$.title").value("Distribuicao de Alimentos"))
                .andExpect(jsonPath("$.description").value("Ajude-nos a distribuir alimentos para familias carenciadas. O trabalho inclui organizacao dos pacotes, atendimento ao publico e entrega em domicilio quando necessario."))
                .andExpect(jsonPath("$.skills").value("Organizacao, Trabalho em Equipa, Carta de Conducao"))
                .andExpect(jsonPath("$.category").value("Assistencia Social"))
                .andExpect(jsonPath("$.duration").value(7))
                .andExpect(jsonPath("$.vacancies").value(10))
                .andExpect(jsonPath("$.points").value(150))
                .andExpect(jsonPath("$.promoterId").value(testPromoter.getId()))
                .andExpect(jsonPath("$.promoterName").value("Caritas Portuguesa"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("Voluntario deve receber erro 404 quando oportunidade nao existir")
    void whenVolunteerRequestsNonExistentOpportunity_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/opportunities/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("99999")));
    }

    @Test
    @DisplayName("Detalhes devem incluir informacoes completas do promotor")
    void whenVolunteerRequestsDetails_thenPromoterInfoIsIncluded() throws Exception {
        Opportunity opportunity = createOpportunity(
                "Aulas de Informatica para Idosos",
                "Ensine idosos a usar computadores e smartphones",
                "Informatica, Paciencia, Pedagogia",
                "Educacao",
                15
        );

        mockMvc.perform(get("/api/opportunities/" + opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoterId").value(testPromoter.getId()))
                .andExpect(jsonPath("$.promoterName").value("Caritas Portuguesa"));
    }

    @Test
    @DisplayName("Detalhes devem incluir todas as competencias necessarias")
    void whenVolunteerRequestsDetails_thenSkillsAreComplete() throws Exception {
        String detailedSkills = "Java, Spring Boot, PostgreSQL, Docker, Kubernetes, AWS";
        Opportunity opportunity = createOpportunity(
                "Desenvolvimento de Sistema para ONG",
                "Desenvolva um sistema de gestao para uma ONG",
                detailedSkills,
                "Tecnologia",
                60
        );

        mockMvc.perform(get("/api/opportunities/" + opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").value(detailedSkills));
    }

    @Test
    @DisplayName("Detalhes devem incluir informacoes de duracao, vagas e pontos")
    void whenVolunteerRequestsDetails_thenMetadataIsComplete() throws Exception {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Workshop de Primeiros Socorros");
        opportunity.setDescription("Participe como instrutor em workshops de primeiros socorros");
        opportunity.setSkills("Certificacao em Primeiros Socorros, Comunicacao");
        opportunity.setCategory("Saude");
        opportunity.setDuration(3);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setPromoter(testPromoter);
        opportunity = opportunityRepository.save(opportunity);

        mockMvc.perform(get("/api/opportunities/" + opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value(3))
                .andExpect(jsonPath("$.vacancies").value(5))
                .andExpect(jsonPath("$.points").value(100));
    }

    private Opportunity createOpportunity(String title, String description, String skills, String category, int duration) {
        Opportunity opp = new Opportunity();
        opp.setTitle(title);
        opp.setDescription(description);
        opp.setSkills(skills);
        opp.setCategory(category);
        opp.setDuration(duration);
        opp.setVacancies(10);
        opp.setPoints(150);
        opp.setPromoter(testPromoter);
        return opportunityRepository.save(opp);
    }
}
