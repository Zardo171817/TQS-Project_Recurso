package com.example.demo.integration;

import com.example.demo.dto.*;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Promoter Profile Controller Integration Tests")
class PromoterProfileControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() {
        // Delete in correct order respecting foreign key constraints
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();
    }

    @Nested
    @DisplayName("Profile Management Tests")
    class ProfileManagementTests {

        @Test
        @DisplayName("Should create promoter profile")
        void shouldCreatePromoterProfile() {
            CreatePromoterProfileRequest request = createProfileRequest("create@test.com");

            ResponseEntity<PromoterProfileResponse> response = restTemplate.postForEntity(
                    "/api/promoters/profile", request, PromoterProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getEmail()).isEqualTo("create@test.com");
            assertThat(response.getBody().getOrganization()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("Should fail to create profile with duplicate email")
        void shouldFailToCreateProfileWithDuplicateEmail() {
            CreatePromoterProfileRequest request = createProfileRequest("duplicate@test.com");

            restTemplate.postForEntity("/api/promoters/profile", request, PromoterProfileResponse.class);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/promoters/profile", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should update promoter profile")
        void shouldUpdatePromoterProfile() {
            CreatePromoterProfileRequest createRequest = createProfileRequest("update@test.com");
            ResponseEntity<PromoterProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/promoters/profile", createRequest, PromoterProfileResponse.class);

            Long promoterId = createResponse.getBody().getId();

            UpdatePromoterProfileRequest updateRequest = new UpdatePromoterProfileRequest();
            updateRequest.setName("Updated Name");
            updateRequest.setOrganization("Updated Org");

            HttpEntity<UpdatePromoterProfileRequest> entity = new HttpEntity<>(updateRequest);
            ResponseEntity<PromoterProfileResponse> response = restTemplate.exchange(
                    "/api/promoters/profile/" + promoterId,
                    HttpMethod.PUT,
                    entity,
                    PromoterProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo("Updated Name");
            assertThat(response.getBody().getOrganization()).isEqualTo("Updated Org");
        }

        @Test
        @DisplayName("Should get profile by ID")
        void shouldGetProfileById() {
            CreatePromoterProfileRequest request = createProfileRequest("getbyid@test.com");
            ResponseEntity<PromoterProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/promoters/profile", request, PromoterProfileResponse.class);

            Long promoterId = createResponse.getBody().getId();

            ResponseEntity<PromoterProfileResponse> response = restTemplate.getForEntity(
                    "/api/promoters/profile/" + promoterId, PromoterProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(promoterId);
        }

        @Test
        @DisplayName("Should get profile by email")
        void shouldGetProfileByEmail() {
            CreatePromoterProfileRequest request = createProfileRequest("getbyemail@test.com");
            restTemplate.postForEntity("/api/promoters/profile", request, PromoterProfileResponse.class);

            ResponseEntity<PromoterProfileResponse> response = restTemplate.getForEntity(
                    "/api/promoters/profile/email/getbyemail@test.com", PromoterProfileResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getEmail()).isEqualTo("getbyemail@test.com");
        }

        @Test
        @DisplayName("Should delete profile")
        void shouldDeleteProfile() {
            CreatePromoterProfileRequest request = createProfileRequest("delete@test.com");
            ResponseEntity<PromoterProfileResponse> createResponse = restTemplate.postForEntity(
                    "/api/promoters/profile", request, PromoterProfileResponse.class);

            Long promoterId = createResponse.getBody().getId();

            restTemplate.delete("/api/promoters/profile/" + promoterId);

            ResponseEntity<String> response = restTemplate.getForEntity(
                    "/api/promoters/profile/" + promoterId, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should find promoters by organization")
        void shouldFindPromotersByOrganization() {
            CreatePromoterProfileRequest request = createProfileRequest("org@test.com");
            request.setOrganization("Unique Organization Name");
            restTemplate.postForEntity("/api/promoters/profile", request, PromoterProfileResponse.class);

            ResponseEntity<List<PromoterProfileResponse>> response = restTemplate.exchange(
                    "/api/promoters/profiles/organization/Unique",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PromoterProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should find promoters by area of activity")
        void shouldFindPromotersByAreaOfActivity() {
            CreatePromoterProfileRequest request = createProfileRequest("area@test.com");
            request.setAreaOfActivity("Environmental Protection");
            restTemplate.postForEntity("/api/promoters/profile", request, PromoterProfileResponse.class);

            ResponseEntity<List<PromoterProfileResponse>> response = restTemplate.exchange(
                    "/api/promoters/profiles/area/Environmental",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PromoterProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should find promoters by organization type")
        void shouldFindPromotersByOrganizationType() {
            CreatePromoterProfileRequest request = createProfileRequest("type@test.com");
            request.setOrganizationType("Non-Profit");
            restTemplate.postForEntity("/api/promoters/profile", request, PromoterProfileResponse.class);

            ResponseEntity<List<PromoterProfileResponse>> response = restTemplate.exchange(
                    "/api/promoters/profiles/type/Non-Profit",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PromoterProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }

        @Test
        @DisplayName("Should get all profiles")
        void shouldGetAllProfiles() {
            createProfileRequest("all1@test.com");
            createProfileRequest("all2@test.com");

            CreatePromoterProfileRequest req1 = createProfileRequest("all1@test.com");
            CreatePromoterProfileRequest req2 = createProfileRequest("all2@test.com");
            restTemplate.postForEntity("/api/promoters/profile", req1, PromoterProfileResponse.class);
            restTemplate.postForEntity("/api/promoters/profile", req2, PromoterProfileResponse.class);

            ResponseEntity<List<PromoterProfileResponse>> response = restTemplate.exchange(
                    "/api/promoters/profiles",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PromoterProfileResponse>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }
    }

    private CreatePromoterProfileRequest createProfileRequest(String email) {
        CreatePromoterProfileRequest request = new CreatePromoterProfileRequest();
        request.setName("Test Promoter");
        request.setEmail(email);
        request.setOrganization("Test Organization");
        request.setDescription("Test Description");
        request.setPhone("123456789");
        request.setAreaOfActivity("Social");
        request.setOrganizationType("NGO");
        return request;
    }
}
