package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.OpportunityStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.OpportunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(OpportunityController.class)
@DisplayName("Conclude Opportunity Controller Tests")
class ConcludeOpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpportunityService opportunityService;

    private ConcludeOpportunityResponse successResponse;
    private ConfirmParticipationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new ConfirmParticipationRequest();
        validRequest.setPromoterId(1L);
        validRequest.setApplicationIds(Arrays.asList(1L, 2L));

        ConcludeOpportunityResponse.ParticipantSummary participant = new ConcludeOpportunityResponse.ParticipantSummary();
        participant.setVolunteerId(1L);
        participant.setVolunteerName("John Doe");
        participant.setVolunteerEmail("john@example.com");
        participant.setPointsAwarded(100);
        participant.setTotalPoints(100);

        successResponse = new ConcludeOpportunityResponse();
        successResponse.setOpportunityId(1L);
        successResponse.setOpportunityTitle("Beach Cleanup");
        successResponse.setStatus(OpportunityStatus.CONCLUDED);
        successResponse.setConcludedAt(LocalDateTime.now());
        successResponse.setTotalParticipantsConfirmed(1);
        successResponse.setTotalPointsAwarded(100);
        successResponse.setConfirmedParticipants(Collections.singletonList(participant));
    }

    @Nested
    @DisplayName("POST /api/opportunities/{id}/conclude")
    class ConcludeOpportunityEndpointTests {

        @Test
        @DisplayName("Should conclude opportunity with single participant successfully")
        void concludeOpportunity_WithSingleParticipant_ReturnsOk() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.opportunityId").value(1))
                    .andExpect(jsonPath("$.status").value("CONCLUDED"))
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(1));
        }

        @Test
        @DisplayName("Should conclude opportunity with multiple participants successfully")
        void concludeOpportunity_WithMultipleParticipants_ReturnsOk() throws Exception {
            ConcludeOpportunityResponse.ParticipantSummary p1 = new ConcludeOpportunityResponse.ParticipantSummary(1L, "John", "john@test.com", 100, 100);
            ConcludeOpportunityResponse.ParticipantSummary p2 = new ConcludeOpportunityResponse.ParticipantSummary(2L, "Jane", "jane@test.com", 100, 200);
            successResponse.setConfirmedParticipants(Arrays.asList(p1, p2));
            successResponse.setTotalParticipantsConfirmed(2);
            successResponse.setTotalPointsAwarded(200);

            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(2))
                    .andExpect(jsonPath("$.totalPointsAwarded").value(200))
                    .andExpect(jsonPath("$.confirmedParticipants", hasSize(2)));
        }

        @Test
        @DisplayName("Should conclude opportunity with empty participant list")
        void concludeOpportunity_WithEmptyList_ReturnsOk() throws Exception {
            validRequest.setApplicationIds(Collections.emptyList());
            successResponse.setConfirmedParticipants(Collections.emptyList());
            successResponse.setTotalParticipantsConfirmed(0);
            successResponse.setTotalPointsAwarded(0);

            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalParticipantsConfirmed").value(0));
        }

        @Test
        @DisplayName("Should return 404 when opportunity not found")
        void concludeOpportunity_OpportunityNotFound_Returns404() throws Exception {
            when(opportunityService.concludeOpportunity(eq(999L), any(ConfirmParticipationRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Opportunity not found with id: 999"));

            mockMvc.perform(post("/api/opportunities/999/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when promoter ID is null")
        void concludeOpportunity_NullPromoterId_Returns400() throws Exception {
            validRequest.setPromoterId(null);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when application IDs is null")
        void concludeOpportunity_NullApplicationIds_Returns400() throws Exception {
            validRequest.setApplicationIds(null);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when wrong promoter tries to conclude")
        void concludeOpportunity_WrongPromoter_ReturnsConflict() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenThrow(new IllegalStateException("Only the promoter who created this opportunity can conclude it"));

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 409 when opportunity already concluded")
        void concludeOpportunity_AlreadyConcluded_ReturnsConflict() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenThrow(new IllegalStateException("Opportunity is already concluded"));

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should verify response contains concluded timestamp")
        void concludeOpportunity_Success_ContainsConcludedAt() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.concludedAt").exists());
        }

        @Test
        @DisplayName("Should verify participant details in response")
        void concludeOpportunity_Success_ContainsParticipantDetails() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmedParticipants[0].volunteerId").value(1))
                    .andExpect(jsonPath("$.confirmedParticipants[0].volunteerName").value("John Doe"))
                    .andExpect(jsonPath("$.confirmedParticipants[0].volunteerEmail").value("john@example.com"))
                    .andExpect(jsonPath("$.confirmedParticipants[0].pointsAwarded").value(100));
        }

        @Test
        @DisplayName("Should verify service is called exactly once")
        void concludeOpportunity_ServiceCalledOnce() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk());

            verify(opportunityService, times(1)).concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class));
        }

        @Test
        @DisplayName("Should return correct opportunity title in response")
        void concludeOpportunity_ResponseContainsTitle() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.opportunityTitle").value("Beach Cleanup"));
        }

        @Test
        @DisplayName("Should return 409 when application not found during conclude")
        void concludeOpportunity_ApplicationNotFound_ReturnsNotFound() throws Exception {
            when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Application not found with id: 999"));

            mockMvc.perform(post("/api/opportunities/1/conclude")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/opportunities/applications/{id}/confirm-participation")
    class ConfirmParticipationEndpointTests {

        @Test
        @DisplayName("Should confirm individual participation successfully")
        void confirmParticipation_Success_ReturnsOk() throws Exception {
            ApplicationResponse response = new ApplicationResponse();
            response.setId(1L);
            response.setParticipationConfirmed(true);
            response.setPointsAwarded(100);

            when(opportunityService.confirmParticipation(1L, 1L)).thenReturn(response);

            mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                            .param("promoterId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.participationConfirmed").value(true))
                    .andExpect(jsonPath("$.pointsAwarded").value(100));
        }

        @Test
        @DisplayName("Should return 404 when application not found")
        void confirmParticipation_ApplicationNotFound_Returns404() throws Exception {
            when(opportunityService.confirmParticipation(999L, 1L))
                    .thenThrow(new ResourceNotFoundException("Application not found with id: 999"));

            mockMvc.perform(post("/api/opportunities/applications/999/confirm-participation")
                            .param("promoterId", "1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when wrong promoter confirms")
        void confirmParticipation_WrongPromoter_ReturnsConflict() throws Exception {
            when(opportunityService.confirmParticipation(1L, 2L))
                    .thenThrow(new IllegalStateException("Only the promoter who created this opportunity can confirm participation"));

            mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                            .param("promoterId", "2"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 409 when application not accepted")
        void confirmParticipation_NotAccepted_ReturnsConflict() throws Exception {
            when(opportunityService.confirmParticipation(1L, 1L))
                    .thenThrow(new IllegalStateException("Only accepted applications can have participation confirmed"));

            mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                            .param("promoterId", "1"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 409 when already confirmed")
        void confirmParticipation_AlreadyConfirmed_ReturnsConflict() throws Exception {
            when(opportunityService.confirmParticipation(1L, 1L))
                    .thenThrow(new IllegalStateException("Participation is already confirmed for this application"));

            mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                            .param("promoterId", "1"))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should verify service called with correct parameters")
        void confirmParticipation_ServiceCalledWithCorrectParams() throws Exception {
            ApplicationResponse response = new ApplicationResponse();
            response.setId(1L);
            response.setParticipationConfirmed(true);

            when(opportunityService.confirmParticipation(5L, 3L)).thenReturn(response);

            mockMvc.perform(post("/api/opportunities/applications/5/confirm-participation")
                            .param("promoterId", "3"))
                    .andExpect(status().isOk());

            verify(opportunityService).confirmParticipation(5L, 3L);
        }

        @Test
        @DisplayName("Should return confirmedAt in response")
        void confirmParticipation_ResponseContainsConfirmedAt() throws Exception {
            ApplicationResponse response = new ApplicationResponse();
            response.setId(1L);
            response.setParticipationConfirmed(true);
            response.setPointsAwarded(100);
            response.setConfirmedAt(LocalDateTime.now());

            when(opportunityService.confirmParticipation(1L, 1L)).thenReturn(response);

            mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                            .param("promoterId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmedAt").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/opportunities/status/{status}")
    class GetOpportunitiesByStatusTests {

        @Test
        @DisplayName("Should return concluded opportunities")
        void getOpportunitiesByStatus_Concluded_ReturnsOk() throws Exception {
            OpportunityResponse opp = new OpportunityResponse();
            opp.setId(1L);
            opp.setStatus(OpportunityStatus.CONCLUDED);

            when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.singletonList(opp));

            mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("CONCLUDED"));
        }

        @Test
        @DisplayName("Should return open opportunities")
        void getOpportunitiesByStatus_Open_ReturnsOk() throws Exception {
            OpportunityResponse opp = new OpportunityResponse();
            opp.setId(1L);
            opp.setStatus(OpportunityStatus.OPEN);

            when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN))
                    .thenReturn(Collections.singletonList(opp));

            mockMvc.perform(get("/api/opportunities/status/OPEN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("OPEN"));
        }

        @Test
        @DisplayName("Should return empty list when no opportunities with status")
        void getOpportunitiesByStatus_NoResults_ReturnsEmptyList() throws Exception {
            when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return multiple opportunities with same status")
        void getOpportunitiesByStatus_Multiple_ReturnsAll() throws Exception {
            OpportunityResponse opp1 = new OpportunityResponse();
            opp1.setId(1L);
            opp1.setStatus(OpportunityStatus.OPEN);
            OpportunityResponse opp2 = new OpportunityResponse();
            opp2.setId(2L);
            opp2.setStatus(OpportunityStatus.OPEN);

            when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN))
                    .thenReturn(Arrays.asList(opp1, opp2));

            mockMvc.perform(get("/api/opportunities/status/OPEN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/opportunities/promoter/{promoterId}/status/{status}")
    class GetOpportunitiesByPromoterAndStatusTests {

        @Test
        @DisplayName("Should return concluded opportunities for promoter")
        void getByPromoterAndStatus_Success_ReturnsOk() throws Exception {
            OpportunityResponse opp = new OpportunityResponse();
            opp.setId(1L);
            opp.setStatus(OpportunityStatus.CONCLUDED);

            when(opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.singletonList(opp));

            mockMvc.perform(get("/api/opportunities/promoter/1/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return 404 when promoter not found")
        void getByPromoterAndStatus_PromoterNotFound_Returns404() throws Exception {
            when(opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.CONCLUDED))
                    .thenThrow(new ResourceNotFoundException("Promoter not found with id: 999"));

            mockMvc.perform(get("/api/opportunities/promoter/999/status/CONCLUDED"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty list when promoter has no opportunities with status")
        void getByPromoterAndStatus_NoResults_ReturnsEmptyList() throws Exception {
            when(opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/opportunities/promoter/1/status/CONCLUDED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/opportunities/{id}/accepted-applications")
    class GetAcceptedApplicationsTests {

        @Test
        @DisplayName("Should return accepted applications for opportunity")
        void getAcceptedApplications_Success_ReturnsOk() throws Exception {
            ApplicationResponse app = new ApplicationResponse();
            app.setId(1L);
            app.setStatus(ApplicationStatus.ACCEPTED);

            when(opportunityService.getAcceptedApplicationsForOpportunity(1L))
                    .thenReturn(Collections.singletonList(app));

            mockMvc.perform(get("/api/opportunities/1/accepted-applications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
        }

        @Test
        @DisplayName("Should return 404 when opportunity not found")
        void getAcceptedApplications_NotFound_Returns404() throws Exception {
            when(opportunityService.getAcceptedApplicationsForOpportunity(999L))
                    .thenThrow(new ResourceNotFoundException("Opportunity not found with id: 999"));

            mockMvc.perform(get("/api/opportunities/999/accepted-applications"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty list when no accepted applications")
        void getAcceptedApplications_NoResults_ReturnsEmptyList() throws Exception {
            when(opportunityService.getAcceptedApplicationsForOpportunity(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/opportunities/1/accepted-applications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return multiple accepted applications")
        void getAcceptedApplications_Multiple_ReturnsAll() throws Exception {
            ApplicationResponse app1 = new ApplicationResponse();
            app1.setId(1L);
            app1.setStatus(ApplicationStatus.ACCEPTED);
            ApplicationResponse app2 = new ApplicationResponse();
            app2.setId(2L);
            app2.setStatus(ApplicationStatus.ACCEPTED);

            when(opportunityService.getAcceptedApplicationsForOpportunity(1L))
                    .thenReturn(Arrays.asList(app1, app2));

            mockMvc.perform(get("/api/opportunities/1/accepted-applications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/opportunities/promoter/{promoterId}/concluded-count")
    class CountConcludedOpportunitiesTests {

        @Test
        @DisplayName("Should return count of concluded opportunities")
        void countConcluded_Success_ReturnsCount() throws Exception {
            when(opportunityService.countConcludedOpportunitiesByPromoter(1L)).thenReturn(5L);

            mockMvc.perform(get("/api/opportunities/promoter/1/concluded-count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }

        @Test
        @DisplayName("Should return zero when no concluded opportunities")
        void countConcluded_NoConcluded_ReturnsZero() throws Exception {
            when(opportunityService.countConcludedOpportunitiesByPromoter(1L)).thenReturn(0L);

            mockMvc.perform(get("/api/opportunities/promoter/1/concluded-count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("Should return 404 when promoter not found")
        void countConcluded_PromoterNotFound_Returns404() throws Exception {
            when(opportunityService.countConcludedOpportunitiesByPromoter(999L))
                    .thenThrow(new ResourceNotFoundException("Promoter not found with id: 999"));

            mockMvc.perform(get("/api/opportunities/promoter/999/concluded-count"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return high count of concluded opportunities")
        void countConcluded_HighCount_ReturnsCount() throws Exception {
            when(opportunityService.countConcludedOpportunitiesByPromoter(1L)).thenReturn(150L);

            mockMvc.perform(get("/api/opportunities/promoter/1/concluded-count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("150"));
        }
    }
}
