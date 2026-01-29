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
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration when email exists")
    void shouldFailRegistrationWhenEmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.isSuccess()).isFalse();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        user.setPassword(hashPassword("password123"));
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    void shouldFailLoginWithWrongPassword() {
        user.setPassword(hashPassword("differentPassword"));
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("Should fail login with non-existent email")
    void shouldFailLoginWithNonExistentEmail() {
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.empty());

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("Should get user by ID and email")
    void shouldGetUserByIdAndEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThat(authService.getUserById(1L)).isPresent();
        assertThat(authService.getUserByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.deactivateUser(1L);

        assertThat(response.isSuccess()).isTrue();
        verify(userRepository).save(any(User.class));
    }

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
