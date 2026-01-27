package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPointsRequest {

    @NotNull(message = "Volunteer ID is required")
    private Long volunteerId;

    @NotNull(message = "Benefit ID is required")
    private Long benefitId;
}
