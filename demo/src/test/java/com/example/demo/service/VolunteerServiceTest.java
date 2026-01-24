package com.example.demo.service;

import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer testVolunteer;

    @BeforeEach
    void setUp() {
        testVolunteer = new Volunteer();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");
        testVolunteer.setPhone("123456789");
        testVolunteer.setSkills("Java, Python");
        testVolunteer.setTotalPoints(100);
    }

    @Test
    void whenGetVolunteerById_thenReturnVolunteer() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
    }

    @Test
    void whenGetVolunteerByIdNotFound_thenThrowException() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            volunteerService.getVolunteerById(999L));
    }

    @Test
    void whenGetVolunteerByEmail_thenReturnVolunteer() {
        when(volunteerRepository.findByEmail("volunteer@test.com")).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerByEmail("volunteer@test.com");

        assertNotNull(response);
        assertEquals("volunteer@test.com", response.getEmail());
    }

    @Test
    void whenGetAllVolunteers_thenReturnList() {
        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(testVolunteer));

        List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        when(volunteerRepository.existsByEmail("volunteer@test.com")).thenReturn(true);

        boolean exists = volunteerService.existsByEmail("volunteer@test.com");

        assertTrue(exists);
    }
}
