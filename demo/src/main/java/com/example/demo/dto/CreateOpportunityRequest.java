package com.example.demo.dto;

import jakarta.validation.constraints.*;

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

    public CreateOpportunityRequest() {
    }

    public CreateOpportunityRequest(String title, String description, String skills, Integer duration,
                                    Integer vacancies, Integer points, Long promoterId) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.duration = duration;
        this.vacancies = vacancies;
        this.points = points;
        this.promoterId = promoterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getVacancies() {
        return vacancies;
    }

    public void setVacancies(Integer vacancies) {
        this.vacancies = vacancies;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }
}
