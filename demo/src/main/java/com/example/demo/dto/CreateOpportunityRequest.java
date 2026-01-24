package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateOpportunityRequest extends BaseOpportunityRequest {

    @NotNull(message = "Promoter ID is required")
    private Long promoterId;

    public CreateOpportunityRequest(String title, String description, String skills,
                                    String category, Integer duration, Integer vacancies,
                                    Integer points, Long promoterId) {
        super(title, description, skills, category, duration, vacancies, points);
        this.promoterId = promoterId;
    }
}
