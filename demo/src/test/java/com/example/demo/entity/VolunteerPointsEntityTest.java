package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VolunteerPointsEntityTest {

    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("volunteer@test.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");
        volunteer.setTotalPoints(0);
    }

    @Test
    void whenNewVolunteer_thenTotalPointsIsZero() {
        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setTotalPoints(0);
        assertEquals(0, newVolunteer.getTotalPoints());
    }

    @Test
    void whenSetTotalPoints_thenTotalPointsIsSet() {
        volunteer.setTotalPoints(100);
        assertEquals(100, volunteer.getTotalPoints());
    }

    @Test
    void whenAddPoints_thenTotalPointsIncreases() {
        volunteer.setTotalPoints(50);
        volunteer.setTotalPoints(volunteer.getTotalPoints() + 100);
        assertEquals(150, volunteer.getTotalPoints());
    }

    @Test
    void whenVolunteerWithMultipleParticipations_thenPointsAccumulate() {
        volunteer.setTotalPoints(100);
        volunteer.setTotalPoints(volunteer.getTotalPoints() + 50);
        volunteer.setTotalPoints(volunteer.getTotalPoints() + 75);
        assertEquals(225, volunteer.getTotalPoints());
    }

    @Test
    void whenVolunteerHasNoPoints_thenTotalPointsIsZero() {
        assertEquals(0, volunteer.getTotalPoints());
    }

    @Test
    void whenSetNegativePoints_thenAccepted() {
        volunteer.setTotalPoints(-10);
        assertEquals(-10, volunteer.getTotalPoints());
    }

    @Test
    void whenVolunteerAllArgsConstructor_thenFieldsSet() {
        Volunteer v = new Volunteer(1L, "Name", "email@test.com", "123", "Skills", 500);
        assertEquals(1L, v.getId());
        assertEquals("Name", v.getName());
        assertEquals("email@test.com", v.getEmail());
        assertEquals("123", v.getPhone());
        assertEquals("Skills", v.getSkills());
        assertEquals(500, v.getTotalPoints());
    }

    @Test
    void whenVolunteerNoArgsConstructor_thenFieldsNullOrDefault() {
        Volunteer v = new Volunteer();
        assertNull(v.getId());
        assertNull(v.getName());
        assertNull(v.getEmail());
        assertEquals(0, v.getTotalPoints()); // totalPoints has default value of 0
    }

    @Test
    void whenVolunteerEqualsAndHashCode_thenCorrect() {
        Volunteer v1 = new Volunteer();
        v1.setId(1L);
        v1.setName("Test");
        v1.setEmail("test@test.com");
        v1.setTotalPoints(100);

        Volunteer v2 = new Volunteer();
        v2.setId(1L);
        v2.setName("Test");
        v2.setEmail("test@test.com");
        v2.setTotalPoints(100);

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void whenVolunteerToString_thenContainsFields() {
        String str = volunteer.toString();
        assertTrue(str.contains("Test Volunteer"));
        assertTrue(str.contains("volunteer@test.com"));
    }
}
