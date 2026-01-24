package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String skills;

    public static VolunteerResponse fromEntity(Volunteer volunteer) {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(volunteer.getId());
        response.setName(volunteer.getName());
        response.setEmail(volunteer.getEmail());
        response.setPhone(volunteer.getPhone());
        response.setSkills(volunteer.getSkills());
        return response;
    }
}
