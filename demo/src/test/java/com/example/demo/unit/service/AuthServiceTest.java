package com.example.demo.unit.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserType;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setUserType(UserType.VOLUNTEER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUserType(UserType.VOLUNTEER);
        user.setActive(true);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setId(1L);
                return savedUser;
            });

            AuthResponse response = authService.register(registerRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getUserType()).isEqualTo(UserType.VOLUNTEER);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail registration when email exists")
        void shouldFailRegistrationWhenEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            AuthResponse response = authService.register(registerRequest);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Email ja registado");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should register different user types")
        void shouldRegisterDifferentUserTypes() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setId(1L);
                return savedUser;
            });

            for (UserType userType : UserType.values()) {
                registerRequest.setUserType(userType);
                registerRequest.setEmail(userType.name().toLowerCase() + "@example.com");

                AuthResponse response = authService.register(registerRequest);

                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getUserType()).isEqualTo(userType);
            }
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with correct credentials")
        void shouldLoginSuccessfully() {
            // Hash the password the same way the service does
            user.setPassword(hashPassword("password123"));
            when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            AuthResponse response = authService.login(loginRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(any(User.class)); // Updates lastLoginAt
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void shouldFailLoginWithWrongPassword() {
            user.setPassword(hashPassword("differentPassword"));
            when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(user));

            AuthResponse response = authService.login(loginRequest);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Email ou password invalidos");
        }

        @Test
        @DisplayName("Should fail login with non-existent email")
        void shouldFailLoginWithNonExistentEmail() {
            when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.empty());

            AuthResponse response = authService.login(loginRequest);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Email ou password invalidos");
        }

        @Test
        @DisplayName("Should fail login for inactive user")
        void shouldFailLoginForInactiveUser() {
            when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.empty());

            AuthResponse response = authService.login(loginRequest);

            assertThat(response.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            Optional<User> result = authService.getUserById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            Optional<User> result = authService.getUserByEmail("test@example.com");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

            assertThat(authService.existsByEmail("test@example.com")).isTrue();
            assertThat(authService.existsByEmail("nonexistent@example.com")).isFalse();
        }

        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            AuthResponse response = authService.deactivateUser(1L);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).contains("Conta desativada com sucesso");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail deactivation for non-existent user")
        void shouldFailDeactivationForNonExistentUser() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            AuthResponse response = authService.deactivateUser(999L);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Utilizador nao encontrado");
        }
    }

    // Helper method to hash password the same way as the service
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
