package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOpportunityRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotBlank(message = "Skills are required")
    @Size(min = 3, max = 200, message = "Skills must be between 3 and 200 characters")
    private String skills;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration;

    @NotNull(message = "Vacancies is required")
    @Positive(message = "Vacancies must be positive")
    private Integer vacancies;

    @NotNull(message = "Points are required")
    @PositiveOrZero(message = "Points must be zero or positive")
    private Integer points;

    @NotNull(message = "Promoter ID is required")
    private Long promoterId;
}
