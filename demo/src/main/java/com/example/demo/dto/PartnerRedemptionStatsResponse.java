package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRedemptionStatsResponse {

    private String provider;
    private Integer totalBenefits;
    private Long totalRedemptions;
    private Long totalPointsRedeemed;
    private List<BenefitRedemptionDetailResponse> benefitDetails;
    private List<RedemptionResponse> recentRedemptions;
}
