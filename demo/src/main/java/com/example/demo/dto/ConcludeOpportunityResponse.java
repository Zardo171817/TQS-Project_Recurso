package com.example.demo.dto;

import com.example.demo.entity.OpportunityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConcludeOpportunityResponse {

    private Long opportunityId;
    private String opportunityTitle;
    private OpportunityStatus status;
    private LocalDateTime concludedAt;
    private Integer totalParticipantsConfirmed;
    private Integer totalPointsAwarded;
    private List<ParticipantSummary> confirmedParticipants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantSummary {
        private Long volunteerId;
        private String volunteerName;
        private String volunteerEmail;
        private Integer pointsAwarded;
        private Integer totalPoints;
    }
}
