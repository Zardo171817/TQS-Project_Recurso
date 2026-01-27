package com.example.demo;

import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.RedeemPointsRequest;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Ver Resgates - Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VerResgatesIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired private BenefitRepository benefitRepository;
    @Autowired private VolunteerRepository volunteerRepository;
    @Autowired private RedemptionRepository redemptionRepository;

    private Volunteer volunteer;
    private Benefit partnerBenefit;
    private Benefit uaBenefit;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";

        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();

        volunteer = new Volunteer();
        volunteer.setName("Maria Silva");
        volunteer.setEmail("maria@test.com");
        volunteer.setTotalPoints(1000);
        volunteer = volunteerRepository.save(volunteer);

        partnerBenefit = new Benefit();
        partnerBenefit.setName("Desconto Cinema");
        partnerBenefit.setDescription("20% desconto");
        partnerBenefit.setPointsRequired(150);
        partnerBenefit.setCategory(BenefitCategory.PARTNER);
        partnerBenefit.setProvider("Cinema NOS");
        partnerBenefit.setActive(true);
        partnerBenefit.setCreatedAt(LocalDateTime.now());
        partnerBenefit = benefitRepository.save(partnerBenefit);

        uaBenefit = new Benefit();
        uaBenefit.setName("Desconto UA");
        uaBenefit.setDescription("10% desconto");
        uaBenefit.setPointsRequired(100);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("UA");
        uaBenefit.setActive(true);
        uaBenefit.setCreatedAt(LocalDateTime.now());
        uaBenefit = benefitRepository.save(uaBenefit);
    }

    @AfterEach
    void tearDown() {
        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    // ========== Partner Stats ==========

    @Test @Order(1)
    @DisplayName("GET /partner/{provider}/stats - returns stats with zero redemptions")
    void getPartnerStats_returnsStatsWithZeroRedemptions() {
        given().contentType(ContentType.JSON)
            .when().get("/redemptions/partner/Cinema NOS/stats")
            .then().statusCode(200)
                .body("provider", equalTo("Cinema NOS"))
                .body("totalBenefits", equalTo(1))
                .body("totalRedemptions", equalTo(0));
    }

    @Test @Order(2)
    @DisplayName("GET /partner/{provider}/stats - returns stats with redemptions")
    void getPartnerStats_returnsStatsWithRedemptions() {
        createRedemption();
        createRedemption();

        given().contentType(ContentType.JSON)
            .when().get("/redemptions/partner/Cinema NOS/stats")
            .then().statusCode(200)
                .body("totalRedemptions", equalTo(2))
                .body("totalPointsRedeemed", equalTo(300));
    }

    @Test @Order(3)
    @DisplayName("GET /partner/{provider}/stats - returns 404 when not found")
    void getPartnerStats_returns404() {
        given().contentType(ContentType.JSON)
            .when().get("/redemptions/partner/Unknown/stats")
            .then().statusCode(404);
    }

    @Test @Order(4)
    @DisplayName("GET /partner/{provider}/stats - returns 404 for UA only")
    void getPartnerStats_returns404ForUaOnly() {
        given().contentType(ContentType.JSON)
            .when().get("/redemptions/partner/UA/stats")
            .then().statusCode(404);
    }

    // ========== Partner Redemptions ==========

    @Test @Order(5)
    @DisplayName("GET /partner/{provider} - returns redemptions")
    void getRedemptionsByProvider_returnsRedemptions() {
        createRedemption();
        given().contentType(ContentType.JSON)
            .when().get("/redemptions/partner/Cinema NOS")
            .then().statusCode(200)
                .body("$", hasSize(1));
    }

    // ========== Create Partner Benefit ==========

    @Test @Order(6)
    @DisplayName("POST /benefits/partner - creates benefit")
    void createPartnerBenefit_createsBenefit() {
        CreateBenefitRequest request = new CreateBenefitRequest("New", "desc", 200, "Provider", null);
        given().contentType(ContentType.JSON).body(request)
            .when().post("/benefits/partner")
            .then().statusCode(201)
                .body("category", equalTo("PARTNER"))
                .body("id", notNullValue());
    }

    @Test @Order(7)
    @DisplayName("POST /benefits/partner - returns 400 for invalid data")
    void createPartnerBenefit_returns400() {
        given().contentType(ContentType.JSON)
            .body("{\"name\":\"\",\"description\":\"d\",\"pointsRequired\":100,\"provider\":\"p\"}")
            .when().post("/benefits/partner")
            .then().statusCode(400);
    }

    // ========== Update Partner Benefit ==========

    @Test @Order(8)
    @DisplayName("PUT /benefits/partner/{id} - updates benefit")
    void updatePartnerBenefit_updatesBenefit() {
        given().contentType(ContentType.JSON)
            .body("{\"name\":\"Updated Name\"}")
            .when().put("/benefits/partner/" + partnerBenefit.getId())
            .then().statusCode(200)
                .body("name", equalTo("Updated Name"));
    }

    @Test @Order(9)
    @DisplayName("PUT /benefits/partner/{id} - returns 404 when not found")
    void updatePartnerBenefit_returns404() {
        given().contentType(ContentType.JSON)
            .body("{\"name\":\"Test\"}")
            .when().put("/benefits/partner/99999")
            .then().statusCode(404);
    }

    @Test @Order(10)
    @DisplayName("PUT /benefits/partner/{id} - returns 409 for UA benefit")
    void updatePartnerBenefit_returns409() {
        given().contentType(ContentType.JSON)
            .body("{\"name\":\"Test\"}")
            .when().put("/benefits/partner/" + uaBenefit.getId())
            .then().statusCode(409);
    }

    // ========== Delete Partner Benefit ==========

    @Test @Order(11)
    @DisplayName("DELETE /benefits/partner/{id} - deactivates benefit")
    void deletePartnerBenefit_deactivatesBenefit() {
        given().contentType(ContentType.JSON)
            .when().delete("/benefits/partner/" + partnerBenefit.getId())
            .then().statusCode(204);

        Benefit updated = benefitRepository.findById(partnerBenefit.getId()).orElseThrow();
        Assertions.assertFalse(updated.getActive());
    }

    @Test @Order(12)
    @DisplayName("DELETE /benefits/partner/{id} - returns 409 for UA benefit")
    void deletePartnerBenefit_returns409() {
        given().contentType(ContentType.JSON)
            .when().delete("/benefits/partner/" + uaBenefit.getId())
            .then().statusCode(409);
    }

    // ========== Get Benefits ==========

    @Test @Order(13)
    @DisplayName("GET /benefits/partner - returns partner benefits")
    void getPartnerBenefits_returnsPartnerBenefits() {
        given().contentType(ContentType.JSON)
            .when().get("/benefits/partner")
            .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].category", equalTo("PARTNER"));
    }

    @Test @Order(14)
    @DisplayName("GET /benefits/partner/provider/{provider} - filters by provider")
    void getPartnerBenefitsByProvider_filtersByProvider() {
        given().contentType(ContentType.JSON)
            .when().get("/benefits/partner/provider/Cinema")
            .then().statusCode(200)
                .body("$", hasSize(1));
    }

    // ========== Redeem Points ==========

    @Test @Order(15)
    @DisplayName("POST /redemptions - redeems points")
    void redeemPoints_redeemsPoints() {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(volunteer.getId());
        request.setBenefitId(partnerBenefit.getId());

        given().contentType(ContentType.JSON).body(request)
            .when().post("/redemptions")
            .then().statusCode(201)
                .body("pointsSpent", equalTo(150));

        Volunteer updated = volunteerRepository.findById(volunteer.getId()).orElseThrow();
        Assertions.assertEquals(850, updated.getTotalPoints());
    }

    @Test @Order(16)
    @DisplayName("POST /redemptions - returns 404 when volunteer not found")
    void redeemPoints_returns404WhenVolunteerNotFound() {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(99999L);
        request.setBenefitId(partnerBenefit.getId());

        given().contentType(ContentType.JSON).body(request)
            .when().post("/redemptions")
            .then().statusCode(404);
    }

    @Test @Order(17)
    @DisplayName("POST /redemptions - returns 409 when insufficient points")
    void redeemPoints_returns409WhenInsufficientPoints() {
        volunteer.setTotalPoints(10);
        volunteerRepository.save(volunteer);

        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(volunteer.getId());
        request.setBenefitId(partnerBenefit.getId());

        given().contentType(ContentType.JSON).body(request)
            .when().post("/redemptions")
            .then().statusCode(409);
    }

    // ========== Redemption Queries ==========

    @Test @Order(18)
    @DisplayName("GET /redemptions/{id} - returns redemption")
    void getRedemptionById_returnsRedemption() {
        Redemption r = createRedemption();
        given().contentType(ContentType.JSON)
            .when().get("/redemptions/" + r.getId())
            .then().statusCode(200)
                .body("id", equalTo(r.getId().intValue()));
    }

    @Test @Order(19)
    @DisplayName("GET /redemptions/volunteer/{id} - returns volunteer redemptions")
    void getRedemptionsByVolunteer_returnsRedemptions() {
        createRedemption();
        createRedemption();

        given().contentType(ContentType.JSON)
            .when().get("/redemptions/volunteer/" + volunteer.getId())
            .then().statusCode(200)
                .body("$", hasSize(2));
    }

    @Test @Order(20)
    @DisplayName("GET /redemptions/volunteer/{id}/total-spent - returns total")
    void getTotalPointsSpent_returnsTotal() {
        createRedemption();
        createRedemption();

        given().contentType(ContentType.JSON)
            .when().get("/redemptions/volunteer/" + volunteer.getId() + "/total-spent")
            .then().statusCode(200)
                .body(equalTo("300"));
    }

    @Test @Order(21)
    @DisplayName("GET /redemptions/volunteer/{id}/count - returns count")
    void getRedemptionCount_returnsCount() {
        createRedemption();
        createRedemption();
        createRedemption();

        given().contentType(ContentType.JSON)
            .when().get("/redemptions/volunteer/" + volunteer.getId() + "/count")
            .then().statusCode(200)
                .body(equalTo("3"));
    }

    // ========== Other Endpoints ==========

    @Test @Order(22)
    @DisplayName("GET /benefits - returns all active benefits")
    void getAllBenefits_returnsAllActive() {
        given().contentType(ContentType.JSON)
            .when().get("/benefits")
            .then().statusCode(200)
                .body("$", hasSize(2));
    }

    @Test @Order(23)
    @DisplayName("GET /benefits/{id} - returns benefit by id")
    void getBenefitById_returnsBenefit() {
        given().contentType(ContentType.JSON)
            .when().get("/benefits/" + partnerBenefit.getId())
            .then().statusCode(200)
                .body("id", equalTo(partnerBenefit.getId().intValue()));
    }

    @Test @Order(24)
    @DisplayName("GET /benefits/category/{category} - returns benefits by category")
    void getBenefitsByCategory_returnsByCategory() {
        given().contentType(ContentType.JSON)
            .when().get("/benefits/category/PARTNER")
            .then().statusCode(200)
                .body("$", hasSize(1));
    }

    private Redemption createRedemption() {
        Redemption r = new Redemption();
        r.setVolunteer(volunteer);
        r.setBenefit(partnerBenefit);
        r.setPointsSpent(partnerBenefit.getPointsRequired());
        r.setStatus(RedemptionStatus.COMPLETED);
        r.setRedeemedAt(LocalDateTime.now());
        return redemptionRepository.save(r);
    }
}
