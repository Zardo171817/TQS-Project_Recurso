package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
@DisplayName("Volunteer Feature Controller Tests")
class VolunteerFeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    @Test
    @DisplayName("Should return volunteer points successfully")
    void getVolunteerPoints_Success_ReturnsOk() throws Exception {
        VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "John Doe", "john@test.com", 250);

        when(volunteerService.getVolunteerPoints(1L)).thenReturn(response);

        mockMvc.perform(get("/api/volunteers/1/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.totalPoints").value(250));
    }

    @Test
    @DisplayName("Should return volunteers ranking sorted by points")
    void getVolunteersRanking_Success_ReturnsOk() throws Exception {
        VolunteerPointsResponse v1 = new VolunteerPointsResponse(2L, "Jane", "jane@test.com", 500);
        VolunteerPointsResponse v2 = new VolunteerPointsResponse(1L, "John", "john@test.com", 200);

        when(volunteerService.getVolunteersRanking()).thenReturn(Arrays.asList(v1, v2));

        mockMvc.perform(get("/api/volunteers/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].totalPoints").value(500))
                .andExpect(jsonPath("$[1].totalPoints").value(200));
    }

    @Test
    @DisplayName("Should return top volunteers limited by count")
    void getTopVolunteers_Success_ReturnsOk() throws Exception {
        VolunteerPointsResponse v1 = new VolunteerPointsResponse(2L, "Jane", "jane@test.com", 500);

        when(volunteerService.getTopVolunteers(1)).thenReturn(Collections.singletonList(v1));

        mockMvc.perform(get("/api/volunteers/top/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].totalPoints").value(500));
    }

    @Test
    @DisplayName("Should return confirmed participations for volunteer")
    void getConfirmedParticipations_Success_ReturnsOk() throws Exception {
        ApplicationResponse app = new ApplicationResponse();
        app.setId(1L);
        app.setParticipationConfirmed(true);
        app.setPointsAwarded(100);
        app.setStatus(ApplicationStatus.ACCEPTED);

        when(volunteerService.getConfirmedParticipations(1L)).thenReturn(Collections.singletonList(app));

        mockMvc.perform(get("/api/volunteers/1/confirmed-participations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].participationConfirmed").value(true))
                .andExpect(jsonPath("$[0].pointsAwarded").value(100));
    }
}
