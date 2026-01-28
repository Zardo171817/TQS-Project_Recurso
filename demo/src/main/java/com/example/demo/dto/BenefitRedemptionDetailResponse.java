package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitRedemptionDetailResponse {

    private Long benefitId;
    private String benefitName;
    private String benefitDescription;
    private Integer pointsRequired;
    private String provider;
    private Boolean active;
    private Long totalRedemptions;
    private Long totalPointsRedeemed;
}
