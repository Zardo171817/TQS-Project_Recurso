package com.example.demo.unit.service;

import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolunteerService Unit Tests")
class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer1;
    private Volunteer volunteer2;

    @BeforeEach
    void setUp() {
        volunteer1 = new Volunteer();
        volunteer1.setId(1L);
        volunteer1.setName("John Doe");
        volunteer1.setEmail("john@example.com");
        volunteer1.setTotalPoints(100);

        volunteer2 = new Volunteer();
        volunteer2.setId(2L);
        volunteer2.setName("Jane Smith");
        volunteer2.setEmail("jane@example.com");
        volunteer2.setTotalPoints(200);
    }

    @Test
    @DisplayName("Should get volunteer by ID")
    void shouldGetVolunteerById() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

        VolunteerResponse response = volunteerService.getVolunteerById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should throw exception for non-existent volunteer")
    void shouldThrowExceptionForNonExistentVolunteer() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> volunteerService.getVolunteerById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get volunteer by email")
    void shouldGetVolunteerByEmail() {
        when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer1));

        VolunteerResponse response = volunteerService.getVolunteerByEmail("john@example.com");

        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should get all volunteers")
    void shouldGetAllVolunteers() {
        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

        List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Should get volunteer points")
    void shouldGetVolunteerPoints() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

        VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

        assertThat(response.getTotalPoints()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should get volunteers ranking sorted by points")
    void shouldGetVolunteersRanking() {
        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0).getTotalPoints()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should check if volunteer exists by email")
    void shouldCheckIfVolunteerExistsByEmail() {
        when(volunteerRepository.existsByEmail("john@example.com")).thenReturn(true);
        when(volunteerRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        assertThat(volunteerService.existsByEmail("john@example.com")).isTrue();
        assertThat(volunteerService.existsByEmail("unknown@example.com")).isFalse();
    }
}
