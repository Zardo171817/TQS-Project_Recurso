package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerPointsResponse {

    private Long id;
    private String name;
    private String email;
    private Integer totalPoints;

    public static VolunteerPointsResponse fromEntity(Volunteer volunteer) {
        VolunteerPointsResponse response = new VolunteerPointsResponse();
        response.setId(volunteer.getId());
        response.setName(volunteer.getName());
        response.setEmail(volunteer.getEmail());
        response.setTotalPoints(volunteer.getTotalPoints());
        return response;
    }
}
