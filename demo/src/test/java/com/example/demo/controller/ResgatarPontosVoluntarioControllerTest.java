package com.example.demo.controller;

import com.example.demo.dto.RedeemPointsRequest;
import com.example.demo.dto.RedemptionResponse;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Resgatar Pontos Voluntario - Controller Tests")
class ResgatarPontosVoluntarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RedemptionService redemptionService;

    @InjectMocks
    private RedemptionController redemptionController;

    private ObjectMapper objectMapper;

    private RedemptionResponse redemptionResponse;
    private RedemptionResponse redemptionResponse2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(redemptionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        redemptionResponse = new RedemptionResponse();
        redemptionResponse.setId(1L);
        redemptionResponse.setVolunteerId(1L);
        redemptionResponse.setVolunteerName("Joao Silva");
        redemptionResponse.setVolunteerEmail("joao@example.com");
        redemptionResponse.setBenefitId(1L);
        redemptionResponse.setBenefitName("Desconto Cantina UA");
        redemptionResponse.setBenefitDescription("10% desconto na cantina da UA");
        redemptionResponse.setBenefitProvider("Universidade de Aveiro");
        redemptionResponse.setPointsSpent(100);
        redemptionResponse.setStatus(RedemptionStatus.COMPLETED);
        redemptionResponse.setRedeemedAt(LocalDateTime.of(2024, 6, 15, 10, 30, 0));
        redemptionResponse.setRemainingPoints(400);

        redemptionResponse2 = new RedemptionResponse();
        redemptionResponse2.setId(2L);
        redemptionResponse2.setVolunteerId(1L);
        redemptionResponse2.setVolunteerName("Joao Silva");
        redemptionResponse2.setVolunteerEmail("joao@example.com");
        redemptionResponse2.setBenefitId(2L);
        redemptionResponse2.setBenefitName("Desconto Cinema");
        redemptionResponse2.setBenefitDescription("20% desconto em bilhetes de cinema");
        redemptionResponse2.setBenefitProvider("Cinema NOS");
        redemptionResponse2.setPointsSpent(200);
        redemptionResponse2.setStatus(RedemptionStatus.COMPLETED);
        redemptionResponse2.setRedeemedAt(LocalDateTime.of(2024, 6, 20, 14, 0, 0));
        redemptionResponse2.setRemainingPoints(200);
    }

    @Nested
    @DisplayName("POST /api/redemptions Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should return 201 CREATED on successful redemption")
        void shouldReturn201OnSuccessfulRedemption() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenReturn(redemptionResponse);

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.volunteerName", is("Joao Silva")))
                    .andExpect(jsonPath("$.benefitName", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$.pointsSpent", is(100)))
                    .andExpect(jsonPath("$.status", is("COMPLETED")))
                    .andExpect(jsonPath("$.remainingPoints", is(400)));

            verify(redemptionService, times(1)).redeemPoints(any(RedeemPointsRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(999L, 1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when benefit not found")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 999L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Benefit not found with id: 999"));

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when insufficient points")
        void shouldReturn409WhenInsufficientPoints() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenThrow(new IllegalStateException("Insufficient points"));

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 409 when benefit not active")
        void shouldReturn409WhenBenefitNotActive() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenThrow(new IllegalStateException("Benefit is not active"));

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 400 when volunteerId is null")
        void shouldReturn400WhenVolunteerIdIsNull() throws Exception {
            String requestBody = "{\"benefitId\": 1}";

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when benefitId is null")
        void shouldReturn400WhenBenefitIdIsNull() throws Exception {
            String requestBody = "{\"volunteerId\": 1}";

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return correct JSON structure on success")
        void shouldReturnCorrectJsonStructureOnSuccess() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenReturn(redemptionResponse);

            mockMvc.perform(post("/api/redemptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.volunteerId").exists())
                    .andExpect(jsonPath("$.volunteerName").exists())
                    .andExpect(jsonPath("$.volunteerEmail").exists())
                    .andExpect(jsonPath("$.benefitId").exists())
                    .andExpect(jsonPath("$.benefitName").exists())
                    .andExpect(jsonPath("$.benefitDescription").exists())
                    .andExpect(jsonPath("$.benefitProvider").exists())
                    .andExpect(jsonPath("$.pointsSpent").exists())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.redeemedAt").exists())
                    .andExpect(jsonPath("$.remainingPoints").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/{id} Tests")
    class GetRedemptionByIdTests {

        @Test
        @DisplayName("Should return 200 OK when redemption found")
        void shouldReturn200WhenRedemptionFound() throws Exception {
            when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);

            mockMvc.perform(get("/api/redemptions/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.benefitName", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$.pointsSpent", is(100)));

            verify(redemptionService, times(1)).getRedemptionById(1L);
        }

        @Test
        @DisplayName("Should return 404 when redemption not found")
        void shouldReturn404WhenRedemptionNotFound() throws Exception {
            when(redemptionService.getRedemptionById(999L))
                    .thenThrow(new ResourceNotFoundException("Redemption not found with id: 999"));

            mockMvc.perform(get("/api/redemptions/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return all redemption details")
        void shouldReturnAllRedemptionDetails() throws Exception {
            when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);

            mockMvc.perform(get("/api/redemptions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.volunteerId", is(1)))
                    .andExpect(jsonPath("$.volunteerName", is("Joao Silva")))
                    .andExpect(jsonPath("$.volunteerEmail", is("joao@example.com")))
                    .andExpect(jsonPath("$.benefitId", is(1)))
                    .andExpect(jsonPath("$.benefitProvider", is("Universidade de Aveiro")))
                    .andExpect(jsonPath("$.status", is("COMPLETED")));
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/volunteer/{volunteerId} Tests")
    class GetRedemptionsByVolunteerTests {

        @Test
        @DisplayName("Should return 200 OK with redemptions list")
        void shouldReturn200WithRedemptionsList() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(1L))
                    .thenReturn(Arrays.asList(redemptionResponse, redemptionResponse2));

            mockMvc.perform(get("/api/redemptions/volunteer/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].benefitName", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$[1].benefitName", is("Desconto Cinema")));

            verify(redemptionService, times(1)).getRedemptionsByVolunteer(1L);
        }

        @Test
        @DisplayName("Should return 200 OK with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/redemptions/volunteer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/redemptions/volunteer/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/volunteer/{volunteerId}/completed Tests")
    class GetCompletedRedemptionsTests {

        @Test
        @DisplayName("Should return 200 OK with completed redemptions")
        void shouldReturn200WithCompletedRedemptions() throws Exception {
            when(redemptionService.getCompletedRedemptionsByVolunteer(1L))
                    .thenReturn(Arrays.asList(redemptionResponse));

            mockMvc.perform(get("/api/redemptions/volunteer/1/completed")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is("COMPLETED")));
        }

        @Test
        @DisplayName("Should return empty list when no completed redemptions")
        void shouldReturnEmptyListWhenNoCompletedRedemptions() throws Exception {
            when(redemptionService.getCompletedRedemptionsByVolunteer(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/redemptions/volunteer/1/completed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(redemptionService.getCompletedRedemptionsByVolunteer(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/redemptions/volunteer/999/completed"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/volunteer/{volunteerId}/total-spent Tests")
    class GetTotalPointsSpentTests {

        @Test
        @DisplayName("Should return 200 OK with total points spent")
        void shouldReturn200WithTotalPointsSpent() throws Exception {
            when(redemptionService.getTotalPointsSpent(1L)).thenReturn(300);

            mockMvc.perform(get("/api/redemptions/volunteer/1/total-spent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("300"));
        }

        @Test
        @DisplayName("Should return zero when no points spent")
        void shouldReturnZeroWhenNoPointsSpent() throws Exception {
            when(redemptionService.getTotalPointsSpent(1L)).thenReturn(0);

            mockMvc.perform(get("/api/redemptions/volunteer/1/total-spent"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(redemptionService.getTotalPointsSpent(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/redemptions/volunteer/999/total-spent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/redemptions/volunteer/{volunteerId}/count Tests")
    class GetRedemptionCountTests {

        @Test
        @DisplayName("Should return 200 OK with redemption count")
        void shouldReturn200WithRedemptionCount() throws Exception {
            when(redemptionService.getRedemptionCount(1L)).thenReturn(5L);

            mockMvc.perform(get("/api/redemptions/volunteer/1/count")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }

        @Test
        @DisplayName("Should return zero count when no redemptions")
        void shouldReturnZeroCountWhenNoRedemptions() throws Exception {
            when(redemptionService.getRedemptionCount(1L)).thenReturn(0L);

            mockMvc.perform(get("/api/redemptions/volunteer/1/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(redemptionService.getRedemptionCount(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/redemptions/volunteer/999/count"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large volunteer ID")
        void shouldHandleLargeVolunteerId() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(Long.MAX_VALUE))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/redemptions/volunteer/" + Long.MAX_VALUE))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return JSON content type for all endpoints")
        void shouldReturnJsonContentType() throws Exception {
            when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);

            mockMvc.perform(get("/api/redemptions/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should invoke service method for redeemPoints")
        void shouldInvokeServiceForRedeemPoints() throws Exception {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);
            when(redemptionService.redeemPoints(any(RedeemPointsRequest.class)))
                    .thenReturn(redemptionResponse);

            mockMvc.perform(post("/api/redemptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            verify(redemptionService, times(1)).redeemPoints(any(RedeemPointsRequest.class));
        }

        @Test
        @DisplayName("Should invoke service method for getRedemptionById")
        void shouldInvokeServiceForGetRedemptionById() throws Exception {
            when(redemptionService.getRedemptionById(1L)).thenReturn(redemptionResponse);

            mockMvc.perform(get("/api/redemptions/1"));

            verify(redemptionService, times(1)).getRedemptionById(1L);
        }

        @Test
        @DisplayName("Should invoke service method for getRedemptionsByVolunteer")
        void shouldInvokeServiceForGetRedemptionsByVolunteer() throws Exception {
            when(redemptionService.getRedemptionsByVolunteer(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/redemptions/volunteer/1"));

            verify(redemptionService, times(1)).getRedemptionsByVolunteer(1L);
        }
    }
}
