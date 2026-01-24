package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
class VolunteerPointsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    private VolunteerPointsResponse pointsResponse;
    private ApplicationResponse applicationResponse;

    @BeforeEach
    void setUp() {
        pointsResponse = new VolunteerPointsResponse();
        pointsResponse.setId(1L);
        pointsResponse.setName("Test Volunteer");
        pointsResponse.setEmail("volunteer@test.com");
        pointsResponse.setTotalPoints(100);

        applicationResponse = new ApplicationResponse();
        applicationResponse.setId(1L);
        applicationResponse.setVolunteerId(1L);
        applicationResponse.setVolunteerName("Test Volunteer");
        applicationResponse.setVolunteerEmail("volunteer@test.com");
        applicationResponse.setOpportunityId(1L);
        applicationResponse.setOpportunityTitle("Test Opportunity");
        applicationResponse.setStatus(ApplicationStatus.ACCEPTED);
        applicationResponse.setParticipationConfirmed(true);
        applicationResponse.setPointsAwarded(100);
        applicationResponse.setAppliedAt(LocalDateTime.now());
    }

    @Test
    void whenGetVolunteerPoints_thenReturnPoints() throws Exception {
        when(volunteerService.getVolunteerPoints(1L)).thenReturn(pointsResponse);

        mockMvc.perform(get("/api/volunteers/1/points"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Volunteer"))
            .andExpect(jsonPath("$.totalPoints").value(100));
    }

    @Test
    void whenGetVolunteerPointsNotFound_thenReturn404() throws Exception {
        when(volunteerService.getVolunteerPoints(999L))
            .thenThrow(new ResourceNotFoundException("Volunteer not found"));

        mockMvc.perform(get("/api/volunteers/999/points"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGetVolunteersRanking_thenReturnSortedList() throws Exception {
        VolunteerPointsResponse second = new VolunteerPointsResponse(2L, "Second", "second@test.com", 200);
        VolunteerPointsResponse third = new VolunteerPointsResponse(3L, "Third", "third@test.com", 50);

        when(volunteerService.getVolunteersRanking())
            .thenReturn(Arrays.asList(second, pointsResponse, third));

        mockMvc.perform(get("/api/volunteers/ranking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].totalPoints").value(200))
            .andExpect(jsonPath("$[1].totalPoints").value(100))
            .andExpect(jsonPath("$[2].totalPoints").value(50));
    }

    @Test
    void whenGetVolunteersRankingEmpty_thenReturnEmptyList() throws Exception {
        when(volunteerService.getVolunteersRanking()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/volunteers/ranking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetTopVolunteers_thenReturnLimitedList() throws Exception {
        VolunteerPointsResponse second = new VolunteerPointsResponse(2L, "Second", "second@test.com", 200);

        when(volunteerService.getTopVolunteers(2))
            .thenReturn(Arrays.asList(second, pointsResponse));

        mockMvc.perform(get("/api/volunteers/top/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].totalPoints").value(200))
            .andExpect(jsonPath("$[1].totalPoints").value(100));
    }

    @Test
    void whenGetTopVolunteersZero_thenReturnEmptyList() throws Exception {
        when(volunteerService.getTopVolunteers(0)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/volunteers/top/0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetConfirmedParticipations_thenReturnList() throws Exception {
        when(volunteerService.getConfirmedParticipations(1L))
            .thenReturn(Arrays.asList(applicationResponse));

        mockMvc.perform(get("/api/volunteers/1/confirmed-participations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].participationConfirmed").value(true))
            .andExpect(jsonPath("$[0].pointsAwarded").value(100));
    }

    @Test
    void whenGetConfirmedParticipationsNotFound_thenReturn404() throws Exception {
        when(volunteerService.getConfirmedParticipations(999L))
            .thenThrow(new ResourceNotFoundException("Volunteer not found"));

        mockMvc.perform(get("/api/volunteers/999/confirmed-participations"))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGetConfirmedParticipationsEmpty_thenReturnEmptyList() throws Exception {
        when(volunteerService.getConfirmedParticipations(1L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/volunteers/1/confirmed-participations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetVolunteerPointsWithZeroPoints_thenReturnZero() throws Exception {
        pointsResponse.setTotalPoints(0);
        when(volunteerService.getVolunteerPoints(1L)).thenReturn(pointsResponse);

        mockMvc.perform(get("/api/volunteers/1/points"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPoints").value(0));
    }

    @Test
    void whenGetTopVolunteersLargeLimit_thenReturnAvailable() throws Exception {
        when(volunteerService.getTopVolunteers(100))
            .thenReturn(Arrays.asList(pointsResponse));

        mockMvc.perform(get("/api/volunteers/top/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
