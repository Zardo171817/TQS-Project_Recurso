package com.example.demo.dto;

import com.example.demo.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationResponseDtoTest {

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
        volunteer.setTotalPoints(100);

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Test Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(150);
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
    void whenFromEntityNotConfirmed_thenParticipationConfirmedIsFalse() {
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertFalse(response.getParticipationConfirmed());
        assertEquals(0, response.getPointsAwarded());
        assertNull(response.getConfirmedAt());
    }

    @Test
    void whenFromEntityConfirmed_thenParticipationConfirmedIsTrue() {
        LocalDateTime confirmedTime = LocalDateTime.now();
        application.setParticipationConfirmed(true);
        application.setPointsAwarded(150);
        application.setConfirmedAt(confirmedTime);

        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertTrue(response.getParticipationConfirmed());
        assertEquals(150, response.getPointsAwarded());
        assertEquals(confirmedTime, response.getConfirmedAt());
    }

    @Test
    void whenFromEntity_thenOpportunityPointsSet() {
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(150, response.getOpportunityPoints());
    }

    @Test
    void whenFromEntity_thenAllFieldsMapped() {
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(application.getId(), response.getId());
        assertEquals(volunteer.getId(), response.getVolunteerId());
        assertEquals(volunteer.getName(), response.getVolunteerName());
        assertEquals(volunteer.getEmail(), response.getVolunteerEmail());
        assertEquals(opportunity.getId(), response.getOpportunityId());
        assertEquals(opportunity.getTitle(), response.getOpportunityTitle());
        assertEquals(application.getStatus(), response.getStatus());
        assertEquals(application.getMotivation(), response.getMotivation());
        assertEquals(application.getAppliedAt(), response.getAppliedAt());
        assertEquals(application.getParticipationConfirmed(), response.getParticipationConfirmed());
        assertEquals(application.getPointsAwarded(), response.getPointsAwarded());
        assertEquals(opportunity.getPoints(), response.getOpportunityPoints());
    }

    @Test
    void whenApplicationResponseNoArgsConstructor_thenFieldsNull() {
        ApplicationResponse response = new ApplicationResponse();

        assertNull(response.getId());
        assertNull(response.getParticipationConfirmed());
        assertNull(response.getPointsAwarded());
    }

    @Test
    void whenApplicationResponseSetters_thenFieldsSet() {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);
        response.setParticipationConfirmed(true);
        response.setPointsAwarded(100);
        response.setConfirmedAt(LocalDateTime.now());
        response.setOpportunityPoints(150);

        assertEquals(1L, response.getId());
        assertTrue(response.getParticipationConfirmed());
        assertEquals(100, response.getPointsAwarded());
        assertNotNull(response.getConfirmedAt());
        assertEquals(150, response.getOpportunityPoints());
    }

    @Test
    void whenApplicationResponseEquals_thenCorrect() {
        ApplicationResponse r1 = ApplicationResponse.fromEntity(application);
        ApplicationResponse r2 = ApplicationResponse.fromEntity(application);

        assertEquals(r1.getId(), r2.getId());
        assertEquals(r1.getParticipationConfirmed(), r2.getParticipationConfirmed());
    }

    @Test
    void whenApplicationResponseToString_thenContainsFields() {
        ApplicationResponse response = ApplicationResponse.fromEntity(application);
        String str = response.toString();

        assertTrue(str.contains("Test Volunteer"));
        assertTrue(str.contains("ACCEPTED"));
    }

    @Test
    void whenDifferentOpportunityPoints_thenReflectedInResponse() {
        opportunity.setPoints(500);
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(500, response.getOpportunityPoints());
    }

    @Test
    void whenApplicationPending_thenStatusIsPending() {
        application.setStatus(ApplicationStatus.PENDING);
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(ApplicationStatus.PENDING, response.getStatus());
    }

    @Test
    void whenApplicationRejected_thenStatusIsRejected() {
        application.setStatus(ApplicationStatus.REJECTED);
        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(ApplicationStatus.REJECTED, response.getStatus());
    }
}
