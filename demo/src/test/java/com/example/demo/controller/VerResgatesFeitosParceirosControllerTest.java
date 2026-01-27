package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BenefitService;
import com.example.demo.service.RedemptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Resgates Feitos Parceiro - Controller Tests")
class VerResgatesFeitosParceirosControllerTest {

    private MockMvc benefitMockMvc;
    private MockMvc redemptionMockMvc;

    @Mock
    private BenefitService benefitService;

    @Mock
    private RedemptionService redemptionService;

    @InjectMocks
    private BenefitController benefitController;

    @InjectMocks
    private RedemptionController redemptionController;

    private ObjectMapper objectMapper;

    private BenefitResponse partnerBenefitResponse;
    private BenefitResponse partnerBenefitResponse2;
    private RedemptionResponse redemptionResponse;
    private PartnerRedemptionStatsResponse statsResponse;

    @BeforeEach
    void setUp() {
        benefitMockMvc = MockMvcBuilders.standaloneSetup(benefitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        redemptionMockMvc = MockMvcBuilders.standaloneSetup(redemptionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        partnerBenefitResponse = new BenefitResponse();
        partnerBenefitResponse.setId(1L);
        partnerBenefitResponse.setName("Desconto Cinema NOS");
        partnerBenefitResponse.setDescription("20% de desconto em bilhetes de cinema");
        partnerBenefitResponse.setPointsRequired(150);
        partnerBenefitResponse.setCategory(BenefitCategory.PARTNER);
        partnerBenefitResponse.setProvider("Cinema NOS");
        partnerBenefitResponse.setImageUrl("http://example.com/cinema.jpg");
        partnerBenefitResponse.setActive(true);
        partnerBenefitResponse.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 30, 0));

        partnerBenefitResponse2 = new BenefitResponse();
        partnerBenefitResponse2.setId(2L);
        partnerBenefitResponse2.setName("Voucher Restaurante");
        partnerBenefitResponse2.setDescription("Voucher de 10 euros");
        partnerBenefitResponse2.setPointsRequired(300);
        partnerBenefitResponse2.setCategory(BenefitCategory.PARTNER);
        partnerBenefitResponse2.setProvider("Restaurante Sabor");
        partnerBenefitResponse2.setActive(true);
        partnerBenefitResponse2.setCreatedAt(LocalDateTime.of(2024, 6, 20, 14, 0, 0));

        redemptionResponse = new RedemptionResponse();
        redemptionResponse.setId(1L);
        redemptionResponse.setVolunteerId(1L);
        redemptionResponse.setVolunteerName("Maria Silva");
        redemptionResponse.setVolunteerEmail("maria@email.com");
        redemptionResponse.setBenefitId(1L);
        redemptionResponse.setBenefitName("Desconto Cinema NOS");
        redemptionResponse.setBenefitDescription("20% de desconto");
        redemptionResponse.setBenefitProvider("Cinema NOS");
        redemptionResponse.setPointsSpent(150);
        redemptionResponse.setStatus(RedemptionStatus.COMPLETED);
        redemptionResponse.setRedeemedAt(LocalDateTime.now());
        redemptionResponse.setRemainingPoints(350);

        BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse();
        detail.setBenefitId(1L);
        detail.setBenefitName("Desconto Cinema NOS");
        detail.setBenefitDescription("20% de desconto");
        detail.setPointsRequired(150);
        detail.setProvider("Cinema NOS");
        detail.setActive(true);
        detail.setTotalRedemptions(5L);
        detail.setTotalPointsRedeemed(750L);

        statsResponse = new PartnerRedemptionStatsResponse();
        statsResponse.setProvider("Cinema NOS");
        statsResponse.setTotalBenefits(1);
        statsResponse.setTotalRedemptions(5L);
        statsResponse.setTotalPointsRedeemed(750L);
        statsResponse.setBenefitDetails(Arrays.asList(detail));
        statsResponse.setRecentRedemptions(Arrays.asList(redemptionResponse));
    }

    @Nested
    @DisplayName("GET /api/redemptions/partner/{provider}/stats Tests")
    class GetPartnerRedemptionStatsTests {

        @Test
        @DisplayName("Should return 200 OK with partner stats")
        void shouldReturn200WithPartnerStats() throws Exception {
            when(redemptionService.getPartnerRedemptionStats("Cinema NOS"))
                    .thenReturn(statsResponse);

            redemptionMockMvc.perform(get("/api/redemptions/partner/Cinema NOS/stats"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.provider", is("Cinema NOS")))
                    .andExpect(jsonPath("$.totalBenefits", is(1)))
                    .andExpect(jsonPath("$.totalRedemptions", is(5)))
                    .andExpect(jsonPath("$.totalPointsRedeemed", is(750)))
                    .andExpect(jsonPath("$.benefitDetails", hasSize(1)))
                    .andExpect(jsonPath("$.recentRedemptions", hasSize(1)));

            verify(redemptionService).getPartnerRedemptionStats("Cinema NOS");
        }

        @Test
        @DisplayName("Should return 404 when provider not found")
        void shouldReturn404WhenProviderNotFound() throws Exception {
            when(redemptionService.getPartnerRedemptionStats("Desconhecido"))
                    .thenThrow(new ResourceNotFoundException("No benefits found for provider: Desconhecido"));

            redemptionMockMvc.perform(get("/api/redemptions/partner/Desconhecido/stats"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return correct benefit detail structure")
        void shouldReturnCorrectBenefitDetailStructure() throws Exception {
            when(redemptionService.getPartnerRedemptionStats("Cinema NOS"))
                    .thenReturn(statsResponse);

            redemptionMockMvc.perform(get("/api/redemptions/partner/Cinema NOS/stats"))
                    .andExpect(jsonPath("$.benefitDetails[0].benefitId", is(1)))
                    .andExpect(jsonPath("$.benefitDetails[0].benefitName", is("Desconto Cinema NOS")))
                    .andExpect(jsonPath("$.benefitDetails[0].totalRedemptions", is(5)))
                    .andExpect(jsonPath("$.benefitDetails[0].totalPointsRedeemed", is(750)));
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/partner/{provider} Tests")
    class GetRedemptionsByProviderTests {

        @Test
        @DisplayName("Should return 200 OK with provider redemptions")
        void shouldReturn200WithProviderRedemptions() throws Exception {
            when(redemptionService.getRedemptionsByProvider("Cinema NOS"))
                    .thenReturn(Arrays.asList(redemptionResponse));

            redemptionMockMvc.perform(get("/api/redemptions/partner/Cinema NOS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].benefitProvider", is("Cinema NOS")));
        }

        @Test
        @DisplayName("Should return 404 when provider not found")
        void shouldReturn404WhenProviderNotFound() throws Exception {
            when(redemptionService.getRedemptionsByProvider("Desconhecido"))
                    .thenThrow(new ResourceNotFoundException("No benefits found"));

            redemptionMockMvc.perform(get("/api/redemptions/partner/Desconhecido"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty list for provider with no redemptions")
        void shouldReturnEmptyListForProviderWithNoRedemptions() throws Exception {
            when(redemptionService.getRedemptionsByProvider("Cinema NOS"))
                    .thenReturn(Collections.emptyList());

            redemptionMockMvc.perform(get("/api/redemptions/partner/Cinema NOS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("POST /api/benefits/partner Tests")
    class CreatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 201 CREATED on successful creation")
        void shouldReturn201OnSuccessfulCreation() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Desconto Cinema NOS", "20% de desconto", 150, "Cinema NOS", "http://example.com/cinema.jpg"
            );

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Desconto Cinema NOS")))
                    .andExpect(jsonPath("$.category", is("PARTNER")));

            verify(benefitService).createPartnerBenefit(any(CreateBenefitRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            String body = "{\"name\":\"\",\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() throws Exception {
            String body = "{\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"\",\"pointsRequired\":100,\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is null")
        void shouldReturn400WhenPointsIsNull() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"desc\",\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is zero")
        void shouldReturn400WhenPointsIsZero() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":0,\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is negative")
        void shouldReturn400WhenPointsIsNegative() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":-5,\"provider\":\"test\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when provider is blank")
        void shouldReturn400WhenProviderIsBlank() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"\"}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when provider is missing")
        void shouldReturn400WhenProviderIsMissing() throws Exception {
            String body = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":100}";

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should accept request without imageUrl")
        void shouldAcceptRequestWithoutImageUrl() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest("Test", "desc", 100, "Provider", null);

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            benefitMockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /api/benefits/partner/{id} Tests")
    class UpdatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 200 OK on successful update")
        void shouldReturn200OnSuccessfulUpdate() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest(
                    "Updated", "Updated desc", 200, "Updated Provider", "http://new.jpg"
            );

            BenefitResponse updated = new BenefitResponse();
            updated.setId(1L);
            updated.setName("Updated");
            updated.setPointsRequired(200);
            updated.setCategory(BenefitCategory.PARTNER);
            updated.setActive(true);
            updated.setCreatedAt(LocalDateTime.now());

            when(benefitService.updatePartnerBenefit(eq(1L), any(UpdateBenefitRequest.class)))
                    .thenReturn(updated);

            benefitMockMvc.perform(put("/api/benefits/partner/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Updated")));
        }

        @Test
        @DisplayName("Should return 404 when benefit not found")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Test");

            when(benefitService.updatePartnerBenefit(eq(999L), any(UpdateBenefitRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Benefit not found with id: 999"));

            benefitMockMvc.perform(put("/api/benefits/partner/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when trying to update non-PARTNER benefit")
        void shouldReturn409WhenUpdatingNonPartnerBenefit() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Test");

            when(benefitService.updatePartnerBenefit(eq(3L), any(UpdateBenefitRequest.class)))
                    .thenThrow(new IllegalStateException("Only PARTNER benefits can be updated"));

            benefitMockMvc.perform(put("/api/benefits/partner/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/benefits/partner/{id} Tests")
    class DeactivatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 204 NO CONTENT on successful deactivation")
        void shouldReturn204OnSuccessfulDeactivation() throws Exception {
            doNothing().when(benefitService).deactivatePartnerBenefit(1L);

            benefitMockMvc.perform(delete("/api/benefits/partner/1"))
                    .andExpect(status().isNoContent());

            verify(benefitService).deactivatePartnerBenefit(1L);
        }

        @Test
        @DisplayName("Should return 404 when benefit not found")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Benefit not found"))
                    .when(benefitService).deactivatePartnerBenefit(999L);

            benefitMockMvc.perform(delete("/api/benefits/partner/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when deactivating non-PARTNER benefit")
        void shouldReturn409WhenDeactivatingNonPartner() throws Exception {
            doThrow(new IllegalStateException("Only PARTNER benefits"))
                    .when(benefitService).deactivatePartnerBenefit(3L);

            benefitMockMvc.perform(delete("/api/benefits/partner/3"))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/partner Tests")
    class GetPartnerBenefitsTests {

        @Test
        @DisplayName("Should return 200 OK with partner benefits")
        void shouldReturn200WithPartnerBenefits() throws Exception {
            when(benefitService.getPartnerBenefits())
                    .thenReturn(Arrays.asList(partnerBenefitResponse, partnerBenefitResponse2));

            benefitMockMvc.perform(get("/api/benefits/partner"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cinema NOS")))
                    .andExpect(jsonPath("$[0].category", is("PARTNER")));
        }

        @Test
        @DisplayName("Should return 200 OK with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(benefitService.getPartnerBenefits())
                    .thenReturn(Collections.emptyList());

            benefitMockMvc.perform(get("/api/benefits/partner"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/partner/provider/{provider} Tests")
    class GetPartnerBenefitsByProviderTests {

        @Test
        @DisplayName("Should return 200 OK with filtered benefits")
        void shouldReturn200WithFilteredBenefits() throws Exception {
            when(benefitService.getPartnerBenefitsByProvider("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/partner/provider/Cinema"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].provider", is("Cinema NOS")));
        }

        @Test
        @DisplayName("Should return empty list for unknown provider")
        void shouldReturnEmptyListForUnknownProvider() throws Exception {
            when(benefitService.getPartnerBenefitsByProvider("Desconhecido"))
                    .thenReturn(Collections.emptyList());

            benefitMockMvc.perform(get("/api/benefits/partner/provider/Desconhecido"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Other Benefit Endpoints Tests")
    class OtherBenefitEndpointsTests {

        @Test
        @DisplayName("Should return all active benefits")
        void shouldReturnAllActiveBenefits() throws Exception {
            when(benefitService.getAllActiveBenefits())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefit by id")
        void shouldReturnBenefitById() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(partnerBenefitResponse);

            benefitMockMvc.perform(get("/api/benefits/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("Should return 404 when benefit not found")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            when(benefitService.getBenefitById(999L))
                    .thenThrow(new ResourceNotFoundException("Benefit not found"));

            benefitMockMvc.perform(get("/api/benefits/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return benefits by category")
        void shouldReturnBenefitsByCategory() throws Exception {
            when(benefitService.getBenefitsByCategory(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/category/PARTNER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return affordable benefits")
        void shouldReturnAffordableBenefits() throws Exception {
            when(benefitService.getAffordableBenefitsForVolunteer(1L))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/volunteer/1/affordable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefits by provider")
        void shouldReturnBenefitsByProvider() throws Exception {
            when(benefitService.getBenefitsByProvider("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/provider/Cinema"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return all providers")
        void shouldReturnAllProviders() throws Exception {
            when(benefitService.getAllProviders())
                    .thenReturn(Arrays.asList("Cinema NOS", "Restaurante Sabor"));

            benefitMockMvc.perform(get("/api/benefits/providers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Should return benefits sorted ascending")
        void shouldReturnBenefitsSortedAsc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsAsc())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/sorted/points-asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefits sorted descending")
        void shouldReturnBenefitsSortedDesc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsDesc())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/sorted/points-desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return catalog for volunteer")
        void shouldReturnCatalogForVolunteer() throws Exception {
            when(benefitService.getCatalogForVolunteer(1L))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            benefitMockMvc.perform(get("/api/benefits/volunteer/1/catalog"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("Redemption Endpoints Tests")
    class RedemptionEndpointsTests {

        @Test
        @DisplayName("Should create redemption successfully")
        void shouldCreateRedemptionSuccessfully() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest();
            request.setVolunteerId(1L);
            request.setBenefitId(1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenReturn(redemptionResponse);

            redemptionMockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.pointsSpent", is(150)));
        }

        @Test
        @DisplayName("Should return redemption by id")
        void shouldReturnRedemptionById() throws Exception {
            when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);

            redemptionMockMvc.perform(get("/api/redemptions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("Should return redemptions by volunteer")
        void shouldReturnRedemptionsByVolunteer() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(1L))
                    .thenReturn(Arrays.asList(redemptionResponse));

            redemptionMockMvc.perform(get("/api/redemptions/volunteer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return completed redemptions by volunteer")
        void shouldReturnCompletedRedemptionsByVolunteer() throws Exception {
            when(redemptionService.getCompletedRedemptionsByVolunteer(1L))
                    .thenReturn(Arrays.asList(redemptionResponse));

            redemptionMockMvc.perform(get("/api/redemptions/volunteer/1/completed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return total points spent")
        void shouldReturnTotalPointsSpent() throws Exception {
            when(redemptionService.getTotalPointsSpent(1L)).thenReturn(450);

            redemptionMockMvc.perform(get("/api/redemptions/volunteer/1/total-spent"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("450"));
        }

        @Test
        @DisplayName("Should return redemption count")
        void shouldReturnRedemptionCount() throws Exception {
            when(redemptionService.getRedemptionCount(1L)).thenReturn(5L);

            redemptionMockMvc.perform(get("/api/redemptions/volunteer/1/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }
    }
}
