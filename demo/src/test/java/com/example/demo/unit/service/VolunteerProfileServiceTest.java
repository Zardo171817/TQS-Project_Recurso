package com.example.demo.unit.service;

import com.example.demo.dto.CreateVolunteerProfileRequest;
import com.example.demo.dto.VolunteerProfileResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.VolunteerProfileService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolunteerProfileService Unit Tests")
class VolunteerProfileServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private VolunteerProfileService volunteerProfileService;

    private Volunteer volunteer;
    private CreateVolunteerProfileRequest createRequest;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");
        volunteer.setSkills("Java, Python");
        volunteer.setInterests("Environment, Education");
        volunteer.setTotalPoints(100);

        createRequest = new CreateVolunteerProfileRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setSkills("Java, Python");
        createRequest.setInterests("Environment");
    }

    @Test
    @DisplayName("Should create volunteer profile")
    void shouldCreateVolunteerProfile() {
        when(volunteerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        VolunteerProfileResponse response = volunteerProfileService.createProfile(createRequest);

        assertThat(response.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void shouldFailWhenEmailExists() {
        when(volunteerRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> volunteerProfileService.createProfile(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should get profile by ID")
    void shouldGetProfileById() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

        VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existent profile")
    void shouldThrowExceptionForNonExistent() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> volunteerProfileService.getProfile(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should search by skills")
    void shouldSearchBySkills() {
        when(volunteerRepository.findBySkillsContainingIgnoreCase("Java"))
                .thenReturn(Arrays.asList(volunteer));

        List<VolunteerProfileResponse> responses = volunteerProfileService.findVolunteersBySkills("Java");

        assertThat(responses).hasSize(1);
    }
}
