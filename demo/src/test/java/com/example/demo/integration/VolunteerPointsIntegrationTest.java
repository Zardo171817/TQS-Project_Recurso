package com.example.demo.integration;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class VolunteerPointsIntegrationTest {

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
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Volunteer topVolunteer;
    private Volunteer midVolunteer;
    private Volunteer lowVolunteer;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();

        topVolunteer = new Volunteer();
        topVolunteer.setName("Top Volunteer");
        topVolunteer.setEmail("top@test.com");
        topVolunteer.setTotalPoints(500);
        topVolunteer = volunteerRepository.save(topVolunteer);

        midVolunteer = new Volunteer();
        midVolunteer.setName("Mid Volunteer");
        midVolunteer.setEmail("mid@test.com");
        midVolunteer.setTotalPoints(250);
        midVolunteer = volunteerRepository.save(midVolunteer);

        lowVolunteer = new Volunteer();
        lowVolunteer.setName("Low Volunteer");
        lowVolunteer.setEmail("low@test.com");
        lowVolunteer.setTotalPoints(100);
        lowVolunteer = volunteerRepository.save(lowVolunteer);
    }

    @Test
    void whenGetVolunteerPoints_thenReturnCorrectPoints() throws Exception {
        mockMvc.perform(get("/api/volunteers/" + topVolunteer.getId() + "/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topVolunteer.getId()))
                .andExpect(jsonPath("$.name").value("Top Volunteer"))
                .andExpect(jsonPath("$.totalPoints").value(500));
    }

    @Test
    void whenGetVolunteerPointsNotFound_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/volunteers/999/points"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRanking_thenReturnSortedByPoints() throws Exception {
        mockMvc.perform(get("/api/volunteers/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].totalPoints").value(500))
                .andExpect(jsonPath("$[1].totalPoints").value(250))
                .andExpect(jsonPath("$[2].totalPoints").value(100));
    }

    @Test
    void whenGetTop2_thenReturnTop2Only() throws Exception {
        mockMvc.perform(get("/api/volunteers/top/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Top Volunteer"))
                .andExpect(jsonPath("$[1].name").value("Mid Volunteer"));
    }

    @Test
    void whenGetTop10_thenReturnAllAvailable() throws Exception {
        mockMvc.perform(get("/api/volunteers/top/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void whenGetConfirmedParticipations_thenReturnList() throws Exception {
        Promoter promoter = new Promoter();
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");
        promoter = promoterRepository.save(promoter);

        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Test Opp");
        opportunity.setDescription("Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setPromoter(promoter);
        opportunity = opportunityRepository.save(opportunity);

        Application app = new Application();
        app.setVolunteer(topVolunteer);
        app.setOpportunity(opportunity);
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setParticipationConfirmed(true);
        app.setPointsAwarded(100);
        applicationRepository.save(app);

        mockMvc.perform(get("/api/volunteers/" + topVolunteer.getId() + "/confirmed-participations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].participationConfirmed").value(true))
                .andExpect(jsonPath("$[0].pointsAwarded").value(100));
    }

    @Test
    void whenGetConfirmedParticipationsEmpty_thenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/volunteers/" + lowVolunteer.getId() + "/confirmed-participations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetRankingWithSamePoints_thenReturnAll() throws Exception {
        topVolunteer.setTotalPoints(100);
        midVolunteer.setTotalPoints(100);
        lowVolunteer.setTotalPoints(100);
        volunteerRepository.save(topVolunteer);
        volunteerRepository.save(midVolunteer);
        volunteerRepository.save(lowVolunteer);

        mockMvc.perform(get("/api/volunteers/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].totalPoints").value(100))
                .andExpect(jsonPath("$[1].totalPoints").value(100))
                .andExpect(jsonPath("$[2].totalPoints").value(100));
    }

    @Test
    void whenGetVolunteerById_thenIncludesTotalPoints() throws Exception {
        mockMvc.perform(get("/api/volunteers/" + topVolunteer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(500));
    }

    @Test
    void whenGetAllVolunteers_thenIncludesTotalPoints() throws Exception {
        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].totalPoints").exists());
    }
}
