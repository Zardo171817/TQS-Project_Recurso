package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmParticipationRequest {

    @NotNull(message = "Promoter ID is required")
    private Long promoterId;

    @NotNull(message = "Application IDs are required")
    private List<Long> applicationIds;
}
