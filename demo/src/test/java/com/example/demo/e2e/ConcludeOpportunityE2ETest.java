package com.example.demo.e2e;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Conclude Opportunity E2E Tests")
class ConcludeOpportunityE2ETest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Promoter promoter;
    private Opportunity opportunity;
    private Volunteer volunteer;
    private Application application;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/opportunities";

        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        volunteerRepository.deleteAll();
        promoterRepository.deleteAll();

        promoter = new Promoter();
        promoter.setName("E2E Promoter");
        promoter.setEmail("e2e@test.com");
        promoter.setOrganization("E2E Org");
        promoter = promoterRepository.save(promoter);

        opportunity = new Opportunity();
        opportunity.setTitle("E2E Beach Cleanup");
        opportunity.setDescription("E2E test cleanup event");
        opportunity.setSkills("Cleaning");
        opportunity.setCategory("Environment");
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(150);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());
        opportunity = opportunityRepository.save(opportunity);

        volunteer = new Volunteer();
        volunteer.setName("E2E Volunteer");
        volunteer.setEmail("volunteer@e2e.com");
        volunteer.setPhone("987654321");
        volunteer.setSkills("Cleaning");
        volunteer.setTotalPoints(0);
        volunteer = volunteerRepository.save(volunteer);

        application = new Application();
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("E2E test motivation");
        application.setAppliedAt(LocalDateTime.now());
        application.setParticipationConfirmed(false);
        application.setPointsAwarded(0);
        application = applicationRepository.save(application);
    }

    @Test
    @DisplayName("E2E: Complete conclude opportunity flow with single participant")
    void e2e_ConcludeOpportunity_SingleParticipant_Success() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                promoter.getId(), Collections.singletonList(application.getId()));

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/{id}/conclude", opportunity.getId())
        .then()
                .statusCode(200)
                .body("opportunityId", equalTo(opportunity.getId().intValue()))
                .body("opportunityTitle", equalTo("E2E Beach Cleanup"))
                .body("status", equalTo("CONCLUDED"))
                .body("totalParticipantsConfirmed", equalTo(1))
                .body("totalPointsAwarded", equalTo(150))
                .body("confirmedParticipants", hasSize(1))
                .body("confirmedParticipants[0].volunteerName", equalTo("E2E Volunteer"))
                .body("confirmedParticipants[0].pointsAwarded", equalTo(150))
                .body("concludedAt", notNullValue());
    }

    @Test
    @DisplayName("E2E: Complete conclude opportunity flow with multiple participants")
    void e2e_ConcludeOpportunity_MultipleParticipants_Success() {
        Volunteer volunteer2 = new Volunteer();
        volunteer2.setName("Second Volunteer");
        volunteer2.setEmail("second@e2e.com");
        volunteer2.setPhone("555555555");
        volunteer2.setSkills("Organizing");
        volunteer2.setTotalPoints(100);
        volunteer2 = volunteerRepository.save(volunteer2);

        Application application2 = new Application();
        application2.setVolunteer(volunteer2);
        application2.setOpportunity(opportunity);
        application2.setStatus(ApplicationStatus.ACCEPTED);
        application2.setMotivation("Second motivation");
        application2.setAppliedAt(LocalDateTime.now());
        application2.setParticipationConfirmed(false);
        application2.setPointsAwarded(0);
        application2 = applicationRepository.save(application2);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                promoter.getId(), Arrays.asList(application.getId(), application2.getId()));

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/{id}/conclude", opportunity.getId())
        .then()
                .statusCode(200)
                .body("totalParticipantsConfirmed", equalTo(2))
                .body("totalPointsAwarded", equalTo(300))
                .body("confirmedParticipants", hasSize(2));
    }

    @Test
    @DisplayName("E2E: Conclude opportunity with empty participant list")
    void e2e_ConcludeOpportunity_EmptyParticipants_Success() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                promoter.getId(), Collections.emptyList());

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/{id}/conclude", opportunity.getId())
        .then()
                .statusCode(200)
                .body("totalParticipantsConfirmed", equalTo(0))
                .body("totalPointsAwarded", equalTo(0))
                .body("status", equalTo("CONCLUDED"));
    }

    @Test
    @DisplayName("E2E: Confirm individual participation and verify points")
    void e2e_ConfirmParticipation_Success() {
        given()
                .param("promoterId", promoter.getId())
        .when()
                .post("/applications/{id}/confirm-participation", application.getId())
        .then()
                .statusCode(200)
                .body("participationConfirmed", equalTo(true))
                .body("pointsAwarded", equalTo(150))
                .body("confirmedAt", notNullValue());
    }

    @Test
    @DisplayName("E2E: Confirm participation fails with wrong promoter")
    void e2e_ConfirmParticipation_WrongPromoter_Fails() {
        Promoter otherPromoter = new Promoter();
        otherPromoter.setName("Other Promoter");
        otherPromoter.setEmail("other@e2e.com");
        otherPromoter.setOrganization("Other Org");
        otherPromoter = promoterRepository.save(otherPromoter);

        given()
                .param("promoterId", otherPromoter.getId())
        .when()
                .post("/applications/{id}/confirm-participation", application.getId())
        .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("E2E: Get opportunities by status OPEN")
    void e2e_GetOpportunitiesByStatus_Open_Success() {
        given()
        .when()
                .get("/status/OPEN")
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].status", equalTo("OPEN"))
                .body("[0].title", equalTo("E2E Beach Cleanup"));
    }

    @Test
    @DisplayName("E2E: Get opportunities by status CONCLUDED")
    void e2e_GetOpportunitiesByStatus_Concluded_Success() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());
        opportunityRepository.save(opportunity);

        given()
        .when()
                .get("/status/CONCLUDED")
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].status", equalTo("CONCLUDED"))
                .body("[0].concludedAt", notNullValue());
    }

    @Test
    @DisplayName("E2E: Get opportunities by promoter and status")
    void e2e_GetByPromoterAndStatus_Success() {
        given()
        .when()
                .get("/promoter/{promoterId}/status/{status}", promoter.getId(), "OPEN")
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].title", equalTo("E2E Beach Cleanup"));
    }

    @Test
    @DisplayName("E2E: Get accepted applications for opportunity")
    void e2e_GetAcceptedApplications_Success() {
        given()
        .when()
                .get("/{id}/accepted-applications", opportunity.getId())
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].status", equalTo("ACCEPTED"));
    }

    @Test
    @DisplayName("E2E: Count concluded opportunities by promoter")
    void e2e_CountConcludedOpportunities_Success() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());
        opportunityRepository.save(opportunity);

        given()
        .when()
                .get("/promoter/{id}/concluded-count", promoter.getId())
        .then()
                .statusCode(200)
                .body(equalTo("1"));
    }

    @Test
    @DisplayName("E2E: Count concluded opportunities returns zero")
    void e2e_CountConcludedOpportunities_Zero() {
        given()
        .when()
                .get("/promoter/{id}/concluded-count", promoter.getId())
        .then()
                .statusCode(200)
                .body(equalTo("0"));
    }

    @Test
    @DisplayName("E2E: Conclude opportunity fails with wrong promoter")
    void e2e_ConcludeOpportunity_WrongPromoter_Fails() {
        Promoter otherPromoter = new Promoter();
        otherPromoter.setName("Other Promoter");
        otherPromoter.setEmail("other2@e2e.com");
        otherPromoter.setOrganization("Other Org");
        otherPromoter = promoterRepository.save(otherPromoter);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                otherPromoter.getId(), Collections.singletonList(application.getId()));

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/{id}/conclude", opportunity.getId())
        .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("E2E: Conclude already concluded opportunity fails")
    void e2e_ConcludeOpportunity_AlreadyConcluded_Fails() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());
        opportunityRepository.save(opportunity);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest(
                promoter.getId(), Collections.singletonList(application.getId()));

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/{id}/conclude", opportunity.getId())
        .then()
                .statusCode(409);
    }
}
