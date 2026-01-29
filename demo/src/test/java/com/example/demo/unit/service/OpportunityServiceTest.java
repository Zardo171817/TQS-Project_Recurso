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

    @Test
    @DisplayName("Given opportunity filter when filtering opportunities then return filtered list")
    void givenOpportunityFilter_whenFiltering_thenReturnFilteredList() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();
        filter.setCategory("Environment");

        when(opportunityRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunities(filter);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCategory()).isEqualTo("Environment");
    }

    @Test
    @DisplayName("Given filter params when filtering opportunities then return filtered list")
    void givenFilterParams_whenFiltering_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses =
                opportunityService.filterOpportunitiesByParams("Environment", "teamwork", 2, 6);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Given existing opportunities when getting all categories then return categories")
    void givenExistingOpportunities_whenGettingAllCategories_thenReturnCategories() {
        when(opportunityRepository.findAllCategories()).thenReturn(List.of("Environment", "Education"));

        List<String> categories = opportunityService.getAllCategories();

        assertThat(categories).hasSize(2);
        assertThat(categories).contains("Environment", "Education");
    }

    @Test
    @DisplayName("Given valid update request when updating opportunity then return updated opportunity")
    void givenValidUpdateRequest_whenUpdating_thenReturnUpdatedOpportunity() {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Beach Cleanup");
        updateRequest.setDescription("Updated description");
        updateRequest.setSkills("teamwork, leadership");
        updateRequest.setCategory("Environment");
        updateRequest.setDuration(5);
        updateRequest.setVacancies(15);
        updateRequest.setPoints(60);

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponse response = opportunityService.updateOpportunity(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    @DisplayName("Given non-existent ID when updating opportunity then throw exception")
    void givenNonExistentId_whenUpdating_thenThrowException() {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> opportunityService.updateOpportunity(999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given non-existent ID when deleting opportunity then throw exception")
    void givenNonExistentId_whenDeleting_thenThrowException() {
        when(opportunityRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> opportunityService.deleteOpportunity(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given already concluded opportunity when concluding then throw exception")
    void givenAlreadyConcluded_whenConcluding_thenThrowException() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.emptyList());

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));

        assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already concluded");
    }

    @Test
    @DisplayName("Given non-accepted application when concluding then throw exception")
    void givenNonAcceptedApplication_whenConcluding_thenThrowException() {
        Application application = new Application();
        application.setId(1L);
        application.setStatus(ApplicationStatus.PENDING);
        application.setOpportunity(opportunity);

        ConfirmParticipationRequest request = new ConfirmParticipationRequest();
        request.setPromoterId(1L);
        request.setApplicationIds(Collections.singletonList(1L));

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("accepted applications");
    }

    @Test
    @DisplayName("Given valid application when confirming participation then return confirmed application")
    void givenValidApplication_whenConfirmingParticipation_thenReturnConfirmedApplication() {
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

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

        ApplicationResponse response = opportunityService.confirmParticipation(1L, 1L);

        assertThat(response).isNotNull();
        verify(volunteerRepository).save(any(Volunteer.class));
    }

    @Test
    @DisplayName("Given non-owner when confirming participation then throw exception")
    void givenNonOwner_whenConfirmingParticipation_thenThrowException() {
        Application application = new Application();
        application.setId(1L);
        application.setOpportunity(opportunity);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("promoter who created");
    }

    @Test
    @DisplayName("Given non-accepted application when confirming participation then throw exception")
    void givenNonAcceptedApplication_whenConfirmingParticipation_thenThrowException() {
        Application application = new Application();
        application.setId(1L);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("accepted applications");
    }

    @Test
    @DisplayName("Given already confirmed application when confirming participation then throw exception")
    void givenAlreadyConfirmed_whenConfirmingParticipation_thenThrowException() {
        Application application = new Application();
        application.setId(1L);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setParticipationConfirmed(true);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already confirmed");
    }

    @Test
    @DisplayName("Given valid status when getting opportunities by status then return filtered list")
    void givenValidStatus_whenGettingOpportunitiesByStatus_thenReturnFilteredList() {
        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
                .thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Given valid promoter and status when getting opportunities then return filtered list")
    void givenValidPromoterAndStatus_whenGettingOpportunities_thenReturnFilteredList() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.OPEN))
                .thenReturn(Collections.singletonList(opportunity));

        List<OpportunityResponse> responses =
                opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.OPEN);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Given non-existent promoter when getting opportunities by status then throw exception")
    void givenNonExistentPromoter_whenGettingOpportunitiesByStatus_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.OPEN))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid opportunity ID when getting accepted applications then return applications")
    void givenValidOpportunityId_whenGettingAcceptedApplications_thenReturnApplications() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByOpportunityIdAndStatus(1L, ApplicationStatus.ACCEPTED))
                .thenReturn(Collections.emptyList());

        List<ApplicationResponse> responses = opportunityService.getAcceptedApplicationsForOpportunity(1L);

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Given non-existent opportunity when getting accepted applications then throw exception")
    void givenNonExistentOpportunity_whenGettingAcceptedApplications_thenThrowException() {
        when(opportunityRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> opportunityService.getAcceptedApplicationsForOpportunity(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid promoter ID when counting concluded opportunities then return count")
    void givenValidPromoterId_whenCountingConcludedOpportunities_thenReturnCount() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED)).thenReturn(5L);

        long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("Given non-existent promoter when counting concluded opportunities then throw exception")
    void givenNonExistentPromoter_whenCountingConcludedOpportunities_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> opportunityService.countConcludedOpportunitiesByPromoter(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given non-existent promoter when creating opportunity then throw exception")
    void givenNonExistentPromoter_whenCreatingOpportunity_thenThrowException() {
        when(promoterRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setPromoterId(999L);

        assertThatThrownBy(() -> opportunityService.createOpportunity(createRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Given non-existent promoter when getting opportunities by promoter then throw exception")
    void givenNonExistentPromoter_whenGettingOpportunitiesByPromoter_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> opportunityService.getOpportunitiesByPromoter(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
