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

    @BeforeEach
    void setUp() {
        testVolunteer = new VolunteerResponse();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("test@test.com");
        testVolunteer.setPhone("123456789");
        testVolunteer.setSkills("Java, Python");
        testVolunteer.setTotalPoints(100);
    }

    @Test
    void whenGetAllVolunteers_thenReturnList() throws Exception {
        when(volunteerService.getAllVolunteers()).thenReturn(Arrays.asList(testVolunteer));

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenGetVolunteerById_thenReturnVolunteer() throws Exception {
        when(volunteerService.getVolunteerById(1L)).thenReturn(testVolunteer);

        mockMvc.perform(get("/api/volunteers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPoints").value(100));
    }

    @Test
    void whenGetVolunteerByIdNotFound_thenReturn404() throws Exception {
        when(volunteerService.getVolunteerById(999L))
                .thenThrow(new ResourceNotFoundException("Volunteer not found"));

        mockMvc.perform(get("/api/volunteers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() throws Exception {
        when(volunteerService.existsByEmail("test@test.com")).thenReturn(true);

        mockMvc.perform(get("/api/volunteers/exists/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
