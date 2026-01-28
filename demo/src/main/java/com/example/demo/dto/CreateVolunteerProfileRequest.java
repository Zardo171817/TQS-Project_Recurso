package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVolunteerProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Size(max = 500, message = "Skills must be at most 500 characters")
    private String skills;

    @Size(max = 500, message = "Interests must be at most 500 characters")
    private String interests;

    @Size(max = 500, message = "Availability must be at most 500 characters")
    private String availability;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    private String bio;
}
