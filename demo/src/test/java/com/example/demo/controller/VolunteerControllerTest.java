package com.example.demo.controller;

import com.example.demo.dto.VolunteerResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    private VolunteerResponse testVolunteer;
    private VolunteerResponse secondVolunteer;

    @BeforeEach
    void setUp() {
        testVolunteer = new VolunteerResponse(1L, "Test Volunteer", "test@test.com", "123456789", "Java, Python");
        secondVolunteer = new VolunteerResponse(2L, "Second Volunteer", "second@test.com", "987654321", "JavaScript");
    }

    // Feature: Ver Candidaturas Voluntario - Get all volunteers
    @Test
    void whenGetAllVolunteers_thenReturnList() throws Exception {
        List<VolunteerResponse> volunteers = Arrays.asList(testVolunteer, secondVolunteer);
        when(volunteerService.getAllVolunteers()).thenReturn(volunteers);

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test Volunteer"))
                .andExpect(jsonPath("$[1].name").value("Second Volunteer"));

        verify(volunteerService, times(1)).getAllVolunteers();
    }

    @Test
    void whenGetAllVolunteersEmpty_thenReturnEmptyList() throws Exception {
        when(volunteerService.getAllVolunteers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(volunteerService, times(1)).getAllVolunteers();
    }

    // Feature: Ver Candidaturas Voluntario - Get volunteer by ID
    @Test
    void whenGetVolunteerById_thenReturnVolunteer() throws Exception {
        when(volunteerService.getVolunteerById(1L)).thenReturn(testVolunteer);

        mockMvc.perform(get("/api/volunteers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Volunteer"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.skills").value("Java, Python"));

        verify(volunteerService, times(1)).getVolunteerById(1L);
    }

    @Test
    void whenGetVolunteerByIdNotFound_thenReturn404() throws Exception {
        when(volunteerService.getVolunteerById(999L))
                .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

        mockMvc.perform(get("/api/volunteers/999"))
                .andExpect(status().isNotFound());

        verify(volunteerService, times(1)).getVolunteerById(999L);
    }

    // Feature: Ver Candidaturas Voluntario - Get volunteer by email
    @Test
    void whenGetVolunteerByEmail_thenReturnVolunteer() throws Exception {
        when(volunteerService.getVolunteerByEmail("test@test.com")).thenReturn(testVolunteer);

        mockMvc.perform(get("/api/volunteers/email/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("Test Volunteer"));

        verify(volunteerService, times(1)).getVolunteerByEmail("test@test.com");
    }

    @Test
    void whenGetVolunteerByEmailNotFound_thenReturn404() throws Exception {
        when(volunteerService.getVolunteerByEmail("notfound@test.com"))
                .thenThrow(new ResourceNotFoundException("Volunteer not found with email: notfound@test.com"));

        mockMvc.perform(get("/api/volunteers/email/notfound@test.com"))
                .andExpect(status().isNotFound());

        verify(volunteerService, times(1)).getVolunteerByEmail("notfound@test.com");
    }

    // Feature: Ver Candidaturas Voluntario - Check if volunteer exists
    @Test
    void whenExistsByEmail_thenReturnTrue() throws Exception {
        when(volunteerService.existsByEmail("test@test.com")).thenReturn(true);

        mockMvc.perform(get("/api/volunteers/exists/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(volunteerService, times(1)).existsByEmail("test@test.com");
    }

    @Test
    void whenExistsByEmailNotFound_thenReturnFalse() throws Exception {
        when(volunteerService.existsByEmail("notfound@test.com")).thenReturn(false);

        mockMvc.perform(get("/api/volunteers/exists/notfound@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(volunteerService, times(1)).existsByEmail("notfound@test.com");
    }

    // Additional tests for complete endpoint coverage
    @Test
    void whenGetVolunteerByIdWithNullOptionalFields_thenReturnVolunteerWithNulls() throws Exception {
        VolunteerResponse volunteerWithNulls = new VolunteerResponse(3L, "Minimal", "minimal@test.com", null, null);
        when(volunteerService.getVolunteerById(3L)).thenReturn(volunteerWithNulls);

        mockMvc.perform(get("/api/volunteers/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Minimal"))
                .andExpect(jsonPath("$.email").value("minimal@test.com"))
                .andExpect(jsonPath("$.phone").isEmpty())
                .andExpect(jsonPath("$.skills").isEmpty());
    }

    @Test
    void whenGetAllVolunteersWithSingleVolunteer_thenReturnSingleElementList() throws Exception {
        when(volunteerService.getAllVolunteers()).thenReturn(Collections.singletonList(testVolunteer));

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Volunteer"));
    }
}
