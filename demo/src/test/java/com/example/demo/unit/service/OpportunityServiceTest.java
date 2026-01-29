package com.example.demo.unit.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.OpportunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpportunityService Unit Tests")
class OpportunityServiceTest {

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

    private Promoter promoter;
    private Opportunity opportunity;
    private CreateOpportunityRequest createRequest;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setOrganization("Test Org");

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Beach Cleanup");
        opportunity.setDescription("Clean the beach");
        opportunity.setSkills("teamwork");
        opportunity.setCategory("Environment");
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(50);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateOpportunityRequest();
        createRequest.setTitle("Beach Cleanup");
        createRequest.setDescription("Clean the beach");
        createRequest.setSkills("teamwork");
        createRequest.setCategory("Environment");
        createRequest.setDuration(4);
        createRequest.setVacancies(10);
        createRequest.setPoints(50);
        createRequest.setPromoterId(1L);
    }

    @Test
    @DisplayName("Should create opportunity successfully")
    void shouldCreateOpportunitySuccessfully() {
        when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> {
            Opportunity o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });

        OpportunityResponse response = opportunityService.createOpportunity(createRequest);

        assertThat(response.getTitle()).isEqualTo("Beach Cleanup");
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    @DisplayName("Should get opportunity by ID")
    void shouldGetOpportunityById() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existent opportunity")
    void shouldThrowExceptionForNonExistent() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> opportunityService.getOpportunityById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get all opportunities")
    void shouldGetAllOpportunities() {
        when(opportunityRepository.findAll()).thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses = opportunityService.getAllOpportunities();

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should get opportunities by promoter")
    void shouldGetOpportunitiesByPromoter() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterId(1L)).thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByPromoter(1L);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should delete opportunity successfully")
    void shouldDeleteOpportunitySuccessfully() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);

        opportunityService.deleteOpportunity(1L);

        verify(opportunityRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should conclude opportunity and award points")
    void shouldConcludeOpportunityAndAwardPoints() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John");
        volunteer.setEmail("john@example.com");
        volunteer.setTotalPoints(0);

        Application application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setParticipationConfirmed(false);
        application.setAppliedAt(LocalDateTime.now());

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.singletonList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

        assertThat(response.getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        assertThat(response.getTotalPointsAwarded()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should fail if non-owner tries to conclude")
    void shouldFailIfNonOwnerTriesToConclude() {
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(999L);
        request.setApplicationIds(Collections.emptyList());

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));

        assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                .isInstanceOf(IllegalStateException.class);
    }
}
