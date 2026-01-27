package com.example.demo.dto;

import com.example.demo.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsHistoryResponse {

    private Long applicationId;
    private Long opportunityId;
    private String opportunityTitle;
    private String opportunityCategory;
    private Integer pointsAwarded;
    private LocalDateTime confirmedAt;
    private String opportunityDescription;

    public static PointsHistoryResponse fromEntity(Application application) {
        PointsHistoryResponse response = new PointsHistoryResponse();
        response.setApplicationId(application.getId());
        response.setOpportunityId(application.getOpportunity().getId());
        response.setOpportunityTitle(application.getOpportunity().getTitle());
        response.setOpportunityCategory(application.getOpportunity().getCategory());
        response.setPointsAwarded(application.getPointsAwarded());
        response.setConfirmedAt(application.getConfirmedAt());
        response.setOpportunityDescription(application.getOpportunity().getDescription());
        return response;
    }
}
