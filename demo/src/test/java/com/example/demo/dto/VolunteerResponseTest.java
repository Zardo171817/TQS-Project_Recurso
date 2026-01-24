package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VolunteerResponseTest {

    @Test
    void whenFromEntity_thenMapAllFields() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("test@test.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");
        volunteer.setTotalPoints(100);

        VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals("Java, Python", response.getSkills());
        assertEquals(100, response.getTotalPoints());
    }

    @Test
    void whenCreateWithNoArgsConstructor_thenFieldsAreNull() {
        VolunteerResponse response = new VolunteerResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getTotalPoints());
    }

    @Test
    void whenSettersUsed_thenFieldsAreUpdated() {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(3L);
        response.setName("Updated Name");
        response.setEmail("updated@test.com");
        response.setTotalPoints(200);

        assertEquals(3L, response.getId());
        assertEquals("Updated Name", response.getName());
        assertEquals(200, response.getTotalPoints());
    }
}
