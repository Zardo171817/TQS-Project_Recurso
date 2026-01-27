package com.example.demo.dto;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitResponse {

    private Long id;
    private String name;
    private String description;
    private Integer pointsRequired;
    private BenefitCategory category;
    private String provider;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;

    public static BenefitResponse fromEntity(Benefit benefit) {
        if (benefit == null) {
            return null;
        }
        BenefitResponse response = new BenefitResponse();
        response.setId(benefit.getId());
        response.setName(benefit.getName());
        response.setDescription(benefit.getDescription());
        response.setPointsRequired(benefit.getPointsRequired());
        response.setCategory(benefit.getCategory());
        response.setProvider(benefit.getProvider());
        response.setImageUrl(benefit.getImageUrl());
        response.setActive(benefit.getActive());
        response.setCreatedAt(benefit.getCreatedAt());
        return response;
    }
}
