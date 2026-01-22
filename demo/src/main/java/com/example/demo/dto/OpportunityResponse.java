package com.example.demo.dto;

import com.example.demo.entity.Opportunity;

import java.time.LocalDateTime;

public class OpportunityResponse {

    private Long id;
    private String title;
    private String description;
    private String skills;
    private Integer duration;
    private Integer vacancies;
    private Integer points;
    private Long promoterId;
    private String promoterName;
    private LocalDateTime createdAt;

    public OpportunityResponse() {
    }

    public OpportunityResponse(Long id, String title, String description, String skills, Integer duration,
                              Integer vacancies, Integer points, Long promoterId, String promoterName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.duration = duration;
        this.vacancies = vacancies;
        this.points = points;
        this.promoterId = promoterId;
        this.promoterName = promoterName;
        this.createdAt = createdAt;
    }

    public static OpportunityResponse fromEntity(Opportunity opportunity) {
        OpportunityResponse response = new OpportunityResponse();
        response.setId(opportunity.getId());
        response.setTitle(opportunity.getTitle());
        response.setDescription(opportunity.getDescription());
        response.setSkills(opportunity.getSkills());
        response.setDuration(opportunity.getDuration());
        response.setVacancies(opportunity.getVacancies());
        response.setPoints(opportunity.getPoints());
        response.setPromoterId(opportunity.getPromoter().getId());
        response.setPromoterName(opportunity.getPromoter().getName());
        response.setCreatedAt(opportunity.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPromoterName() {
        return promoterName;
    }

    public void setPromoterName(String promoterName) {
        this.promoterName = promoterName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
