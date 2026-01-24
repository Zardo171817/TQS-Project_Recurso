package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotNull(message = "Opportunity ID is required")
    private Long opportunityId;

    @NotBlank(message = "Name is required")
    private String volunteerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String volunteerEmail;

    private String volunteerPhone;

    private String volunteerSkills;

    @Size(max = 500, message = "Motivation must be at most 500 characters")
    private String motivation;
}
