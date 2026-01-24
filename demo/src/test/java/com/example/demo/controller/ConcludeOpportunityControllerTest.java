package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.OpportunityStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.OpportunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
class ConcludeOpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpportunityService opportunityService;

    private ConcludeOpportunityResponse concludeResponse;
    private OpportunityResponse opportunityResponse;
    private ApplicationResponse applicationResponse;

    @BeforeEach
    void setUp() {
        ConcludeOpportunityResponse.ParticipantSummary participant =
            new ConcludeOpportunityResponse.ParticipantSummary(1L, "Test Volunteer", "volunteer@test.com", 100, 100);

        concludeResponse = new ConcludeOpportunityResponse();
        concludeResponse.setOpportunityId(1L);
        concludeResponse.setOpportunityTitle("Test Opportunity");
        concludeResponse.setStatus(OpportunityStatus.CONCLUDED);
        concludeResponse.setConcludedAt(LocalDateTime.now());
        concludeResponse.setTotalParticipantsConfirmed(1);
        concludeResponse.setTotalPointsAwarded(100);
        concludeResponse.setConfirmedParticipants(Arrays.asList(participant));

        opportunityResponse = new OpportunityResponse();
        opportunityResponse.setId(1L);
        opportunityResponse.setTitle("Test Opportunity");
        opportunityResponse.setDescription("Description");
        opportunityResponse.setSkills("Java");
        opportunityResponse.setCategory("Tech");
        opportunityResponse.setDuration(10);
        opportunityResponse.setVacancies(5);
        opportunityResponse.setPoints(100);
        opportunityResponse.setPromoterId(1L);
        opportunityResponse.setPromoterName("Test Promoter");
        opportunityResponse.setStatus(OpportunityStatus.OPEN);
        opportunityResponse.setCreatedAt(LocalDateTime.now());

        applicationResponse = new ApplicationResponse();
        applicationResponse.setId(1L);
        applicationResponse.setVolunteerId(1L);
        applicationResponse.setVolunteerName("Test Volunteer");
        applicationResponse.setVolunteerEmail("volunteer@test.com");
        applicationResponse.setOpportunityId(1L);
        applicationResponse.setOpportunityTitle("Test Opportunity");
        applicationResponse.setStatus(ApplicationStatus.ACCEPTED);
        applicationResponse.setParticipationConfirmed(false);
        applicationResponse.setPointsAwarded(0);
        applicationResponse.setAppliedAt(LocalDateTime.now());
    }

    @Test
    void whenConcludeOpportunity_thenReturnSuccess() throws Exception {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
            .thenReturn(concludeResponse);

        mockMvc.perform(post("/api/opportunities/1/conclude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.opportunityId").value(1))
            .andExpect(jsonPath("$.status").value("CONCLUDED"))
            .andExpect(jsonPath("$.totalParticipantsConfirmed").value(1))
            .andExpect(jsonPath("$.totalPointsAwarded").value(100));
    }

    @Test
    void whenConcludeOpportunityNotFound_thenReturn404() throws Exception {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityService.concludeOpportunity(eq(999L), any(ConfirmParticipationRequest.class)))
            .thenThrow(new ResourceNotFoundException("Opportunity not found"));

        mockMvc.perform(post("/api/opportunities/999/conclude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenConfirmParticipation_thenReturnSuccess() throws Exception {
        applicationResponse.setParticipationConfirmed(true);
        applicationResponse.setPointsAwarded(100);

        when(opportunityService.confirmParticipation(1L, 1L))
            .thenReturn(applicationResponse);

        mockMvc.perform(post("/api/opportunities/applications/1/confirm-participation")
                .param("promoterId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.participationConfirmed").value(true))
            .andExpect(jsonPath("$.pointsAwarded").value(100));
    }

    @Test
    void whenConfirmParticipationNotFound_thenReturn404() throws Exception {
        when(opportunityService.confirmParticipation(999L, 1L))
            .thenThrow(new ResourceNotFoundException("Application not found"));

        mockMvc.perform(post("/api/opportunities/applications/999/confirm-participation")
                .param("promoterId", "1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOpportunitiesByStatusOpen_thenReturnList() throws Exception {
        when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(opportunityResponse));

        mockMvc.perform(get("/api/opportunities/status/OPEN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void whenGetOpportunitiesByStatusConcluded_thenReturnList() throws Exception {
        opportunityResponse.setStatus(OpportunityStatus.CONCLUDED);
        when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED))
            .thenReturn(Arrays.asList(opportunityResponse));

        mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("CONCLUDED"));
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatus_thenReturnList() throws Exception {
        when(opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(opportunityResponse));

        mockMvc.perform(get("/api/opportunities/promoter/1/status/OPEN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].promoterId").value(1));
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatusNotFound_thenReturn404() throws Exception {
        when(opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.OPEN))
            .thenThrow(new ResourceNotFoundException("Promoter not found"));

        mockMvc.perform(get("/api/opportunities/promoter/999/status/OPEN"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAcceptedApplicationsForOpportunity_thenReturnList() throws Exception {
        when(opportunityService.getAcceptedApplicationsForOpportunity(1L))
            .thenReturn(Arrays.asList(applicationResponse));

        mockMvc.perform(get("/api/opportunities/1/accepted-applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    @Test
    void whenGetAcceptedApplicationsForOpportunityNotFound_thenReturn404() throws Exception {
        when(opportunityService.getAcceptedApplicationsForOpportunity(999L))
            .thenThrow(new ResourceNotFoundException("Opportunity not found"));

        mockMvc.perform(get("/api/opportunities/999/accepted-applications"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenCountConcludedOpportunitiesByPromoter_thenReturnCount() throws Exception {
        when(opportunityService.countConcludedOpportunitiesByPromoter(1L))
            .thenReturn(5L);

        mockMvc.perform(get("/api/opportunities/promoter/1/concluded-count"))
            .andExpect(status().isOk())
            .andExpect(content().string("5"));
    }

    @Test
    void whenCountConcludedOpportunitiesByPromoterNotFound_thenReturn404() throws Exception {
        when(opportunityService.countConcludedOpportunitiesByPromoter(999L))
            .thenThrow(new ResourceNotFoundException("Promoter not found"));

        mockMvc.perform(get("/api/opportunities/promoter/999/concluded-count"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenConcludeOpportunityWithEmptyApplicationIds_thenReturnSuccess() throws Exception {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.emptyList());

        concludeResponse.setTotalParticipantsConfirmed(0);
        concludeResponse.setTotalPointsAwarded(0);
        concludeResponse.setConfirmedParticipants(Collections.emptyList());

        when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
            .thenReturn(concludeResponse);

        mockMvc.perform(post("/api/opportunities/1/conclude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalParticipantsConfirmed").value(0));
    }

    @Test
    void whenConcludeOpportunityWithMultipleApplications_thenReturnSuccess() throws Exception {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L, 2L, 3L));

        concludeResponse.setTotalParticipantsConfirmed(3);
        concludeResponse.setTotalPointsAwarded(300);

        when(opportunityService.concludeOpportunity(eq(1L), any(ConfirmParticipationRequest.class)))
            .thenReturn(concludeResponse);

        mockMvc.perform(post("/api/opportunities/1/conclude")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalParticipantsConfirmed").value(3))
            .andExpect(jsonPath("$.totalPointsAwarded").value(300));
    }

    @Test
    void whenGetOpportunitiesByStatusEmpty_thenReturnEmptyList() throws Exception {
        when(opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/opportunities/status/CONCLUDED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenConcludeOpportunityInvalidRequest_thenReturn400() throws Exception {
        mockMvc.perform(post("/api/opportunities/1/conclude")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
