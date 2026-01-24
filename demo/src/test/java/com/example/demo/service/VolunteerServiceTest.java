package com.example.demo.service;

import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
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

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer testVolunteer;
    private Volunteer secondVolunteer;

    @BeforeEach
    void setUp() {
        testVolunteer = new Volunteer();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");
        testVolunteer.setPhone("123456789");
        testVolunteer.setSkills("Java, Python");

        secondVolunteer = new Volunteer();
        secondVolunteer.setId(2L);
        secondVolunteer.setName("Second Volunteer");
        secondVolunteer.setEmail("second@test.com");
        secondVolunteer.setPhone("987654321");
        secondVolunteer.setSkills("JavaScript, React");
    }

    // Feature: Ver Candidaturas Voluntario - getVolunteerById tests
    @Test
    void whenGetVolunteerById_thenReturnVolunteer() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals("volunteer@test.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals("Java, Python", response.getSkills());
        verify(volunteerRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetVolunteerByIdNotFound_thenThrowException() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            volunteerService.getVolunteerById(999L));
        verify(volunteerRepository, times(1)).findById(999L);
    }

    // Feature: Ver Candidaturas Voluntario - getVolunteerByEmail tests
    @Test
    void whenGetVolunteerByEmail_thenReturnVolunteer() {
        when(volunteerRepository.findByEmail("volunteer@test.com")).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerByEmail("volunteer@test.com");

        assertNotNull(response);
        assertEquals("volunteer@test.com", response.getEmail());
        assertEquals("Test Volunteer", response.getName());
        verify(volunteerRepository, times(1)).findByEmail("volunteer@test.com");
    }

    @Test
    void whenGetVolunteerByEmailNotFound_thenThrowException() {
        when(volunteerRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            volunteerService.getVolunteerByEmail("notfound@test.com"));
        verify(volunteerRepository, times(1)).findByEmail("notfound@test.com");
    }

    // Feature: Ver Candidaturas Voluntario - getAllVolunteers tests
    @Test
    void whenGetAllVolunteers_thenReturnList() {
        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(testVolunteer, secondVolunteer));

        List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Volunteer", responses.get(0).getName());
        assertEquals("Second Volunteer", responses.get(1).getName());
        verify(volunteerRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllVolunteersEmpty_thenReturnEmptyList() {
        when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

        List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(volunteerRepository, times(1)).findAll();
    }

    // Feature: Ver Candidaturas Voluntario - existsByEmail tests
    @Test
    void whenExistsByEmail_thenReturnTrue() {
        when(volunteerRepository.existsByEmail("volunteer@test.com")).thenReturn(true);

        boolean exists = volunteerService.existsByEmail("volunteer@test.com");

        assertTrue(exists);
        verify(volunteerRepository, times(1)).existsByEmail("volunteer@test.com");
    }

    @Test
    void whenExistsByEmailNotFound_thenReturnFalse() {
        when(volunteerRepository.existsByEmail("notfound@test.com")).thenReturn(false);

        boolean exists = volunteerService.existsByEmail("notfound@test.com");

        assertFalse(exists);
        verify(volunteerRepository, times(1)).existsByEmail("notfound@test.com");
    }

    // Additional tests for better coverage
    @Test
    void whenGetVolunteerByIdWithNullPhone_thenReturnVolunteerWithNullPhone() {
        testVolunteer.setPhone(null);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerById(1L);

        assertNotNull(response);
        assertNull(response.getPhone());
    }

    @Test
    void whenGetVolunteerByIdWithNullSkills_thenReturnVolunteerWithNullSkills() {
        testVolunteer.setSkills(null);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerById(1L);

        assertNotNull(response);
        assertNull(response.getSkills());
    }

    @Test
    void whenGetVolunteerByEmailWithCompleteData_thenReturnCompleteVolunteer() {
        when(volunteerRepository.findByEmail("volunteer@test.com")).thenReturn(Optional.of(testVolunteer));

        VolunteerResponse response = volunteerService.getVolunteerByEmail("volunteer@test.com");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals("volunteer@test.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals("Java, Python", response.getSkills());
    }

    @Test
    void whenGetAllVolunteersWithOneVolunteer_thenReturnSingleElementList() {
        when(volunteerRepository.findAll()).thenReturn(Collections.singletonList(testVolunteer));

        List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Volunteer", responses.get(0).getName());
    }
}
