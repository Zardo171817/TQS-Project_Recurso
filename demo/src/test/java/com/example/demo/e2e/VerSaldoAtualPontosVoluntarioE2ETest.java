package com.example.demo.e2e;

import com.example.demo.entity.Volunteer;
import com.example.demo.repository.VolunteerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Ver Saldo Atual Pontos Voluntario - E2E Tests")
class VerSaldoAtualPontosVoluntarioE2ETest {

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

    @LocalServerPort
    private int port;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private Volunteer volunteer1;
    private Volunteer volunteer2;
    private Volunteer volunteer3;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/volunteers";

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
    @DisplayName("GET /{id}/points E2E Tests")
    class GetVolunteerPointsE2ETests {

        @Test
        @DisplayName("Deve retornar pontos do voluntário com sucesso")
        void shouldReturnVolunteerPointsSuccessfully() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}/points", volunteer1.getId())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(volunteer1.getId().intValue()))
                .body("name", equalTo("Maria Silva"))
                .body("email", equalTo("maria@test.com"))
                .body("totalPoints", equalTo(150));
        }

        @Test
        @DisplayName("Deve retornar 404 para voluntário inexistente")
        void shouldReturn404ForNonExistentVolunteer() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}/points", 99999)
            .then()
                .statusCode(404)
                .body("message", containsString("Volunteer not found"));
        }

        @Test
        @DisplayName("Deve retornar zero pontos para novo voluntário")
        void shouldReturnZeroPointsForNewVolunteer() {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setName("Novo");
            newVolunteer.setEmail("novo@test.com");
            newVolunteer.setTotalPoints(0);
            newVolunteer = volunteerRepository.save(newVolunteer);

            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}/points", newVolunteer.getId())
            .then()
                .statusCode(200)
                .body("totalPoints", equalTo(0));
        }

        @Test
        @DisplayName("Deve retornar pontos altos corretamente")
        void shouldReturnHighPointsCorrectly() {
            volunteer1.setTotalPoints(10000);
            volunteerRepository.save(volunteer1);

            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}/points", volunteer1.getId())
            .then()
                .statusCode(200)
                .body("totalPoints", equalTo(10000));
        }
    }

    @Nested
    @DisplayName("GET /ranking E2E Tests")
    class GetVolunteersRankingE2ETests {

        @Test
        @DisplayName("Deve retornar ranking ordenado por pontos decrescente")
        void shouldReturnRankingSortedByPointsDescending() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("[0].totalPoints", equalTo(300))
                .body("[0].name", equalTo("João Santos"))
                .body("[1].totalPoints", equalTo(150))
                .body("[2].totalPoints", equalTo(75));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void shouldReturnEmptyListWhenNoVolunteers() {
            volunteerRepository.deleteAll();

            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
        }

        @Test
        @DisplayName("Deve incluir todos os campos no ranking")
        void shouldIncludeAllFieldsInRanking() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].email", notNullValue())
                .body("[0].totalPoints", notNullValue());
        }
    }

    @Nested
    @DisplayName("GET /top/{limit} E2E Tests")
    class GetTopVolunteersE2ETests {

        @Test
        @DisplayName("Deve retornar top 2 voluntários")
        void shouldReturnTopTwoVolunteers() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/top/{limit}", 2)
            .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].totalPoints", equalTo(300))
                .body("[1].totalPoints", equalTo(150));
        }

        @Test
        @DisplayName("Deve retornar top 1 voluntário")
        void shouldReturnTopOneVolunteer() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/top/{limit}", 1)
            .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("João Santos"));
        }

        @Test
        @DisplayName("Deve retornar todos quando limite maior que total")
        void shouldReturnAllWhenLimitExceedsTotal() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/top/{limit}", 100)
            .then()
                .statusCode(200)
                .body("$", hasSize(3));
        }

        @Test
        @DisplayName("Deve retornar lista vazia para limite zero")
        void shouldReturnEmptyListForZeroLimit() {
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/top/{limit}", 0)
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
        }
    }

    @Nested
    @DisplayName("Full Flow E2E Tests")
    class FullFlowE2ETests {

        @Test
        @DisplayName("Deve manter consistência entre ranking e pontos individuais")
        void shouldMaintainConsistencyBetweenRankingAndIndividualPoints() {
            // Get ranking
            Long topVolunteerId = given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .body("[0].totalPoints", equalTo(300))
                .extract()
                .jsonPath()
                .getLong("[0].id");

            // Get individual points
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/{id}/points", topVolunteerId)
            .then()
                .statusCode(200)
                .body("totalPoints", equalTo(300));
        }

        @Test
        @DisplayName("Deve atualizar ranking quando pontos são alterados")
        void shouldUpdateRankingWhenPointsAreChanged() {
            // Initially volunteer1 has 150 points
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .body("[0].id", equalTo(volunteer2.getId().intValue()));

            // Update volunteer1 to have more points
            volunteer1.setTotalPoints(500);
            volunteerRepository.save(volunteer1);

            // Now volunteer1 should be first
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .body("[0].id", equalTo(volunteer1.getId().intValue()))
                .body("[0].totalPoints", equalTo(500));
        }

        @Test
        @DisplayName("Deve manter top volunteers consistente com ranking")
        void shouldMaintainTopVolunteersConsistentWithRanking() {
            // Get top 2
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/top/2")
            .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].id", equalTo(volunteer2.getId().intValue()))
                .body("[1].id", equalTo(volunteer1.getId().intValue()));

            // Get ranking and verify same order
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .body("[0].id", equalTo(volunteer2.getId().intValue()))
                .body("[1].id", equalTo(volunteer1.getId().intValue()));
        }
    }

    @Nested
    @DisplayName("Response Format E2E Tests")
    class ResponseFormatE2ETests {

        @Test
        @DisplayName("Deve retornar JSON válido para pontos")
        void shouldReturnValidJsonForPoints() {
            given()
                .accept(ContentType.JSON)
            .when()
                .get("/{id}/points", volunteer1.getId())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar JSON válido para ranking")
        void shouldReturnValidJsonForRanking() {
            given()
                .accept(ContentType.JSON)
            .when()
                .get("/ranking")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("Deve retornar JSON válido para top volunteers")
        void shouldReturnValidJsonForTopVolunteers() {
            given()
                .accept(ContentType.JSON)
            .when()
                .get("/top/5")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        }
    }
}
