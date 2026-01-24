package com.example.demo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateOpportunityRequest extends BaseOpportunityRequest {

    public UpdateOpportunityRequest(String title, String description, String skills,
                                    String category, Integer duration, Integer vacancies,
                                    Integer points) {
        super(title, description, skills, category, duration, vacancies, points);
    }
}
