package com.example.demo.entity;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationEntityTest {

    @Test
    void testVolunteerAllArgsConstructor() {
        Volunteer volunteer = new Volunteer(1L, "John Doe", "john@test.com", "123456789", "Java, Python");

        assertEquals(1L, volunteer.getId());
        assertEquals("John Doe", volunteer.getName());
        assertEquals("john@test.com", volunteer.getEmail());
        assertEquals("123456789", volunteer.getPhone());
        assertEquals("Java, Python", volunteer.getSkills());
    }

    @Test
    void testApplicationAllArgsConstructor() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opp");
        LocalDateTime now = LocalDateTime.now();

        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.ACCEPTED, "Motivation", now);

        assertEquals(1L, application.getId());
        assertEquals(volunteer, application.getVolunteer());
        assertEquals(opportunity, application.getOpportunity());
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        assertEquals("Motivation", application.getMotivation());
        assertEquals(now, application.getAppliedAt());
    }

    @Test
    void testApplicationOnCreateSetsDefaults() {
        Application application = new Application();
        assertNull(application.getAppliedAt());

        application.onCreate();

        assertNotNull(application.getAppliedAt());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void testApplicationOnCreatePreservesExistingStatus() {
        Application application = new Application();
        application.setStatus(ApplicationStatus.ACCEPTED);

        application.onCreate();

        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        assertNotNull(application.getAppliedAt());
    }

    @Test
    void testCreateApplicationRequestAllArgsConstructor() {
        CreateApplicationRequest request = new CreateApplicationRequest(
                1L, "John", "john@test.com", "999888777", "Skills", "I want to help"
        );

        assertEquals(1L, request.getOpportunityId());
        assertEquals("John", request.getVolunteerName());
        assertEquals("john@test.com", request.getVolunteerEmail());
        assertEquals("999888777", request.getVolunteerPhone());
        assertEquals("Skills", request.getVolunteerSkills());
        assertEquals("I want to help", request.getMotivation());
    }

    @Test
    void testApplicationResponseAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(
                1L, 2L, "John", "john@test.com", 3L, "Opp Title",
                ApplicationStatus.PENDING, "Motivation", now
        );

        assertEquals(1L, response.getId());
        assertEquals(2L, response.getVolunteerId());
        assertEquals("John", response.getVolunteerName());
        assertEquals("john@test.com", response.getVolunteerEmail());
        assertEquals(3L, response.getOpportunityId());
        assertEquals("Opp Title", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
        assertEquals("Motivation", response.getMotivation());
        assertEquals(now, response.getAppliedAt());
    }

    @Test
    void testApplicationResponseFromEntity() {
        Volunteer volunteer = new Volunteer(1L, "Vol Name", "vol@test.com", "123", "Java");
        Opportunity opportunity = new Opportunity();
        opportunity.setId(2L);
        opportunity.setTitle("Opp Title");

        Application application = new Application();
        application.setId(10L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("My motivation");
        application.setAppliedAt(LocalDateTime.of(2026, 1, 24, 10, 0));

        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getVolunteerId());
        assertEquals("Vol Name", response.getVolunteerName());
        assertEquals("vol@test.com", response.getVolunteerEmail());
        assertEquals(2L, response.getOpportunityId());
        assertEquals("Opp Title", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        assertEquals("My motivation", response.getMotivation());
    }

    // Testes para métodos Lombok (equals, hashCode, toString)
    @Test
    void testVolunteerEqualsAndHashCode() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v3 = new Volunteer(2L, "Jane", "jane@test.com", "456", "Python");

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertNotEquals(v1, null);
        assertNotEquals(v1, "string");
    }

    @Test
    void testVolunteerToString() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        String str = volunteer.toString();

        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
    }

    @Test
    void testApplicationEqualsAndHashCode() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a3 = new Application(2L, volunteer, opportunity, ApplicationStatus.ACCEPTED, "Other", now);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, a3);
    }

    @Test
    void testCreateApplicationRequestEqualsAndHashCode() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r3 = new CreateApplicationRequest(2L, "Jane", "jane@test.com", "456", "Python", "Other");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testApplicationResponseEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r3 = new ApplicationResponse(2L, 3L, "Jane", "jane@test.com", 4L, "Other", ApplicationStatus.ACCEPTED, "Other", now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testApplicationStatusValues() {
        ApplicationStatus[] values = ApplicationStatus.values();
        assertEquals(3, values.length);
        assertEquals(ApplicationStatus.PENDING, ApplicationStatus.valueOf("PENDING"));
        assertEquals(ApplicationStatus.ACCEPTED, ApplicationStatus.valueOf("ACCEPTED"));
        assertEquals(ApplicationStatus.REJECTED, ApplicationStatus.valueOf("REJECTED"));
    }
}
