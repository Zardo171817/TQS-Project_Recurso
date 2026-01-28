package com.example.demo.dto;

import com.example.demo.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private UserType userType;
    private String message;
    private boolean success;

    public static AuthResponse success(Long id, String name, String email, UserType userType, String message) {
        return new AuthResponse(id, name, email, userType, message, true);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(null, null, null, null, message, false);
    }
}
