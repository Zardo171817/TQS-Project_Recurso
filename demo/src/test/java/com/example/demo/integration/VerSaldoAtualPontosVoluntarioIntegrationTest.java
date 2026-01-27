package com.example.demo.integration;

import com.example.demo.entity.Volunteer;
import com.example.demo.repository.VolunteerRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@DisplayName("Ver Saldo Atual Pontos Voluntario - Integration Tests")
class VerSaldoAtualPontosVoluntarioIntegrationTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private Volunteer volunteer1;
    private Volunteer volunteer2;
    private Volunteer volunteer3;

    @BeforeEach
    void setUp() {
        volunteerRepository.deleteAll();

        volunteer1 = new Volunteer();
        volunteer1.setName("Maria Silva");
        volunteer1.setEmail("maria@test.com");
        volunteer1.setPhone("912345678");
        volunteer1.setSkills("Organização");
        volunteer1.setTotalPoints(150);
        volunteer1 = volunteerRepository.save(volunteer1);

        volunteer2 = new Volunteer();
        volunteer2.setName("João Santos");
        volunteer2.setEmail("joao@test.com");
        volunteer2.setPhone("923456789");
        volunteer2.setSkills("Limpeza");
        volunteer2.setTotalPoints(300);
        volunteer2 = volunteerRepository.save(volunteer2);

        volunteer3 = new Volunteer();
        volunteer3.setName("Ana Costa");
        volunteer3.setEmail("ana@test.com");
        volunteer3.setPhone("934567890");
        volunteer3.setSkills("Ensino");
        volunteer3.setTotalPoints(75);
        volunteer3 = volunteerRepository.save(volunteer3);
    }

    @Nested
    @DisplayName("GET /api/volunteers/{id}/points Integration Tests")
    class GetVolunteerPointsIntegrationTests {

        @Test
        @DisplayName("Deve retornar pontos do voluntário existente")
        void shouldReturnPointsForExistingVolunteer() throws Exception {
            mockMvc.perform(get("/api/volunteers/" + volunteer1.getId() + "/points")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(volunteer1.getId()))
                    .andExpect(jsonPath("$.name").value("Maria Silva"))
                    .andExpect(jsonPath("$.email").value("maria@test.com"))
                    .andExpect(jsonPath("$.totalPoints").value(150));
        }

        @Test
        @DisplayName("Deve retornar 404 para voluntário inexistente")
        void shouldReturn404ForNonExistentVolunteer() throws Exception {
            mockMvc.perform(get("/api/volunteers/99999/points"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Volunteer not found")));
        }

        @Test
        @DisplayName("Deve retornar pontos diferentes para voluntários diferentes")
        void shouldReturnDifferentPointsForDifferentVolunteers() throws Exception {
            mockMvc.perform(get("/api/volunteers/" + volunteer1.getId() + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(150));

            mockMvc.perform(get("/api/volunteers/" + volunteer2.getId() + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(300));
        }

        @Test
        @DisplayName("Deve retornar zero pontos para voluntário sem pontos")
        void shouldReturnZeroPointsForVolunteerWithoutPoints() throws Exception {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setName("Novo Voluntário");
            newVolunteer.setEmail("novo@test.com");
            newVolunteer.setTotalPoints(0);
            newVolunteer = volunteerRepository.save(newVolunteer);

            mockMvc.perform(get("/api/volunteers/" + newVolunteer.getId() + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/ranking Integration Tests")
    class GetVolunteersRankingIntegrationTests {

        @Test
        @DisplayName("Deve retornar ranking ordenado por pontos")
        void shouldReturnRankingSortedByPoints() throws Exception {
            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].totalPoints").value(300))
                    .andExpect(jsonPath("$[0].name").value("João Santos"))
                    .andExpect(jsonPath("$[1].totalPoints").value(150))
                    .andExpect(jsonPath("$[1].name").value("Maria Silva"))
                    .andExpect(jsonPath("$[2].totalPoints").value(75))
                    .andExpect(jsonPath("$[2].name").value("Ana Costa"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void shouldReturnEmptyListWhenNoVolunteers() throws Exception {
            volunteerRepository.deleteAll();

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Deve retornar ranking com voluntário único")
        void shouldReturnRankingWithSingleVolunteer() throws Exception {
            volunteerRepository.deleteAll();
            Volunteer singleVolunteer = new Volunteer();
            singleVolunteer.setName("Único");
            singleVolunteer.setEmail("unico@test.com");
            singleVolunteer.setTotalPoints(100);
            volunteerRepository.save(singleVolunteer);

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Único"));
        }

        @Test
        @DisplayName("Deve manter ordem correta após adicionar novo voluntário")
        void shouldMaintainCorrectOrderAfterAddingNewVolunteer() throws Exception {
            Volunteer topVolunteer = new Volunteer();
            topVolunteer.setName("Top Volunteer");
            topVolunteer.setEmail("top@test.com");
            topVolunteer.setTotalPoints(500);
            volunteerRepository.save(topVolunteer);

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].totalPoints").value(500))
                    .andExpect(jsonPath("$[0].name").value("Top Volunteer"));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/top/{limit} Integration Tests")
    class GetTopVolunteersIntegrationTests {

        @Test
        @DisplayName("Deve retornar top 2 voluntários")
        void shouldReturnTopTwoVolunteers() throws Exception {
            mockMvc.perform(get("/api/volunteers/top/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].totalPoints").value(300))
                    .andExpect(jsonPath("$[1].totalPoints").value(150));
        }

        @Test
        @DisplayName("Deve retornar top 1 voluntário")
        void shouldReturnTopOneVolunteer() throws Exception {
            mockMvc.perform(get("/api/volunteers/top/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("João Santos"));
        }

        @Test
        @DisplayName("Deve retornar todos quando limite maior que total")
        void shouldReturnAllWhenLimitExceedsTotal() throws Exception {
            mockMvc.perform(get("/api/volunteers/top/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        @DisplayName("Deve retornar lista vazia para limite zero")
        void shouldReturnEmptyListForZeroLimit() throws Exception {
            mockMvc.perform(get("/api/volunteers/top/0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Database State Verification Tests")
    class DatabaseStateVerificationTests {

        @Test
        @DisplayName("Deve persistir pontos corretamente no banco")
        void shouldPersistPointsCorrectlyInDatabase() throws Exception {
            Volunteer savedVolunteer = volunteerRepository.findById(volunteer1.getId()).orElseThrow();

            assertThat(savedVolunteer.getTotalPoints()).isEqualTo(150);
        }

        @Test
        @DisplayName("Deve atualizar pontos no banco e refletir na API")
        void shouldUpdatePointsInDatabaseAndReflectInApi() throws Exception {
            volunteer1.setTotalPoints(500);
            volunteerRepository.save(volunteer1);

            mockMvc.perform(get("/api/volunteers/" + volunteer1.getId() + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(500));
        }

        @Test
        @DisplayName("Deve manter consistência entre ranking e pontos individuais")
        void shouldMaintainConsistencyBetweenRankingAndIndividualPoints() throws Exception {
            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(volunteer2.getId()));

            mockMvc.perform(get("/api/volunteers/" + volunteer2.getId() + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(300));
        }
    }

    @Nested
    @DisplayName("Error Handling Integration Tests")
    class ErrorHandlingIntegrationTests {

        @Test
        @DisplayName("Deve retornar mensagem de erro apropriada para ID inválido")
        void shouldReturnAppropriateErrorMessageForInvalidId() throws Exception {
            mockMvc.perform(get("/api/volunteers/999999/points"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
