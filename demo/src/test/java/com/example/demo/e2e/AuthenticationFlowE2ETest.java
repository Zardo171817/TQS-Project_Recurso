package com.example.demo.e2e;

import com.example.demo.dto.*;
import com.example.demo.entity.UserType;
import com.example.demo.integration.AbstractIntegrationTest;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("E2E: Authentication Flow")
class AuthenticationFlowE2ETest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete authentication flow for all user types")
    void completeAuthenticationFlow() {
        // Test registration and login for each user type
        for (UserType userType : UserType.values()) {
            String email = userType.name().toLowerCase() + "@test.com";

            // === Register ===
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setName("Test " + userType.name());
            registerRequest.setEmail(email);
            registerRequest.setPassword("SecurePassword123");
            registerRequest.setUserType(userType);

            ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                    "/api/auth/register", registerRequest, AuthResponse.class);

            assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(registerResponse.getBody().isSuccess()).isTrue();
            assertThat(registerResponse.getBody().getUserType()).isEqualTo(userType);
            assertThat(registerResponse.getBody().getMessage()).contains("Registo efetuado com sucesso");

            // === Login ===
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword("SecurePassword123");

            ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                    "/api/auth/login", loginRequest, AuthResponse.class);

            assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(loginResponse.getBody().isSuccess()).isTrue();
            assertThat(loginResponse.getBody().getEmail()).isEqualTo(email);
            assertThat(loginResponse.getBody().getMessage()).contains("Login efetuado com sucesso");

            // === Check email exists ===
            ResponseEntity<Boolean> existsResponse = restTemplate.getForEntity(
                    "/api/auth/check-email/" + email, Boolean.class);

            assertThat(existsResponse.getBody()).isTrue();
        }
    }

    @Test
    @DisplayName("User deactivation prevents login")
    void userDeactivationPreventsLogin() {
        // Register user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Deactivate User");
        registerRequest.setEmail("deactivate@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setUserType(UserType.VOLUNTEER);

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", registerRequest, AuthResponse.class);

        Long userId = registerResponse.getBody().getId();

        // Verify login works
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("deactivate@test.com");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> loginBefore = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(loginBefore.getBody().isSuccess()).isTrue();

        // Deactivate user
        restTemplate.delete("/api/auth/user/" + userId);

        // Verify login fails
        ResponseEntity<AuthResponse> loginAfter = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(loginAfter.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("Duplicate email registration fails")
    void duplicateEmailRegistrationFails() {
        // Register first user
        RegisterRequest firstRequest = new RegisterRequest();
        firstRequest.setName("First User");
        firstRequest.setEmail("duplicate@test.com");
        firstRequest.setPassword("password123");
        firstRequest.setUserType(UserType.VOLUNTEER);

        restTemplate.postForEntity("/api/auth/register", firstRequest, AuthResponse.class);

        // Try to register with same email
        RegisterRequest secondRequest = new RegisterRequest();
        secondRequest.setName("Second User");
        secondRequest.setEmail("duplicate@test.com");
        secondRequest.setPassword("differentPassword");
        secondRequest.setUserType(UserType.PROMOTER); // Different type

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/register", secondRequest, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Email ja registado");
    }

    @Test
    @DisplayName("Wrong password login fails")
    void wrongPasswordLoginFails() {
        // Register user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("wrongpass@test.com");
        registerRequest.setPassword("correctPassword");
        registerRequest.setUserType(UserType.VOLUNTEER);

        restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);

        // Try to login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpass@test.com");
        loginRequest.setPassword("wrongPassword");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Email ou password invalidos");
    }

    @Test
    @DisplayName("Non-existent email login fails")
    void nonExistentEmailLoginFails() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("anyPassword");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("Get user by ID and email")
    void getUserByIdAndEmail() {
        // Register user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Get User Test");
        registerRequest.setEmail("getuser@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setUserType(UserType.VOLUNTEER);

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", registerRequest, AuthResponse.class);

        Long userId = registerResponse.getBody().getId();

        // Get by ID
        ResponseEntity<Object> byIdResponse = restTemplate.getForEntity(
                "/api/auth/user/" + userId, Object.class);

        assertThat(byIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Get by email
        ResponseEntity<Object> byEmailResponse = restTemplate.getForEntity(
                "/api/auth/user/email/getuser@test.com", Object.class);

        assertThat(byEmailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Password is properly hashed and validated")
    void passwordHashingWorks() {
        // Register with specific password
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Hash Test");
        registerRequest.setEmail("hash@test.com");
        registerRequest.setPassword("MySecurePassword123!");
        registerRequest.setUserType(UserType.VOLUNTEER);

        restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);

        // Exact password works
        LoginRequest correctLogin = new LoginRequest();
        correctLogin.setEmail("hash@test.com");
        correctLogin.setPassword("MySecurePassword123!");

        ResponseEntity<AuthResponse> correctResponse = restTemplate.postForEntity(
                "/api/auth/login", correctLogin, AuthResponse.class);

        assertThat(correctResponse.getBody().isSuccess()).isTrue();

        // Similar but different password fails
        LoginRequest wrongLogin = new LoginRequest();
        wrongLogin.setEmail("hash@test.com");
        wrongLogin.setPassword("MySecurePassword123"); // Missing !

        ResponseEntity<AuthResponse> wrongResponse = restTemplate.postForEntity(
                "/api/auth/login", wrongLogin, AuthResponse.class);

        assertThat(wrongResponse.getBody().isSuccess()).isFalse();
    }
}
