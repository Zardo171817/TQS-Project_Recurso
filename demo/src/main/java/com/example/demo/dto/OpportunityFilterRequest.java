package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityFilterRequest {

    private String category;
    private String skills;
    private Integer minDuration;
    private Integer maxDuration;
}
