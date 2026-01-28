package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String skills;
    private String interests;
    private String availability;
    private String bio;
    private Integer totalPoints;
    private LocalDateTime profileCreatedAt;
    private LocalDateTime profileUpdatedAt;

    public static VolunteerProfileResponse fromEntity(Volunteer volunteer) {
        VolunteerProfileResponse response = new VolunteerProfileResponse();
        response.setId(volunteer.getId());
        response.setName(volunteer.getName());
        response.setEmail(volunteer.getEmail());
        response.setPhone(volunteer.getPhone());
        response.setSkills(volunteer.getSkills());
        response.setInterests(volunteer.getInterests());
        response.setAvailability(volunteer.getAvailability());
        response.setBio(volunteer.getBio());
        response.setTotalPoints(volunteer.getTotalPoints());
        response.setProfileCreatedAt(volunteer.getProfileCreatedAt());
        response.setProfileUpdatedAt(volunteer.getProfileUpdatedAt());
        return response;
    }
}
