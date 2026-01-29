package com.example.demo.integration;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.UserType;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Auth Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register volunteer successfully")
        void shouldRegisterVolunteerSuccessfully() {
            RegisterRequest request = createRegisterRequest("volunteer@test.com", UserType.VOLUNTEER);

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/register", request, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isSuccess()).isTrue();
            assertThat(response.getBody().getUserType()).isEqualTo(UserType.VOLUNTEER);
        }

        @Test
        @DisplayName("Should register promoter successfully")
        void shouldRegisterPromoterSuccessfully() {
            RegisterRequest request = createRegisterRequest("promoter@test.com", UserType.PROMOTER);

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/register", request, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().isSuccess()).isTrue();
            assertThat(response.getBody().getUserType()).isEqualTo(UserType.PROMOTER);
        }

        @Test
        @DisplayName("Should register partner successfully")
        void shouldRegisterPartnerSuccessfully() {
            RegisterRequest request = createRegisterRequest("partner@test.com", UserType.PARTNER);

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/register", request, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().isSuccess()).isTrue();
            assertThat(response.getBody().getUserType()).isEqualTo(UserType.PARTNER);
        }

        @Test
        @DisplayName("Should fail registration with duplicate email")
        void shouldFailRegistrationWithDuplicateEmail() {
            RegisterRequest request = createRegisterRequest("duplicate@test.com", UserType.VOLUNTEER);

            // First registration
            restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

            // Second registration with same email
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/register", request, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().isSuccess()).isFalse();
            assertThat(response.getBody().getMessage()).contains("Email ja registado");
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with correct credentials")
        void shouldLoginSuccessfully() {
            // Register first
            RegisterRequest registerRequest = createRegisterRequest("login@test.com", UserType.VOLUNTEER);
            restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);

            // Login
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("login@test.com");
            loginRequest.setPassword("password123");

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/login", loginRequest, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().isSuccess()).isTrue();
            assertThat(response.getBody().getEmail()).isEqualTo("login@test.com");
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void shouldFailLoginWithWrongPassword() {
            // Register first
            RegisterRequest registerRequest = createRegisterRequest("wrongpass@test.com", UserType.VOLUNTEER);
            restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);

            // Login with wrong password
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("wrongpass@test.com");
            loginRequest.setPassword("wrongpassword");

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/login", loginRequest, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().isSuccess()).isFalse();
        }

        @Test
        @DisplayName("Should fail login with non-existent email")
        void shouldFailLoginWithNonExistentEmail() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("nonexistent@test.com");
            loginRequest.setPassword("password123");

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    "/api/auth/login", loginRequest, AuthResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            // Register user
            RegisterRequest registerRequest = createRegisterRequest("exists@test.com", UserType.VOLUNTEER);
            restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);

            ResponseEntity<Boolean> existsResponse = restTemplate.getForEntity(
                    "/api/auth/check-email/exists@test.com", Boolean.class);

            assertThat(existsResponse.getBody()).isTrue();

            ResponseEntity<Boolean> notExistsResponse = restTemplate.getForEntity(
                    "/api/auth/check-email/notexists@test.com", Boolean.class);

            assertThat(notExistsResponse.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            // Register user
            RegisterRequest registerRequest = createRegisterRequest("deactivate@test.com", UserType.VOLUNTEER);
            ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                    "/api/auth/register", registerRequest, AuthResponse.class);

            Long userId = registerResponse.getBody().getId();

            // Deactivate
            restTemplate.delete("/api/auth/user/" + userId);

            // Try to login (should fail)
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("deactivate@test.com");
            loginRequest.setPassword("password123");

            ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                    "/api/auth/login", loginRequest, AuthResponse.class);

            assertThat(loginResponse.getBody().isSuccess()).isFalse();
        }
    }

    private RegisterRequest createRegisterRequest(String email, UserType userType) {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail(email);
        request.setPassword("password123");
        request.setUserType(userType);
        return request;
    }
}
