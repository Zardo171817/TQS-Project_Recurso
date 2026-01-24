package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.ConcludeOpportunityResponse;
import com.example.demo.dto.ConfirmParticipationRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcludeOpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private PromoterRepository promoterRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    private Promoter testPromoter;
    private Opportunity testOpportunity;
    private Volunteer testVolunteer;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("promoter@test.com");
        testPromoter.setOrganization("Test Org");

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("Test Description");
        testOpportunity.setSkills("Java");
        testOpportunity.setCategory("Tech");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setStatus(OpportunityStatus.OPEN);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());

        testVolunteer = new Volunteer();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");
        testVolunteer.setTotalPoints(0);

        testApplication = new Application();
        testApplication.setId(1L);
        testApplication.setVolunteer(testVolunteer);
        testApplication.setOpportunity(testOpportunity);
        testApplication.setStatus(ApplicationStatus.ACCEPTED);
        testApplication.setParticipationConfirmed(false);
        testApplication.setPointsAwarded(0);
        testApplication.setAppliedAt(LocalDateTime.now());
    }

    @Test
    void whenConcludeOpportunity_thenSuccess() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(testVolunteer);
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getOpportunityId());
        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
        assertEquals(1, response.getTotalParticipantsConfirmed());
        assertEquals(100, response.getTotalPointsAwarded());
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    void whenConcludeOpportunityNotFound_thenThrowException() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.concludeOpportunity(999L, request));
    }

    @Test
    void whenConcludeOpportunityWrongPromoter_thenThrowException() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(999L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.concludeOpportunity(1L, request));
    }

    @Test
    void whenConcludeAlreadyConcludedOpportunity_thenThrowException() {
        testOpportunity.setStatus(OpportunityStatus.CONCLUDED);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.concludeOpportunity(1L, request));
    }

    @Test
    void whenConcludeWithNonAcceptedApplication_thenThrowException() {
        testApplication.setStatus(ApplicationStatus.PENDING);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.concludeOpportunity(1L, request));
    }

    @Test
    void whenConcludeWithApplicationFromDifferentOpportunity_thenThrowException() {
        Opportunity otherOpportunity = new Opportunity();
        otherOpportunity.setId(2L);
        otherOpportunity.setPromoter(testPromoter);
        testApplication.setOpportunity(otherOpportunity);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.concludeOpportunity(1L, request));
    }

    @Test
    void whenConcludeWithMultipleApplications_thenSuccess() {
        Volunteer secondVolunteer = new Volunteer();
        secondVolunteer.setId(2L);
        secondVolunteer.setName("Second Volunteer");
        secondVolunteer.setEmail("second@test.com");
        secondVolunteer.setTotalPoints(50);

        Application secondApplication = new Application();
        secondApplication.setId(2L);
        secondApplication.setVolunteer(secondVolunteer);
        secondApplication.setOpportunity(testOpportunity);
        secondApplication.setStatus(ApplicationStatus.ACCEPTED);
        secondApplication.setParticipationConfirmed(false);
        secondApplication.setPointsAwarded(0);
        secondApplication.setAppliedAt(LocalDateTime.now());

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L, 2L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.findById(2L)).thenReturn(Optional.of(secondApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertNotNull(response);
        assertEquals(2, response.getTotalParticipantsConfirmed());
        assertEquals(200, response.getTotalPointsAwarded());
    }

    @Test
    void whenConcludeSkipsAlreadyConfirmed_thenSuccess() {
        testApplication.setParticipationConfirmed(true);
        testApplication.setPointsAwarded(100);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertNotNull(response);
        assertEquals(0, response.getTotalParticipantsConfirmed());
        assertEquals(0, response.getTotalPointsAwarded());
    }

    @Test
    void whenConfirmParticipation_thenSuccess() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(testVolunteer);

        ApplicationResponse response = opportunityService.confirmParticipation(1L, 1L);

        assertNotNull(response);
        verify(applicationRepository).save(any(Application.class));
        verify(volunteerRepository).save(any(Volunteer.class));
    }

    @Test
    void whenConfirmParticipationNotFound_thenThrowException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.confirmParticipation(999L, 1L));
    }

    @Test
    void whenConfirmParticipationWrongPromoter_thenThrowException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.confirmParticipation(1L, 999L));
    }

    @Test
    void whenConfirmParticipationNotAccepted_thenThrowException() {
        testApplication.setStatus(ApplicationStatus.PENDING);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.confirmParticipation(1L, 1L));
    }

    @Test
    void whenConfirmParticipationAlreadyConfirmed_thenThrowException() {
        testApplication.setParticipationConfirmed(true);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.confirmParticipation(1L, 1L));
    }

    @Test
    void whenGetOpportunitiesByStatus_thenReturnList() {
        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(OpportunityStatus.OPEN, responses.get(0).getStatus());
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatus_thenReturnList() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses =
            opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.OPEN);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatusNotFound_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.OPEN));
    }

    @Test
    void whenGetAcceptedApplicationsForOpportunity_thenReturnList() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByOpportunityIdAndStatus(1L, ApplicationStatus.ACCEPTED))
            .thenReturn(Arrays.asList(testApplication));

        List<ApplicationResponse> responses =
            opportunityService.getAcceptedApplicationsForOpportunity(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void whenGetAcceptedApplicationsForOpportunityNotFound_thenThrowException() {
        when(opportunityRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.getAcceptedApplicationsForOpportunity(999L));
    }

    @Test
    void whenCountConcludedOpportunitiesByPromoter_thenReturnCount() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
            .thenReturn(5L);

        long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

        assertEquals(5L, count);
    }

    @Test
    void whenCountConcludedOpportunitiesByPromoterNotFound_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.countConcludedOpportunitiesByPromoter(999L));
    }

    @Test
    void whenConcludeOpportunityWithEmptyApplicationList_thenConcludeWithNoParticipants() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.emptyList());

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertNotNull(response);
        assertEquals(0, response.getTotalParticipantsConfirmed());
        assertEquals(0, response.getTotalPointsAwarded());
        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
    }

    @Test
    void whenConcludeOpportunityApplicationNotFound_thenThrowException() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Arrays.asList(999L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.concludeOpportunity(1L, request));
    }

    @Test
    void whenConfirmParticipationRejectedApplication_thenThrowException() {
        testApplication.setStatus(ApplicationStatus.REJECTED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        assertThrows(IllegalStateException.class, () ->
            opportunityService.confirmParticipation(1L, 1L));
    }

    @Test
    void whenConcludeOpportunityUpdatesConcludedAt() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.emptyList());

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(i -> i.getArgument(0));

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertNotNull(response.getConcludedAt());
    }
}
