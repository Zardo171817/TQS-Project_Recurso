package com.example.demo.dto;

import com.example.demo.entity.OpportunityStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ConcludeOpportunityDtoTest {

    @Test
    void whenCreateConcludeOpportunityRequest_thenFieldsSet() {
        ConcludeOpportunityRequest request = new ConcludeOpportunityRequest();
        request.setPromoterId(1L);

        assertEquals(1L, request.getPromoterId());
    }

    @Test
    void whenCreateConcludeOpportunityRequestWithAllArgs_thenFieldsSet() {
        ConcludeOpportunityRequest request = new ConcludeOpportunityRequest(1L);

        assertEquals(1L, request.getPromoterId());
    }

    @Test
    void whenCreateConfirmParticipationRequest_thenFieldsSet() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L, 2L, 3L));

        assertEquals(1L, request.getPromoterId());
        assertEquals(3, request.getApplicationIds().size());
    }

    @Test
    void whenCreateConfirmParticipationRequestWithAllArgs_thenFieldsSet() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));

        assertEquals(1L, request.getPromoterId());
        assertEquals(2, request.getApplicationIds().size());
    }

    @Test
    void whenCreateConfirmParticipationRequestWithEmptyList_thenFieldsSet() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.emptyList());

        assertEquals(1L, request.getPromoterId());
        assertTrue(request.getApplicationIds().isEmpty());
    }

    @Test
    void whenCreateConcludeOpportunityResponse_thenFieldsSet() {
        LocalDateTime now = LocalDateTime.now();

        ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
        response.setOpportunityId(1L);
        response.setOpportunityTitle("Test Opportunity");
        response.setStatus(OpportunityStatus.CONCLUDED);
        response.setConcludedAt(now);
        response.setTotalParticipantsConfirmed(5);
        response.setTotalPointsAwarded(500);

        assertEquals(1L, response.getOpportunityId());
        assertEquals("Test Opportunity", response.getOpportunityTitle());
        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
        assertEquals(now, response.getConcludedAt());
        assertEquals(5, response.getTotalParticipantsConfirmed());
        assertEquals(500, response.getTotalPointsAwarded());
    }

    @Test
    void whenCreateParticipantSummary_thenFieldsSet() {
        ConcludeOpportunityResponse.ParticipantSummary summary =
            new ConcludeOpportunityResponse.ParticipantSummary();
        summary.setVolunteerId(1L);
        summary.setVolunteerName("Test Volunteer");
        summary.setVolunteerEmail("volunteer@test.com");
        summary.setPointsAwarded(100);
        summary.setTotalPoints(500);

        assertEquals(1L, summary.getVolunteerId());
        assertEquals("Test Volunteer", summary.getVolunteerName());
        assertEquals("volunteer@test.com", summary.getVolunteerEmail());
        assertEquals(100, summary.getPointsAwarded());
        assertEquals(500, summary.getTotalPoints());
    }

    @Test
    void whenCreateParticipantSummaryWithAllArgs_thenFieldsSet() {
        ConcludeOpportunityResponse.ParticipantSummary summary =
            new ConcludeOpportunityResponse.ParticipantSummary(1L, "Test", "test@test.com", 100, 500);

        assertEquals(1L, summary.getVolunteerId());
        assertEquals("Test", summary.getVolunteerName());
        assertEquals("test@test.com", summary.getVolunteerEmail());
        assertEquals(100, summary.getPointsAwarded());
        assertEquals(500, summary.getTotalPoints());
    }

    @Test
    void whenConcludeOpportunityResponseWithParticipants_thenListCorrect() {
        ConcludeOpportunityResponse.ParticipantSummary p1 =
            new ConcludeOpportunityResponse.ParticipantSummary(1L, "V1", "v1@test.com", 100, 100);
        ConcludeOpportunityResponse.ParticipantSummary p2 =
            new ConcludeOpportunityResponse.ParticipantSummary(2L, "V2", "v2@test.com", 100, 200);

        ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
        response.setConfirmedParticipants(Arrays.asList(p1, p2));

        assertEquals(2, response.getConfirmedParticipants().size());
    }

    @Test
    void whenConcludeOpportunityRequestEquals_thenCorrect() {
        ConcludeOpportunityRequest r1 = new ConcludeOpportunityRequest(1L);
        ConcludeOpportunityRequest r2 = new ConcludeOpportunityRequest(1L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void whenConfirmParticipationRequestEquals_thenCorrect() {
        ConfirmParticipationRequest r1 = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));
        ConfirmParticipationRequest r2 = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void whenConcludeOpportunityResponseAllArgs_thenFieldsSet() {
        LocalDateTime now = LocalDateTime.now();
        ConcludeOpportunityResponse.ParticipantSummary summary =
            new ConcludeOpportunityResponse.ParticipantSummary(1L, "Test", "test@test.com", 100, 100);

        ConcludeOpportunityResponse response = new ConcludeOpportunityResponse(
            1L, "Test", OpportunityStatus.CONCLUDED, now, 1, 100, Arrays.asList(summary)
        );

        assertEquals(1L, response.getOpportunityId());
        assertEquals("Test", response.getOpportunityTitle());
        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
        assertEquals(now, response.getConcludedAt());
        assertEquals(1, response.getTotalParticipantsConfirmed());
        assertEquals(100, response.getTotalPointsAwarded());
        assertEquals(1, response.getConfirmedParticipants().size());
    }
}
