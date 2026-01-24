package com.example.demo.entity;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testApplicationStatusValues() {
        ApplicationStatus[] values = ApplicationStatus.values();
        assertEquals(3, values.length);
        assertEquals(ApplicationStatus.PENDING, ApplicationStatus.valueOf("PENDING"));
        assertEquals(ApplicationStatus.ACCEPTED, ApplicationStatus.valueOf("ACCEPTED"));
        assertEquals(ApplicationStatus.REJECTED, ApplicationStatus.valueOf("REJECTED"));
    }

    @Test
    void testVolunteerNoArgsConstructorAndSetters() {
        Volunteer volunteer = new Volunteer();

        volunteer.setId(15L);
        volunteer.setName("Pedro");
        volunteer.setEmail("pedro@test.com");
        volunteer.setPhone("987654321");
        volunteer.setSkills("Excel, Word");

        assertEquals(15L, volunteer.getId());
        assertEquals("Pedro", volunteer.getName());
        assertEquals("pedro@test.com", volunteer.getEmail());
        assertEquals("987654321", volunteer.getPhone());
        assertEquals("Excel, Word", volunteer.getSkills());
    }

    @Test
    void testApplicationNoArgsConstructorAndSetters() {
        Application application = new Application();
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        application.setId(100L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setMotivation("My reason");
        application.setAppliedAt(now);

        assertEquals(100L, application.getId());
        assertEquals(volunteer, application.getVolunteer());
        assertEquals(opportunity, application.getOpportunity());
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertEquals("My reason", application.getMotivation());
        assertEquals(now, application.getAppliedAt());
    }

    @Test
    void testApplicationDefaultStatus() {
        Application application = new Application();
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void testCreateApplicationRequestNoArgsConstructorAndSetters() {
        CreateApplicationRequest request = new CreateApplicationRequest();

        request.setOpportunityId(5L);
        request.setVolunteerName("Maria");
        request.setVolunteerEmail("maria@test.com");
        request.setVolunteerPhone("912345678");
        request.setVolunteerSkills("Communication, Leadership");
        request.setMotivation("I want to contribute");

        assertEquals(5L, request.getOpportunityId());
        assertEquals("Maria", request.getVolunteerName());
        assertEquals("maria@test.com", request.getVolunteerEmail());
        assertEquals("912345678", request.getVolunteerPhone());
        assertEquals("Communication, Leadership", request.getVolunteerSkills());
        assertEquals("I want to contribute", request.getMotivation());
    }

    @Test
    void testApplicationResponseNoArgsConstructorAndSetters() {
        ApplicationResponse response = new ApplicationResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(10L);
        response.setVolunteerId(20L);
        response.setVolunteerName("Carlos");
        response.setVolunteerEmail("carlos@test.com");
        response.setOpportunityId(30L);
        response.setOpportunityTitle("Help Event");
        response.setStatus(ApplicationStatus.ACCEPTED);
        response.setMotivation("Quero ajudar");
        response.setAppliedAt(now);

        assertEquals(10L, response.getId());
        assertEquals(20L, response.getVolunteerId());
        assertEquals("Carlos", response.getVolunteerName());
        assertEquals("carlos@test.com", response.getVolunteerEmail());
        assertEquals(30L, response.getOpportunityId());
        assertEquals("Help Event", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        assertEquals("Quero ajudar", response.getMotivation());
        assertEquals(now, response.getAppliedAt());
    }
}
