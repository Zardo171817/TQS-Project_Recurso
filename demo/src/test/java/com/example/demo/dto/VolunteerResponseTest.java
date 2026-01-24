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

        VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals("Java, Python", response.getSkills());
    }

    @Test
    void whenFromEntityWithNullOptionalFields_thenMapCorrectly() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(2L);
        volunteer.setName("Minimal Volunteer");
        volunteer.setEmail("minimal@test.com");
        volunteer.setPhone(null);
        volunteer.setSkills(null);

        VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

        assertEquals(2L, response.getId());
        assertEquals("Minimal Volunteer", response.getName());
        assertEquals("minimal@test.com", response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getSkills());
    }

    @Test
    void whenCreateWithNoArgsConstructor_thenFieldsAreNull() {
        VolunteerResponse response = new VolunteerResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getSkills());
    }

    @Test
    void whenCreateWithAllArgsConstructor_thenFieldsAreSet() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("123", response.getPhone());
        assertEquals("Java", response.getSkills());
    }

    @Test
    void whenSettersUsed_thenFieldsAreUpdated() {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(3L);
        response.setName("Updated Name");
        response.setEmail("updated@test.com");
        response.setPhone("999888777");
        response.setSkills("React, Node.js");

        assertEquals(3L, response.getId());
        assertEquals("Updated Name", response.getName());
        assertEquals("updated@test.com", response.getEmail());
        assertEquals("999888777", response.getPhone());
        assertEquals("React, Node.js", response.getSkills());
    }
}
