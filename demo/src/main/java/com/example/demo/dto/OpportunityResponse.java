package com.example.demo.dto;

import com.example.demo.entity.Opportunity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {

    private Long id;
    private String title;
    private String description;
    private String skills;
    private String category;
    private Integer duration;
    private Integer vacancies;
    private Integer points;
    private Long promoterId;
    private String promoterName;
    private LocalDateTime createdAt;

    public static OpportunityResponse fromEntity(Opportunity opportunity) {
        OpportunityResponse response = new OpportunityResponse();
        response.setId(opportunity.getId());
        response.setTitle(opportunity.getTitle());
        response.setDescription(opportunity.getDescription());
        response.setSkills(opportunity.getSkills());
        response.setCategory(opportunity.getCategory());
        response.setDuration(opportunity.getDuration());
        response.setVacancies(opportunity.getVacancies());
        response.setPoints(opportunity.getPoints());
        response.setPromoterId(opportunity.getPromoter().getId());
        response.setPromoterName(opportunity.getPromoter().getName());
        response.setCreatedAt(opportunity.getCreatedAt());
        return response;
    }
}
