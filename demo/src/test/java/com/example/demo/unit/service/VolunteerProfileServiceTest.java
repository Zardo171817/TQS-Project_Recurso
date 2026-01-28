package com.example.demo.unit.service;

import com.example.demo.dto.CreateVolunteerProfileRequest;
import com.example.demo.dto.UpdateVolunteerProfileRequest;
import com.example.demo.dto.VolunteerProfileResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.VolunteerProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolunteerProfileService Unit Tests")
class VolunteerProfileServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private VolunteerProfileService volunteerProfileService;

    private Volunteer volunteer;
    private CreateVolunteerProfileRequest createRequest;
    private UpdateVolunteerProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");
        volunteer.setInterests("Environment, Education");
        volunteer.setAvailability("Weekends");
        volunteer.setBio("Test bio");
        volunteer.setTotalPoints(0);

        createRequest = new CreateVolunteerProfileRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setPhone("123456789");
        createRequest.setSkills("Java, Python");
        createRequest.setInterests("Environment");
        createRequest.setAvailability("Weekends");
        createRequest.setBio("Test bio");

        updateRequest = new UpdateVolunteerProfileRequest();
    }

    @Nested
    @DisplayName("Create Profile Tests")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create profile successfully")
        void shouldCreateProfileSuccessfully() {
            when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
                Volunteer v = invocation.getArgument(0);
                v.setId(1L);
                return v;
            });

            VolunteerProfileResponse response = volunteerProfileService.createProfile(createRequest);

            assertThat(response.getName()).isEqualTo("John Doe");
            assertThat(response.getEmail()).isEqualTo("john@example.com");
            assertThat(response.getTotalPoints()).isEqualTo(0);
            verify(volunteerRepository).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Should fail when email already exists")
        void shouldFailWhenEmailExists() {
            when(volunteerRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> volunteerProfileService.createProfile(createRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already exists");

            verify(volunteerRepository, never()).save(any(Volunteer.class));
        }
    }

    @Nested
    @DisplayName("Update Profile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfileSuccessfully() {
            updateRequest.setName("Updated Name");
            updateRequest.setPhone("987654321");

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

            VolunteerProfileResponse response = volunteerProfileService.updateProfile(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(volunteerRepository).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Should fail update when no fields provided")
        void shouldFailUpdateWhenNoFieldsProvided() {
            UpdateVolunteerProfileRequest emptyRequest = new UpdateVolunteerProfileRequest();
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            assertThatThrownBy(() -> volunteerProfileService.updateProfile(1L, emptyRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No fields to update");
        }

        @Test
        @DisplayName("Should throw exception for non-existent volunteer")
        void shouldThrowExceptionForNonExistentVolunteer() {
            updateRequest.setName("Updated");
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerProfileService.updateProfile(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            updateRequest.setSkills("New Skills");
            // Other fields are null

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

            volunteerProfileService.updateProfile(1L, updateRequest);

            verify(volunteerRepository).save(argThat(v ->
                    v.getSkills().equals("New Skills") &&
                    v.getName().equals("John Doe") // unchanged
            ));
        }
    }

    @Nested
    @DisplayName("Get Profile Tests")
    class GetProfileTests {

        @Test
        @DisplayName("Should get profile by ID")
        void shouldGetProfileById() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should get profile by email")
        void shouldGetProfileByEmail() {
            when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfileByEmail("john@example.com");

            assertThat(response.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should get all profiles")
        void shouldGetAllProfiles() {
            Volunteer volunteer2 = new Volunteer();
            volunteer2.setId(2L);
            volunteer2.setName("Jane");
            volunteer2.setEmail("jane@example.com");
            volunteer2.setTotalPoints(0);

            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer, volunteer2));

            List<VolunteerProfileResponse> profiles = volunteerProfileService.getAllProfiles();

            assertThat(profiles).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Search Profile Tests")
    class SearchProfileTests {

        @Test
        @DisplayName("Should find volunteers by skills")
        void shouldFindVolunteersBySkills() {
            when(volunteerRepository.findBySkillsContainingIgnoreCase("Java"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> results = volunteerProfileService.findVolunteersBySkills("Java");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getSkills()).contains("Java");
        }

        @Test
        @DisplayName("Should find volunteers by interests")
        void shouldFindVolunteersByInterests() {
            when(volunteerRepository.findByInterestsContainingIgnoreCase("Environment"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> results = volunteerProfileService.findVolunteersByInterests("Environment");

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("Should find volunteers by availability")
        void shouldFindVolunteersByAvailability() {
            when(volunteerRepository.findByAvailabilityContainingIgnoreCase("Weekends"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> results = volunteerProfileService.findVolunteersByAvailability("Weekends");

            assertThat(results).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Delete Profile Tests")
    class DeleteProfileTests {

        @Test
        @DisplayName("Should delete profile successfully")
        void shouldDeleteProfileSuccessfully() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            doNothing().when(volunteerRepository).deleteById(1L);

            volunteerProfileService.deleteProfile(1L);

            verify(volunteerRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent profile")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> volunteerProfileService.deleteProfile(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(volunteerRepository, never()).deleteById(any());
        }
    }
}
