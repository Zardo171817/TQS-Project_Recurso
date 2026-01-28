package com.example.demo.dto;

import com.example.demo.entity.Promoter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoterProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String organization;
    private String description;
    private String phone;
    private String website;
    private String address;
    private String logoUrl;
    private String organizationType;
    private String areaOfActivity;
    private String foundedYear;
    private String numberOfEmployees;
    private String socialMedia;
    private LocalDateTime profileCreatedAt;
    private LocalDateTime profileUpdatedAt;

    public static PromoterProfileResponse fromEntity(Promoter promoter) {
        PromoterProfileResponse response = new PromoterProfileResponse();
        response.setId(promoter.getId());
        response.setName(promoter.getName());
        response.setEmail(promoter.getEmail());
        response.setOrganization(promoter.getOrganization());
        response.setDescription(promoter.getDescription());
        response.setPhone(promoter.getPhone());
        response.setWebsite(promoter.getWebsite());
        response.setAddress(promoter.getAddress());
        response.setLogoUrl(promoter.getLogoUrl());
        response.setOrganizationType(promoter.getOrganizationType());
        response.setAreaOfActivity(promoter.getAreaOfActivity());
        response.setFoundedYear(promoter.getFoundedYear());
        response.setNumberOfEmployees(promoter.getNumberOfEmployees());
        response.setSocialMedia(promoter.getSocialMedia());
        response.setProfileCreatedAt(promoter.getProfileCreatedAt());
        response.setProfileUpdatedAt(promoter.getProfileUpdatedAt());
        return response;
    }
}
