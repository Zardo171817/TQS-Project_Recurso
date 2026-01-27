package com.example.demo.dto;

import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionResponse {

    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private String volunteerEmail;
    private Long benefitId;
    private String benefitName;
    private String benefitDescription;
    private String benefitProvider;
    private Integer pointsSpent;
    private RedemptionStatus status;
    private LocalDateTime redeemedAt;
    private Integer remainingPoints;

    public static RedemptionResponse fromEntity(Redemption redemption) {
        if (redemption == null) {
            return null;
        }
        RedemptionResponse response = new RedemptionResponse();
        response.setId(redemption.getId());
        response.setPointsSpent(redemption.getPointsSpent());
        response.setStatus(redemption.getStatus());
        response.setRedeemedAt(redemption.getRedeemedAt());

        if (redemption.getVolunteer() != null) {
            response.setVolunteerId(redemption.getVolunteer().getId());
            response.setVolunteerName(redemption.getVolunteer().getName());
            response.setVolunteerEmail(redemption.getVolunteer().getEmail());
            response.setRemainingPoints(redemption.getVolunteer().getTotalPoints());
        }

        if (redemption.getBenefit() != null) {
            response.setBenefitId(redemption.getBenefit().getId());
            response.setBenefitName(redemption.getBenefit().getName());
            response.setBenefitDescription(redemption.getBenefit().getDescription());
            response.setBenefitProvider(redemption.getBenefit().getProvider());
        }

        return response;
    }
}
