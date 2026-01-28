package com.example.demo.service;

import com.example.demo.dto.CreateVolunteerProfileRequest;
import com.example.demo.dto.UpdateVolunteerProfileRequest;
import com.example.demo.dto.VolunteerProfileResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolunteerProfileService Tests")
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
        volunteer.setEmail("john@email.com");
        volunteer.setPhone("+351912345678");
        volunteer.setSkills("Java, Python");
        volunteer.setInterests("Education, Environment");
        volunteer.setAvailability("Weekends");
        volunteer.setBio("I love volunteering");
        volunteer.setTotalPoints(100);
        volunteer.setProfileCreatedAt(LocalDateTime.now().minusDays(1));
        volunteer.setProfileUpdatedAt(LocalDateTime.now());

        createRequest = new CreateVolunteerProfileRequest(
                "John Doe", "john@email.com", "+351912345678",
                "Java, Python", "Education, Environment", "Weekends", "I love volunteering"
        );

        updateRequest = new UpdateVolunteerProfileRequest();
        updateRequest.setName("John Updated");
        updateRequest.setSkills("Java, Python, JavaScript");
    }

    @Nested
    @DisplayName("createProfile Tests")
    class CreateProfileTests {

        @Test
        @DisplayName("Successfully creates profile when email does not exist")
        void createProfileSuccess() {
            when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

            VolunteerProfileResponse response = volunteerProfileService.createProfile(createRequest);

            assertNotNull(response);
            assertEquals("John Doe", response.getName());
            assertEquals("john@email.com", response.getEmail());

            ArgumentCaptor<Volunteer> captor = ArgumentCaptor.forClass(Volunteer.class);
            verify(volunteerRepository).save(captor.capture());

            Volunteer savedVolunteer = captor.getValue();
            assertEquals("John Doe", savedVolunteer.getName());
            assertEquals("john@email.com", savedVolunteer.getEmail());
            assertEquals(0, savedVolunteer.getTotalPoints());
            assertNotNull(savedVolunteer.getProfileCreatedAt());
        }

        @Test
        @DisplayName("Throws exception when email already exists")
        void createProfileEmailExists() {
            when(volunteerRepository.existsByEmail(anyString())).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> volunteerProfileService.createProfile(createRequest)
            );

            assertTrue(exception.getMessage().contains("Email already exists"));
            verify(volunteerRepository, never()).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Creates profile with minimal required fields")
        void createProfileMinimalFields() {
            CreateVolunteerProfileRequest minimalRequest = new CreateVolunteerProfileRequest(
                    "Jane", "jane@email.com", null, null, null, null, null
            );

            Volunteer minimalVolunteer = new Volunteer();
            minimalVolunteer.setId(2L);
            minimalVolunteer.setName("Jane");
            minimalVolunteer.setEmail("jane@email.com");
            minimalVolunteer.setTotalPoints(0);

            when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(minimalVolunteer);

            VolunteerProfileResponse response = volunteerProfileService.createProfile(minimalRequest);

            assertNotNull(response);
            assertEquals("Jane", response.getName());
            assertNull(response.getPhone());
            assertNull(response.getSkills());
        }

        @Test
        @DisplayName("Creates profile with all optional fields")
        void createProfileAllFields() {
            when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

            VolunteerProfileResponse response = volunteerProfileService.createProfile(createRequest);

            assertNotNull(response);
            assertEquals("Java, Python", response.getSkills());
            assertEquals("Education, Environment", response.getInterests());
            assertEquals("Weekends", response.getAvailability());
            assertEquals("I love volunteering", response.getBio());
        }
    }

    @Nested
    @DisplayName("updateProfile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Successfully updates profile")
        void updateProfileSuccess() {
            Volunteer updatedVolunteer = new Volunteer();
            updatedVolunteer.setId(1L);
            updatedVolunteer.setName("John Updated");
            updatedVolunteer.setEmail("john@email.com");
            updatedVolunteer.setSkills("Java, Python, JavaScript");
            updatedVolunteer.setTotalPoints(100);
            updatedVolunteer.setProfileUpdatedAt(LocalDateTime.now());

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(updatedVolunteer);

            VolunteerProfileResponse response = volunteerProfileService.updateProfile(1L, updateRequest);

            assertNotNull(response);
            assertEquals("John Updated", response.getName());
            assertEquals("Java, Python, JavaScript", response.getSkills());
            verify(volunteerRepository).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Throws exception when volunteer not found")
        void updateProfileNotFound() {
            when(volunteerRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> volunteerProfileService.updateProfile(999L, updateRequest)
            );

            verify(volunteerRepository, never()).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Throws exception when no updates provided")
        void updateProfileNoUpdates() {
            UpdateVolunteerProfileRequest emptyRequest = new UpdateVolunteerProfileRequest();

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> volunteerProfileService.updateProfile(1L, emptyRequest)
            );

            verify(volunteerRepository, never()).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Updates only provided fields")
        void updateProfilePartialUpdate() {
            UpdateVolunteerProfileRequest partialRequest = new UpdateVolunteerProfileRequest();
            partialRequest.setPhone("+351999999999");

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VolunteerProfileResponse response = volunteerProfileService.updateProfile(1L, partialRequest);

            assertNotNull(response);
            assertEquals("+351999999999", response.getPhone());
            assertEquals("John Doe", response.getName());
        }

        @Test
        @DisplayName("Updates all fields when all provided")
        void updateProfileAllFields() {
            UpdateVolunteerProfileRequest fullRequest = new UpdateVolunteerProfileRequest(
                    "New Name", "+351111111111", "New Skills",
                    "New Interests", "New Availability", "New Bio"
            );

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VolunteerProfileResponse response = volunteerProfileService.updateProfile(1L, fullRequest);

            assertNotNull(response);
            assertEquals("New Name", response.getName());
            assertEquals("+351111111111", response.getPhone());
            assertEquals("New Skills", response.getSkills());
            assertEquals("New Interests", response.getInterests());
            assertEquals("New Availability", response.getAvailability());
            assertEquals("New Bio", response.getBio());
        }

        @Test
        @DisplayName("Sets profileUpdatedAt when updating")
        void updateProfileSetsUpdatedAt() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            volunteerProfileService.updateProfile(1L, updateRequest);

            ArgumentCaptor<Volunteer> captor = ArgumentCaptor.forClass(Volunteer.class);
            verify(volunteerRepository).save(captor.capture());

            assertNotNull(captor.getValue().getProfileUpdatedAt());
        }
    }

    @Nested
    @DisplayName("getProfile Tests")
    class GetProfileTests {

        @Test
        @DisplayName("Successfully gets profile by id")
        void getProfileSuccess() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("John Doe", response.getName());
            assertEquals("john@email.com", response.getEmail());
        }

        @Test
        @DisplayName("Throws exception when volunteer not found")
        void getProfileNotFound() {
            when(volunteerRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> volunteerProfileService.getProfile(999L)
            );
        }
    }

    @Nested
    @DisplayName("getProfileByEmail Tests")
    class GetProfileByEmailTests {

        @Test
        @DisplayName("Successfully gets profile by email")
        void getProfileByEmailSuccess() {
            when(volunteerRepository.findByEmail("john@email.com")).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfileByEmail("john@email.com");

            assertNotNull(response);
            assertEquals("john@email.com", response.getEmail());
            assertEquals("John Doe", response.getName());
        }

        @Test
        @DisplayName("Throws exception when email not found")
        void getProfileByEmailNotFound() {
            when(volunteerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> volunteerProfileService.getProfileByEmail("nonexistent@email.com")
            );
        }
    }

    @Nested
    @DisplayName("getAllProfiles Tests")
    class GetAllProfilesTests {

        @Test
        @DisplayName("Returns all profiles")
        void getAllProfilesSuccess() {
            Volunteer volunteer2 = new Volunteer();
            volunteer2.setId(2L);
            volunteer2.setName("Jane Doe");
            volunteer2.setEmail("jane@email.com");
            volunteer2.setTotalPoints(50);

            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer, volunteer2));

            List<VolunteerProfileResponse> profiles = volunteerProfileService.getAllProfiles();

            assertNotNull(profiles);
            assertEquals(2, profiles.size());
            assertEquals("John Doe", profiles.get(0).getName());
            assertEquals("Jane Doe", profiles.get(1).getName());
        }

        @Test
        @DisplayName("Returns empty list when no profiles exist")
        void getAllProfilesEmpty() {
            when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

            List<VolunteerProfileResponse> profiles = volunteerProfileService.getAllProfiles();

            assertNotNull(profiles);
            assertTrue(profiles.isEmpty());
        }
    }

    @Nested
    @DisplayName("findVolunteersBySkills Tests")
    class FindBySkillsTests {

        @Test
        @DisplayName("Finds volunteers by skills")
        void findBySkillsSuccess() {
            when(volunteerRepository.findBySkillsContainingIgnoreCase("Java"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersBySkills("Java");

            assertNotNull(profiles);
            assertEquals(1, profiles.size());
            assertTrue(profiles.get(0).getSkills().contains("Java"));
        }

        @Test
        @DisplayName("Returns empty list when no match found")
        void findBySkillsNoMatch() {
            when(volunteerRepository.findBySkillsContainingIgnoreCase("Nonexistent"))
                    .thenReturn(Collections.emptyList());

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersBySkills("Nonexistent");

            assertNotNull(profiles);
            assertTrue(profiles.isEmpty());
        }
    }

    @Nested
    @DisplayName("findVolunteersByInterests Tests")
    class FindByInterestsTests {

        @Test
        @DisplayName("Finds volunteers by interests")
        void findByInterestsSuccess() {
            when(volunteerRepository.findByInterestsContainingIgnoreCase("Education"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByInterests("Education");

            assertNotNull(profiles);
            assertEquals(1, profiles.size());
            assertTrue(profiles.get(0).getInterests().contains("Education"));
        }

        @Test
        @DisplayName("Returns empty list when no match found")
        void findByInterestsNoMatch() {
            when(volunteerRepository.findByInterestsContainingIgnoreCase("Sports"))
                    .thenReturn(Collections.emptyList());

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByInterests("Sports");

            assertNotNull(profiles);
            assertTrue(profiles.isEmpty());
        }
    }

    @Nested
    @DisplayName("findVolunteersByAvailability Tests")
    class FindByAvailabilityTests {

        @Test
        @DisplayName("Finds volunteers by availability")
        void findByAvailabilitySuccess() {
            when(volunteerRepository.findByAvailabilityContainingIgnoreCase("Weekends"))
                    .thenReturn(Collections.singletonList(volunteer));

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByAvailability("Weekends");

            assertNotNull(profiles);
            assertEquals(1, profiles.size());
            assertTrue(profiles.get(0).getAvailability().contains("Weekends"));
        }

        @Test
        @DisplayName("Returns empty list when no match found")
        void findByAvailabilityNoMatch() {
            when(volunteerRepository.findByAvailabilityContainingIgnoreCase("Nightshift"))
                    .thenReturn(Collections.emptyList());

            List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByAvailability("Nightshift");

            assertNotNull(profiles);
            assertTrue(profiles.isEmpty());
        }
    }

    @Nested
    @DisplayName("deleteProfile Tests")
    class DeleteProfileTests {

        @Test
        @DisplayName("Successfully deletes profile")
        void deleteProfileSuccess() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            doNothing().when(volunteerRepository).deleteById(1L);

            assertDoesNotThrow(() -> volunteerProfileService.deleteProfile(1L));

            verify(volunteerRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Throws exception when volunteer not found")
        void deleteProfileNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> volunteerProfileService.deleteProfile(999L)
            );

            verify(volunteerRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Handles profile with zero points")
        void handleZeroPoints() {
            volunteer.setTotalPoints(0);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

            assertEquals(0, response.getTotalPoints());
        }

        @Test
        @DisplayName("Handles profile with null dates")
        void handleNullDates() {
            volunteer.setProfileCreatedAt(null);
            volunteer.setProfileUpdatedAt(null);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

            assertNull(response.getProfileCreatedAt());
            assertNull(response.getProfileUpdatedAt());
        }

        @Test
        @DisplayName("Handles special characters in fields")
        void handleSpecialCharacters() {
            volunteer.setName("José María");
            volunteer.setSkills("C++, C#, Node.js");
            volunteer.setBio("I'm passionate about helping others & making a difference!");

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

            VolunteerProfileResponse response = volunteerProfileService.getProfile(1L);

            assertEquals("José María", response.getName());
            assertEquals("C++, C#, Node.js", response.getSkills());
            assertTrue(response.getBio().contains("&"));
        }
    }
}
