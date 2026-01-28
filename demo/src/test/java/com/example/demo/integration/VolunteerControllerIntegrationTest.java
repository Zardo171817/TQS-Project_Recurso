package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Volunteer Controller Integration Tests")
class VolunteerControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Nested
    @DisplayName("Profile Management Tests")
    class ProfileManagementTests {

        @Test
        @DisplayName("Should create volunteer profile")
        void shouldCreateVolunteerProfile() {
            CreateVolunteerProfileRequest request = createProfileRequest("create@test.com");

            ResponseEntity<VolunteerProfileResponse> response = restTemplate.postForEntity(
                    "/api/volunteers/profile", request, VolunteerProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getEmail()).isEqualTo("create@test.com");
            assertThat(response.getBody().getTotalPoints()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should fail to create profile with duplicate email")
        void shouldFailToCreateProfileWithDuplicateEmail() {
            CreateVolunteerProfileRequest request = createProfileRequest("duplicate@test.com");

            // First creation
            restTemplate.postForEntity("/api/volunteers/profile", request, VolunteerProfileResponse.class);

            // Second creation with same email
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/volunteers/profile", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should update volunteer profile")
        void shouldUpdateVolunteerProfile() {
            // Create first
            CreateVolunteerProfileRequest createRequest = createProfileRequest("update@test.com");
            ResponseEntity<VolunteerProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/volunteers/profile", createRequest, VolunteerProfileResponse.class);

            Long volunteerId = createResponse.getBody().getId();

            // Update
            UpdateVolunteerProfileRequest updateRequest = new UpdateVolunteerProfileRequest();
            updateRequest.setName("Updated Name");
            updateRequest.setSkills("New Skills");

            HttpEntity<UpdateVolunteerProfileRequest> entity = new HttpEntity<>(updateRequest);
            ResponseEntity<VolunteerProfileResponse> updateResponse = restTemplate.exchange(
                    "/api/volunteers/profile/" + volunteerId, HttpMethod.PUT, entity, VolunteerProfileResponse.class);

            assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updateResponse.getBody().getName()).isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("Should get profile by ID")
        void shouldGetProfileById() {
            CreateVolunteerProfileRequest request = createProfileRequest("getbyid@test.com");
            ResponseEntity<VolunteerProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/volunteers/profile", request, VolunteerProfileResponse.class);

            Long volunteerId = createResponse.getBody().getId();

            ResponseEntity<VolunteerProfileResponse> response = restTemplate.getForEntity(
                    "/api/volunteers/profile/" + volunteerId, VolunteerProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(volunteerId);
        }

        @Test
        @DisplayName("Should get profile by email")
        void shouldGetProfileByEmail() {
            CreateVolunteerProfileRequest request = createProfileRequest("getbyemail@test.com");
            restTemplate.postForEntity("/api/volunteers/profile", request, VolunteerProfileResponse.class);

            ResponseEntity<VolunteerProfileResponse> response = restTemplate.getForEntity(
                    "/api/volunteers/profile/email/getbyemail@test.com", VolunteerProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getEmail()).isEqualTo("getbyemail@test.com");
        }

        @Test
        @DisplayName("Should delete profile")
        void shouldDeleteProfile() {
            CreateVolunteerProfileRequest request = createProfileRequest("delete@test.com");
            ResponseEntity<VolunteerProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/volunteers/profile", request, VolunteerProfileResponse.class);

            Long volunteerId = createResponse.getBody().getId();

            restTemplate.delete("/api/volunteers/profile/" + volunteerId);

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "/api/volunteers/profile/" + volunteerId, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should find volunteers by skills")
        void shouldFindVolunteersBySkills() {
            CreateVolunteerProfileRequest request = createProfileRequest("skills@test.com");
            request.setSkills("Java, Python, Docker");
            restTemplate.postForEntity("/api/volunteers/profile", request, VolunteerProfileResponse.class);

            ResponseEntity<List<VolunteerProfileResponse>> response = restTemplate.exchange(
                    "/api/volunteers/profiles/skills/Java",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VolunteerProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should find volunteers by interests")
        void shouldFindVolunteersByInterests() {
            CreateVolunteerProfileRequest request = createProfileRequest("interests@test.com");
            request.setInterests("Environment, Education");
            restTemplate.postForEntity("/api/volunteers/profile", request, VolunteerProfileResponse.class);

            ResponseEntity<List<VolunteerProfileResponse>> response = restTemplate.exchange(
                    "/api/volunteers/profiles/interests/Environment",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VolunteerProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should find volunteers by availability")
        void shouldFindVolunteersByAvailability() {
            CreateVolunteerProfileRequest request = createProfileRequest("availability@test.com");
            request.setAvailability("Weekends, Mornings");
            restTemplate.postForEntity("/api/volunteers/profile", request, VolunteerProfileResponse.class);

            ResponseEntity<List<VolunteerProfileResponse>> response = restTemplate.exchange(
                    "/api/volunteers/profiles/availability/Weekends",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VolunteerProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Points and Ranking Tests")
    class PointsAndRankingTests {

        @Test
        @DisplayName("Should get volunteer points")
        void shouldGetVolunteerPoints() {
            Volunteer volunteer = new Volunteer();
            volunteer.setName("Test");
            volunteer.setEmail("points@test.com");
            volunteer.setTotalPoints(100);
            volunteer = volunteerRepository.save(volunteer);

            ResponseEntity<VolunteerPointsResponse> response = restTemplate.getForEntity(
                    "/api/volunteers/" + volunteer.getId() + "/points", VolunteerPointsResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should get volunteers ranking")
        void shouldGetVolunteersRanking() {
            // Create volunteers with different points
            Volunteer v1 = createAndSaveVolunteer("rank1@test.com", 100);
            Volunteer v2 = createAndSaveVolunteer("rank2@test.com", 200);
            Volunteer v3 = createAndSaveVolunteer("rank3@test.com", 50);

            ResponseEntity<List<VolunteerPointsResponse>> response = restTemplate.exchange(
                    "/api/volunteers/ranking",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VolunteerPointsResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(3);
            // Should be sorted by points descending
            assertThat(response.getBody().get(0).getTotalPoints()).isEqualTo(200);
            assertThat(response.getBody().get(1).getTotalPoints()).isEqualTo(100);
            assertThat(response.getBody().get(2).getTotalPoints()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should get top volunteers")
        void shouldGetTopVolunteers() {
            createAndSaveVolunteer("top1@test.com", 300);
            createAndSaveVolunteer("top2@test.com", 200);
            createAndSaveVolunteer("top3@test.com", 100);

            ResponseEntity<List<VolunteerPointsResponse>> response = restTemplate.exchange(
                    "/api/volunteers/top/2",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VolunteerPointsResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }
    }

    private CreateVolunteerProfileRequest createProfileRequest(String email) {
        CreateVolunteerProfileRequest request = new CreateVolunteerProfileRequest();
        request.setName("Test Volunteer");
        request.setEmail(email);
        request.setPhone("123456789");
        request.setSkills("Java");
        request.setInterests("Tech");
        request.setAvailability("Weekends");
        request.setBio("Test bio");
        return request;
    }

    private Volunteer createAndSaveVolunteer(String email, int points) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Test");
        volunteer.setEmail(email);
        volunteer.setTotalPoints(points);
        return volunteerRepository.save(volunteer);
    }
}
