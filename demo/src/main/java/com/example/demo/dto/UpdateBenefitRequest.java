package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBenefitRequest {

    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Min(value = 1, message = "Points required must be at least 1")
    private Integer pointsRequired;

    private String provider;

    private String imageUrl;
}
