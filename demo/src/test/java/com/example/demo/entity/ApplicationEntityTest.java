package com.example.demo.entity;

import com.example.demo.dto.ApplicationResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationEntityTest {

    @Test
    void testApplicationOnCreateSetsDefaults() {
        Application application = new Application();
        assertNull(application.getAppliedAt());

        application.onCreate();

        assertNotNull(application.getAppliedAt());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertFalse(application.getParticipationConfirmed());
        assertEquals(0, application.getPointsAwarded());
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
    void testApplicationResponseFromEntity() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Vol Name");
        volunteer.setEmail("vol@test.com");
        volunteer.setTotalPoints(100);

        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Org");

        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opp");
        opportunity.setDescription("Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(150);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        Application application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("I want to help");
        application.setAppliedAt(LocalDateTime.now());
        application.setParticipationConfirmed(true);
        application.setPointsAwarded(150);

        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(1L, response.getId());
        assertEquals("Vol Name", response.getVolunteerName());
        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        assertTrue(response.getParticipationConfirmed());
        assertEquals(150, response.getPointsAwarded());
        assertEquals(150, response.getOpportunityPoints());
    }
}
