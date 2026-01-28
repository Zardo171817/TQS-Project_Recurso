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
public class CreatePromoterProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Organization is required")
    @Size(min = 2, max = 200, message = "Organization must be between 2 and 200 characters")
    private String organization;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Size(max = 255, message = "Website must be at most 255 characters")
    private String website;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @Size(max = 255, message = "Logo URL must be at most 255 characters")
    private String logoUrl;

    @Size(max = 100, message = "Organization type must be at most 100 characters")
    private String organizationType;

    @Size(max = 500, message = "Area of activity must be at most 500 characters")
    private String areaOfActivity;

    @Size(max = 100, message = "Founded year must be at most 100 characters")
    private String foundedYear;

    @Size(max = 50, message = "Number of employees must be at most 50 characters")
    private String numberOfEmployees;

    @Size(max = 500, message = "Social media must be at most 500 characters")
    private String socialMedia;
}
