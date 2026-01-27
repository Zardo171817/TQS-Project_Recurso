package com.example.demo;

import com.example.demo.controller.BenefitController;
import com.example.demo.controller.RedemptionController;
import com.example.demo.dto.*;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BenefitService;
import com.example.demo.service.RedemptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Resgates - Controller Unit Tests")
class VerResgatesControllerTest {

    private MockMvc benefitMvc;
    private MockMvc redemptionMvc;
    private ObjectMapper objectMapper;

    @Mock private BenefitService benefitService;
    @Mock private RedemptionService redemptionService;
    @InjectMocks private BenefitController benefitController;
    @InjectMocks private RedemptionController redemptionController;

    private BenefitResponse benefitResponse;
    private RedemptionResponse redemptionResponse;
    private PartnerRedemptionStatsResponse statsResponse;

    @BeforeEach
    void setUp() {
        benefitMvc = MockMvcBuilders.standaloneSetup(benefitController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        redemptionMvc = MockMvcBuilders.standaloneSetup(redemptionController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        benefitResponse = new BenefitResponse();
        benefitResponse.setId(1L);
        benefitResponse.setName("Desconto Cinema");
        benefitResponse.setDescription("20% desconto");
        benefitResponse.setPointsRequired(150);
        benefitResponse.setCategory(BenefitCategory.PARTNER);
        benefitResponse.setProvider("Cinema NOS");
        benefitResponse.setActive(true);

        redemptionResponse = new RedemptionResponse();
        redemptionResponse.setId(1L);
        redemptionResponse.setVolunteerId(1L);
        redemptionResponse.setVolunteerName("Maria");
        redemptionResponse.setBenefitId(1L);
        redemptionResponse.setBenefitName("Desconto");
        redemptionResponse.setBenefitProvider("Cinema NOS");
        redemptionResponse.setPointsSpent(150);
        redemptionResponse.setStatus(RedemptionStatus.COMPLETED);

        BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse();
        detail.setBenefitId(1L);
        detail.setBenefitName("Desconto");
        detail.setTotalRedemptions(5L);
        detail.setTotalPointsRedeemed(750L);

        statsResponse = new PartnerRedemptionStatsResponse();
        statsResponse.setProvider("Cinema NOS");
        statsResponse.setTotalBenefits(1);
        statsResponse.setTotalRedemptions(5L);
        statsResponse.setTotalPointsRedeemed(750L);
        statsResponse.setBenefitDetails(List.of(detail));
        statsResponse.setRecentRedemptions(List.of(redemptionResponse));
    }

    // ========== Partner Stats Endpoint ==========

    @Test
    @DisplayName("GET /api/redemptions/partner/{provider}/stats - returns 200")
    void getPartnerStats_returns200() throws Exception {
        when(redemptionService.getPartnerRedemptionStats("Cinema NOS")).thenReturn(statsResponse);
        redemptionMvc.perform(get("/api/redemptions/partner/Cinema NOS/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider", is("Cinema NOS")))
                .andExpect(jsonPath("$.totalBenefits", is(1)))
                .andExpect(jsonPath("$.totalRedemptions", is(5)));
    }

    @Test
    @DisplayName("GET /api/redemptions/partner/{provider}/stats - returns 404 when not found")
    void getPartnerStats_returns404() throws Exception {
        when(redemptionService.getPartnerRedemptionStats("Unknown"))
                .thenThrow(new ResourceNotFoundException("Not found"));
        redemptionMvc.perform(get("/api/redemptions/partner/Unknown/stats"))
                .andExpect(status().isNotFound());
    }

    // ========== Partner Redemptions Endpoint ==========

    @Test
    @DisplayName("GET /api/redemptions/partner/{provider} - returns 200")
    void getRedemptionsByProvider_returns200() throws Exception {
        when(redemptionService.getRedemptionsByProvider("Cinema NOS"))
                .thenReturn(List.of(redemptionResponse));
        redemptionMvc.perform(get("/api/redemptions/partner/Cinema NOS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/redemptions/partner/{provider} - returns 404 when not found")
    void getRedemptionsByProvider_returns404() throws Exception {
        when(redemptionService.getRedemptionsByProvider("Unknown"))
                .thenThrow(new ResourceNotFoundException("Not found"));
        redemptionMvc.perform(get("/api/redemptions/partner/Unknown"))
                .andExpect(status().isNotFound());
    }

    // ========== Create Partner Benefit ==========

    @Test
    @DisplayName("POST /api/benefits/partner - returns 201")
    void createPartnerBenefit_returns201() throws Exception {
        CreateBenefitRequest request = new CreateBenefitRequest("Test", "desc", 100, "Provider", null);
        when(benefitService.createPartnerBenefit(any())).thenReturn(benefitResponse);
        benefitMvc.perform(post("/api/benefits/partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category", is("PARTNER")));
    }

    @Test
    @DisplayName("POST /api/benefits/partner - returns 400 when name blank")
    void createPartnerBenefit_returns400WhenNameBlank() throws Exception {
        benefitMvc.perform(post("/api/benefits/partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"d\",\"pointsRequired\":100,\"provider\":\"p\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/benefits/partner - returns 400 when points negative")
    void createPartnerBenefit_returns400WhenPointsNegative() throws Exception {
        benefitMvc.perform(post("/api/benefits/partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"t\",\"description\":\"d\",\"pointsRequired\":-5,\"provider\":\"p\"}"))
                .andExpect(status().isBadRequest());
    }

    // ========== Update Partner Benefit ==========

    @Test
    @DisplayName("PUT /api/benefits/partner/{id} - returns 200")
    void updatePartnerBenefit_returns200() throws Exception {
        when(benefitService.updatePartnerBenefit(eq(1L), any())).thenReturn(benefitResponse);
        benefitMvc.perform(put("/api/benefits/partner/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/benefits/partner/{id} - returns 404 when not found")
    void updatePartnerBenefit_returns404() throws Exception {
        when(benefitService.updatePartnerBenefit(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));
        benefitMvc.perform(put("/api/benefits/partner/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/benefits/partner/{id} - returns 409 for UA benefit")
    void updatePartnerBenefit_returns409() throws Exception {
        when(benefitService.updatePartnerBenefit(eq(2L), any()))
                .thenThrow(new IllegalStateException("Only PARTNER"));
        benefitMvc.perform(put("/api/benefits/partner/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\"}"))
                .andExpect(status().isConflict());
    }

    // ========== Delete Partner Benefit ==========

    @Test
    @DisplayName("DELETE /api/benefits/partner/{id} - returns 204")
    void deletePartnerBenefit_returns204() throws Exception {
        doNothing().when(benefitService).deactivatePartnerBenefit(1L);
        benefitMvc.perform(delete("/api/benefits/partner/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/benefits/partner/{id} - returns 404 when not found")
    void deletePartnerBenefit_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(benefitService).deactivatePartnerBenefit(999L);
        benefitMvc.perform(delete("/api/benefits/partner/999"))
                .andExpect(status().isNotFound());
    }

    // ========== Get Partner Benefits ==========

    @Test
    @DisplayName("GET /api/benefits/partner - returns 200")
    void getPartnerBenefits_returns200() throws Exception {
        when(benefitService.getPartnerBenefits()).thenReturn(List.of(benefitResponse));
        benefitMvc.perform(get("/api/benefits/partner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/benefits/partner/provider/{provider} - returns 200")
    void getPartnerBenefitsByProvider_returns200() throws Exception {
        when(benefitService.getPartnerBenefitsByProvider("Cinema")).thenReturn(List.of(benefitResponse));
        benefitMvc.perform(get("/api/benefits/partner/provider/Cinema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ========== Redemption Endpoints ==========

    @Test
    @DisplayName("POST /api/redemptions - returns 201")
    void createRedemption_returns201() throws Exception {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);
        when(redemptionService.redeemPoints(any())).thenReturn(redemptionResponse);
        redemptionMvc.perform(post("/api/redemptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pointsSpent", is(150)));
    }

    @Test
    @DisplayName("GET /api/redemptions/{id} - returns 200")
    void getRedemptionById_returns200() throws Exception {
        when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);
        redemptionMvc.perform(get("/api/redemptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/redemptions/volunteer/{id} - returns 200")
    void getRedemptionsByVolunteer_returns200() throws Exception {
        when(redemptionService.getRedemptionsByVolunteer(1L)).thenReturn(List.of(redemptionResponse));
        redemptionMvc.perform(get("/api/redemptions/volunteer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/redemptions/volunteer/{id}/total-spent - returns 200")
    void getTotalPointsSpent_returns200() throws Exception {
        when(redemptionService.getTotalPointsSpent(1L)).thenReturn(450);
        redemptionMvc.perform(get("/api/redemptions/volunteer/1/total-spent"))
                .andExpect(status().isOk())
                .andExpect(content().string("450"));
    }

    @Test
    @DisplayName("GET /api/redemptions/volunteer/{id}/count - returns 200")
    void getRedemptionCount_returns200() throws Exception {
        when(redemptionService.getRedemptionCount(1L)).thenReturn(5L);
        redemptionMvc.perform(get("/api/redemptions/volunteer/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    // ========== Other Benefit Endpoints ==========

    @Test
    @DisplayName("GET /api/benefits - returns 200")
    void getAllBenefits_returns200() throws Exception {
        when(benefitService.getAllActiveBenefits()).thenReturn(List.of(benefitResponse));
        benefitMvc.perform(get("/api/benefits"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/benefits/{id} - returns 200")
    void getBenefitById_returns200() throws Exception {
        when(benefitService.getBenefitById(1L)).thenReturn(benefitResponse);
        benefitMvc.perform(get("/api/benefits/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/benefits/category/{category} - returns 200")
    void getBenefitsByCategory_returns200() throws Exception {
        when(benefitService.getBenefitsByCategory(BenefitCategory.PARTNER)).thenReturn(List.of(benefitResponse));
        benefitMvc.perform(get("/api/benefits/category/PARTNER"))
                .andExpect(status().isOk());
    }
}
