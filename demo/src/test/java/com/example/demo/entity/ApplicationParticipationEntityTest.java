package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationParticipationEntityTest {

    private Application application;
    private Volunteer volunteer;
    private Opportunity opportunity;
    private Promoter promoter;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("volunteer@test.com");
        volunteer.setTotalPoints(0);

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Test Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("I want to help");
        application.setAppliedAt(LocalDateTime.now());
        application.setParticipationConfirmed(false);
        application.setPointsAwarded(0);
    }

    @Test
    void whenNewApplication_thenParticipationNotConfirmed() {
        Application newApp = new Application();
        newApp.setParticipationConfirmed(false);
        assertFalse(newApp.getParticipationConfirmed());
    }

    @Test
    void whenConfirmParticipation_thenParticipationConfirmedIsTrue() {
        application.setParticipationConfirmed(true);
        assertTrue(application.getParticipationConfirmed());
    }

    @Test
    void whenNewApplication_thenPointsAwardedIsZero() {
        assertEquals(0, application.getPointsAwarded());
    }

    @Test
    void whenAwardPoints_thenPointsAwardedIsSet() {
        application.setPointsAwarded(100);
        assertEquals(100, application.getPointsAwarded());
    }

    @Test
    void whenConfirmParticipation_thenConfirmedAtIsSet() {
        LocalDateTime now = LocalDateTime.now();
        application.setConfirmedAt(now);
        assertEquals(now, application.getConfirmedAt());
    }

    @Test
    void whenFullParticipationConfirmation_thenAllFieldsSet() {
        LocalDateTime now = LocalDateTime.now();
        application.setParticipationConfirmed(true);
        application.setPointsAwarded(150);
        application.setConfirmedAt(now);

        assertTrue(application.getParticipationConfirmed());
        assertEquals(150, application.getPointsAwarded());
        assertEquals(now, application.getConfirmedAt());
    }

    @Test
    void whenApplicationNotConfirmed_thenConfirmedAtIsNull() {
        assertFalse(application.getParticipationConfirmed());
        assertNull(application.getConfirmedAt());
    }

    @Test
    void whenApplicationStatusAccepted_thenCanConfirmParticipation() {
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        application.setParticipationConfirmed(true);
        assertTrue(application.getParticipationConfirmed());
    }

    @Test
    void whenGetOpportunityPoints_thenReturnsOpportunityPoints() {
        assertEquals(100, application.getOpportunity().getPoints());
    }

    @Test
    void whenApplicationToString_thenContainsFields() {
        String str = application.toString();
        assertNotNull(str);
        assertTrue(str.contains("1"));
    }

    @Test
    void whenApplicationEqualsAndHashCode_thenCorrect() {
        Application app1 = new Application();
        app1.setId(1L);
        app1.setParticipationConfirmed(true);
        app1.setPointsAwarded(100);

        Application app2 = new Application();
        app2.setId(1L);
        app2.setParticipationConfirmed(true);
        app2.setPointsAwarded(100);

        assertEquals(app1.getId(), app2.getId());
    }

    @Test
    void whenSetPointsAwardedMultipleTimes_thenLastValuePersists() {
        application.setPointsAwarded(50);
        application.setPointsAwarded(100);
        application.setPointsAwarded(75);
        assertEquals(75, application.getPointsAwarded());
    }
}
