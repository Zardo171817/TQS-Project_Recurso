package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserType;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.error("Email ja registado");
        }

        String hashedPassword = hashPassword(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword,
                request.getUserType()
        );

        User savedUser = userRepository.save(user);

        return AuthResponse.success(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getUserType(),
                "Registo efetuado com sucesso"
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmailAndActiveTrue(request.getEmail());

        if (userOpt.isEmpty()) {
            return AuthResponse.error("Email ou password invalidos");
        }

        User user = userOpt.get();
        String hashedPassword = hashPassword(request.getPassword());

        if (!hashedPassword.equals(user.getPassword())) {
            return AuthResponse.error("Email ou password invalidos");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.success(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                "Login efetuado com sucesso"
        );
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public AuthResponse deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return AuthResponse.error("Utilizador nao encontrado");
        }

        User user = userOpt.get();
        user.setActive(false);
        userRepository.save(user);

        return AuthResponse.success(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                "Conta desativada com sucesso"
        );
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
