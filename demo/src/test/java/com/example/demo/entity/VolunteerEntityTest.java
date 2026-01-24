package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VolunteerEntityTest {

    @Test
    void whenCreateVolunteer_thenFieldsAreSet() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("test@test.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");
        volunteer.setTotalPoints(100);

        assertEquals(1L, volunteer.getId());
        assertEquals("Test Volunteer", volunteer.getName());
        assertEquals("test@test.com", volunteer.getEmail());
        assertEquals("123456789", volunteer.getPhone());
        assertEquals("Java, Python", volunteer.getSkills());
        assertEquals(100, volunteer.getTotalPoints());
    }

    @Test
    void whenCreateVolunteerWithAllArgsConstructor_thenFieldsAreSet() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", "123", "Java", 50);

        assertEquals(1L, volunteer.getId());
        assertEquals("Test", volunteer.getName());
        assertEquals(50, volunteer.getTotalPoints());
    }

    @Test
    void whenTwoVolunteersHaveSameData_thenEqualsReturnsTrue() {
        Volunteer volunteer1 = new Volunteer(1L, "Test", "test@test.com", "123", "Java", 100);
        Volunteer volunteer2 = new Volunteer(1L, "Test", "test@test.com", "123", "Java", 100);

        assertEquals(volunteer1, volunteer2);
        assertEquals(volunteer1.hashCode(), volunteer2.hashCode());
    }

    @Test
    void whenToString_thenContainsAllFields() {
        Volunteer volunteer = new Volunteer(1L, "Test Volunteer", "test@test.com", "123456", "Java", 200);

        String toString = volunteer.toString();

        assertTrue(toString.contains("Test Volunteer"));
        assertTrue(toString.contains("test@test.com"));
    }
}
