package com.example.demo.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VolunteerEntityTest {

    // Feature: Ver Candidaturas Voluntario - Entity tests
    @Test
    void whenCreateVolunteer_thenFieldsAreSet() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("test@test.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");

        assertEquals(1L, volunteer.getId());
        assertEquals("Test Volunteer", volunteer.getName());
        assertEquals("test@test.com", volunteer.getEmail());
        assertEquals("123456789", volunteer.getPhone());
        assertEquals("Java, Python", volunteer.getSkills());
    }

    @Test
    void whenCreateVolunteerWithAllArgsConstructor_thenFieldsAreSet() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", "123", "Java");

        assertEquals(1L, volunteer.getId());
        assertEquals("Test", volunteer.getName());
        assertEquals("test@test.com", volunteer.getEmail());
        assertEquals("123", volunteer.getPhone());
        assertEquals("Java", volunteer.getSkills());
    }

    @Test
    void whenCreateVolunteerWithNoArgsConstructor_thenFieldsAreNull() {
        Volunteer volunteer = new Volunteer();

        assertNull(volunteer.getId());
        assertNull(volunteer.getName());
        assertNull(volunteer.getEmail());
        assertNull(volunteer.getPhone());
        assertNull(volunteer.getSkills());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "user@domain.org", "volunteer@charity.com"})
    void whenSetEmail_thenEmailIsStored(String email) {
        Volunteer volunteer = new Volunteer();
        volunteer.setEmail(email);

        assertEquals(email, volunteer.getEmail());
    }

    @ParameterizedTest
    @CsvSource({
        "Java, 'Java'",
        "'Java, Python', 'Java, Python'",
        "'React, Node.js, TypeScript', 'React, Node.js, TypeScript'"
    })
    void whenSetSkills_thenSkillsAreStored(String input, String expected) {
        Volunteer volunteer = new Volunteer();
        volunteer.setSkills(input);

        assertEquals(expected, volunteer.getSkills());
    }

    @ParameterizedTest
    @NullSource
    void whenSetNullPhone_thenPhoneIsNull(String phone) {
        Volunteer volunteer = new Volunteer();
        volunteer.setPhone(phone);

        assertNull(volunteer.getPhone());
    }

    @ParameterizedTest
    @NullSource
    void whenSetNullSkills_thenSkillsIsNull(String skills) {
        Volunteer volunteer = new Volunteer();
        volunteer.setSkills(skills);

        assertNull(volunteer.getSkills());
    }

    @Test
    void whenTwoVolunteersHaveSameData_thenEqualsReturnsTrue() {
        Volunteer volunteer1 = new Volunteer(1L, "Test", "test@test.com", "123", "Java");
        Volunteer volunteer2 = new Volunteer(1L, "Test", "test@test.com", "123", "Java");

        assertEquals(volunteer1, volunteer2);
        assertEquals(volunteer1.hashCode(), volunteer2.hashCode());
    }

    @Test
    void whenTwoVolunteersHaveDifferentId_thenEqualsReturnsFalse() {
        Volunteer volunteer1 = new Volunteer(1L, "Test", "test@test.com", "123", "Java");
        Volunteer volunteer2 = new Volunteer(2L, "Test", "test@test.com", "123", "Java");

        assertNotEquals(volunteer1, volunteer2);
    }

    @Test
    void whenToString_thenContainsAllFields() {
        Volunteer volunteer = new Volunteer(1L, "Test Volunteer", "test@test.com", "123456", "Java");

        String toString = volunteer.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Volunteer"));
        assertTrue(toString.contains("test@test.com"));
    }

    @Test
    void whenSetName_thenNameIsStored() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("New Name");

        assertEquals("New Name", volunteer.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "+351912345678", "00351912345678"})
    void whenSetPhone_thenPhoneIsStored(String phone) {
        Volunteer volunteer = new Volunteer();
        volunteer.setPhone(phone);

        assertEquals(phone, volunteer.getPhone());
    }

    @Test
    void whenCompareWithNull_thenNotEqual() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", "123", "Java");

        assertNotEquals(null, volunteer);
    }

    @Test
    void whenCompareWithSameInstance_thenEqual() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", "123", "Java");

        assertEquals(volunteer, volunteer);
    }

    @Test
    void whenCompareWithDifferentClass_thenNotEqual() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", "123", "Java");
        String notVolunteer = "Not a volunteer";

        assertNotEquals(volunteer, notVolunteer);
    }
}
