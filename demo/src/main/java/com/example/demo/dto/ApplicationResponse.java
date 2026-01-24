package com.example.demo.dto;

import com.example.demo.entity.Application;
import com.example.demo.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private String volunteerEmail;
    private Long opportunityId;
    private String opportunityTitle;
    private ApplicationStatus status;
    private String motivation;
    private LocalDateTime appliedAt;

    public static ApplicationResponse fromEntity(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setVolunteerId(application.getVolunteer().getId());
        response.setVolunteerName(application.getVolunteer().getName());
        response.setVolunteerEmail(application.getVolunteer().getEmail());
        response.setOpportunityId(application.getOpportunity().getId());
        response.setOpportunityTitle(application.getOpportunity().getTitle());
        response.setStatus(application.getStatus());
        response.setMotivation(application.getMotivation());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }
}
