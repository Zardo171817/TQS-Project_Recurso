package com.example.demo.controller;

import com.example.demo.dto.CreateVolunteerProfileRequest;
import com.example.demo.dto.UpdateVolunteerProfileRequest;
import com.example.demo.dto.VolunteerProfileResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.VolunteerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Volunteer Profile Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VolunteerProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private static final String BASE_URL = "/api/volunteers";

    @BeforeEach
    void setUp() {
        volunteerRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/volunteers/profile - Create Profile")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create profile successfully with all fields")
        void createProfileWithAllFields() throws Exception {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", "+351912345678",
                    "Java, Python", "Education, Environment", "Weekends", "I love volunteering"
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john@email.com")))
                    .andExpect(jsonPath("$.phone", is("+351912345678")))
                    .andExpect(jsonPath("$.skills", is("Java, Python")))
                    .andExpect(jsonPath("$.interests", is("Education, Environment")))
                    .andExpect(jsonPath("$.availability", is("Weekends")))
                    .andExpect(jsonPath("$.bio", is("I love volunteering")))
                    .andExpect(jsonPath("$.totalPoints", is(0)))
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.profileCreatedAt", notNullValue()));

            assertEquals(1, volunteerRepository.count());
        }

        @Test
        @DisplayName("Should create profile with minimal required fields")
        void createProfileWithMinimalFields() throws Exception {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "Jane Doe", "jane@email.com", null, null, null, null, null
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Jane Doe")))
                    .andExpect(jsonPath("$.email", is("jane@email.com")))
                    .andExpect(jsonPath("$.phone", nullValue()))
                    .andExpect(jsonPath("$.skills", nullValue()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void createProfileBlankName() throws Exception {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "", "john@email.com", null, null, null, null, null
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name", notNullValue()));
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void createProfileInvalidEmail() throws Exception {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "invalid-email", null, null, null, null, null
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email", notNullValue()));
        }

        @Test
        @DisplayName("Should return 400 when email already exists")
        void createProfileDuplicateEmail() throws Exception {
            Volunteer existing = new Volunteer();
            existing.setName("Existing User");
            existing.setEmail("existing@email.com");
            existing.setTotalPoints(0);
            volunteerRepository.save(existing);

            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "John Doe", "existing@email.com", null, null, null, null, null
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Email already exists")));
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void createProfileNameTooShort() throws Exception {
            CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest(
                    "J", "john@email.com", null, null, null, null, null
            );

            mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/volunteers/profile/{id} - Update Profile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void updateProfileSuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");

            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("John Updated");
            request.setSkills("Java, Python, JavaScript");

            mockMvc.perform(put(BASE_URL + "/profile/" + volunteer.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("John Updated")))
                    .andExpect(jsonPath("$.skills", is("Java, Python, JavaScript")))
                    .andExpect(jsonPath("$.profileUpdatedAt", notNullValue()));
        }

        @Test
        @DisplayName("Should update only provided fields")
        void updateProfilePartial() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");
            volunteer.setSkills("Original Skills");
            volunteerRepository.save(volunteer);

            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setPhone("+351999999999");

            mockMvc.perform(put(BASE_URL + "/profile/" + volunteer.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phone", is("+351999999999")))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.skills", is("Original Skills")));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void updateProfileNotFound() throws Exception {
            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();
            request.setName("Updated Name");

            mockMvc.perform(put(BASE_URL + "/profile/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when no fields to update")
        void updateProfileNoFields() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");

            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest();

            mockMvc.perform(put(BASE_URL + "/profile/" + volunteer.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("No fields to update")));
        }

        @Test
        @DisplayName("Should update all fields when all provided")
        void updateProfileAllFields() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");

            UpdateVolunteerProfileRequest request = new UpdateVolunteerProfileRequest(
                    "New Name", "+351111111111", "New Skills",
                    "New Interests", "New Availability", "New Bio"
            );

            mockMvc.perform(put(BASE_URL + "/profile/" + volunteer.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("New Name")))
                    .andExpect(jsonPath("$.phone", is("+351111111111")))
                    .andExpect(jsonPath("$.skills", is("New Skills")))
                    .andExpect(jsonPath("$.interests", is("New Interests")))
                    .andExpect(jsonPath("$.availability", is("New Availability")))
                    .andExpect(jsonPath("$.bio", is("New Bio")));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profile/{id} - Get Profile by ID")
    class GetProfileByIdTests {

        @Test
        @DisplayName("Should get profile by id successfully")
        void getProfileByIdSuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");

            mockMvc.perform(get(BASE_URL + "/profile/" + volunteer.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(volunteer.getId().intValue())))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john@email.com")));
        }

        @Test
        @DisplayName("Should return 404 when profile not found")
        void getProfileByIdNotFound() throws Exception {
            mockMvc.perform(get(BASE_URL + "/profile/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profile/email/{email} - Get Profile by Email")
    class GetProfileByEmailTests {

        @Test
        @DisplayName("Should get profile by email successfully")
        void getProfileByEmailSuccess() throws Exception {
            createTestVolunteer("john@email.com");

            mockMvc.perform(get(BASE_URL + "/profile/email/john@email.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", is("john@email.com")))
                    .andExpect(jsonPath("$.name", is("John Doe")));
        }

        @Test
        @DisplayName("Should return 404 when email not found")
        void getProfileByEmailNotFound() throws Exception {
            mockMvc.perform(get(BASE_URL + "/profile/email/nonexistent@email.com"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profiles - Get All Profiles")
    class GetAllProfilesTests {

        @Test
        @DisplayName("Should get all profiles")
        void getAllProfilesSuccess() throws Exception {
            createTestVolunteer("john@email.com");
            createTestVolunteer("jane@email.com");

            mockMvc.perform(get(BASE_URL + "/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Should return empty list when no profiles exist")
        void getAllProfilesEmpty() throws Exception {
            mockMvc.perform(get(BASE_URL + "/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profiles/skills/{skills} - Find by Skills")
    class FindBySkillsTests {

        @Test
        @DisplayName("Should find volunteers by skills")
        void findBySkillsSuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");
            volunteer.setSkills("Java, Python");
            volunteerRepository.save(volunteer);

            mockMvc.perform(get(BASE_URL + "/profiles/skills/Java"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].skills", containsString("Java")));
        }

        @Test
        @DisplayName("Should return empty list when no match")
        void findBySkillsNoMatch() throws Exception {
            createTestVolunteer("john@email.com");

            mockMvc.perform(get(BASE_URL + "/profiles/skills/NonexistentSkill"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profiles/interests/{interests} - Find by Interests")
    class FindByInterestsTests {

        @Test
        @DisplayName("Should find volunteers by interests")
        void findByInterestsSuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");
            volunteer.setInterests("Education, Environment");
            volunteerRepository.save(volunteer);

            mockMvc.perform(get(BASE_URL + "/profiles/interests/Education"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].interests", containsString("Education")));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/profiles/availability/{availability} - Find by Availability")
    class FindByAvailabilityTests {

        @Test
        @DisplayName("Should find volunteers by availability")
        void findByAvailabilitySuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");
            volunteer.setAvailability("Weekends, Evenings");
            volunteerRepository.save(volunteer);

            mockMvc.perform(get(BASE_URL + "/profiles/availability/Weekends"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].availability", containsString("Weekends")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/volunteers/profile/{id} - Delete Profile")
    class DeleteProfileTests {

        @Test
        @DisplayName("Should delete profile successfully")
        void deleteProfileSuccess() throws Exception {
            Volunteer volunteer = createTestVolunteer("john@email.com");
            Long volunteerId = volunteer.getId();

            mockMvc.perform(delete(BASE_URL + "/profile/" + volunteerId))
                    .andExpect(status().isNoContent());

            assertFalse(volunteerRepository.existsById(volunteerId));
        }

        @Test
        @DisplayName("Should return 404 when profile not found")
        void deleteProfileNotFound() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/profile/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Integration Flow Tests")
    class IntegrationFlowTests {

        @Test
        @DisplayName("Complete CRUD flow for volunteer profile")
        void completeCrudFlow() throws Exception {
            CreateVolunteerProfileRequest createRequest = new CreateVolunteerProfileRequest(
                    "John Doe", "john@email.com", "+351912345678",
                    "Java", "Education", "Weekends", "My bio"
            );

            MvcResult createResult = mockMvc.perform(post(BASE_URL + "/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            VolunteerProfileResponse created = objectMapper.readValue(
                    createResult.getResponse().getContentAsString(),
                    VolunteerProfileResponse.class
            );
            Long profileId = created.getId();

            mockMvc.perform(get(BASE_URL + "/profile/" + profileId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("John Doe")));

            UpdateVolunteerProfileRequest updateRequest = new UpdateVolunteerProfileRequest();
            updateRequest.setName("John Updated");
            updateRequest.setSkills("Java, Python");

            mockMvc.perform(put(BASE_URL + "/profile/" + profileId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("John Updated")))
                    .andExpect(jsonPath("$.skills", is("Java, Python")));

            mockMvc.perform(get(BASE_URL + "/profile/" + profileId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("John Updated")));

            mockMvc.perform(delete(BASE_URL + "/profile/" + profileId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(BASE_URL + "/profile/" + profileId))
                    .andExpect(status().isNotFound());
        }
    }

    private Volunteer createTestVolunteer(String email) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("John Doe");
        volunteer.setEmail(email);
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        return volunteerRepository.save(volunteer);
    }
}
