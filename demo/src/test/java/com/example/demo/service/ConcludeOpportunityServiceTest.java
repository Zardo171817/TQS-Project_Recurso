package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Conclude Opportunity Service Tests")
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

    private Promoter promoter;
    private Opportunity opportunity;
    private Volunteer volunteer;
    private Application application;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Beach Cleanup");
        opportunity.setDescription("Help clean the beach");
        opportunity.setSkills("Cleaning");
        opportunity.setCategory("Environment");
        opportunity.setDuration(4);
        opportunity.setVacancies(10);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@test.com");
        volunteer.setTotalPoints(0);

        application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setParticipationConfirmed(false);
        application.setPointsAwarded(0);
        application.setAppliedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("concludeOpportunity - Success Cases")
    class ConcludeOpportunitySuccessTests {

        @Test
        @DisplayName("Should conclude opportunity with single participant")
        void concludeOpportunity_SingleParticipant_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getOpportunityId()).isEqualTo(1L);
            assertThat(response.getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
            assertThat(response.getTotalParticipantsConfirmed()).isEqualTo(1);
            assertThat(response.getTotalPointsAwarded()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should conclude opportunity with multiple participants")
        void concludeOpportunity_MultipleParticipants_Success() {
            Volunteer volunteer2 = new Volunteer();
            volunteer2.setId(2L);
            volunteer2.setName("Jane Doe");
            volunteer2.setEmail("jane@test.com");
            volunteer2.setTotalPoints(50);

            Application application2 = new Application();
            application2.setId(2L);
            application2.setVolunteer(volunteer2);
            application2.setOpportunity(opportunity);
            application2.setStatus(ApplicationStatus.ACCEPTED);
            application2.setParticipationConfirmed(false);
            application2.setPointsAwarded(0);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.findById(2L)).thenReturn(Optional.of(application2));
            when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalParticipantsConfirmed()).isEqualTo(2);
            assertThat(response.getTotalPointsAwarded()).isEqualTo(200);
            assertThat(response.getConfirmedParticipants()).hasSize(2);
        }

        @Test
        @DisplayName("Should conclude opportunity with empty application list")
        void concludeOpportunity_EmptyApplicationList_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.emptyList());

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalParticipantsConfirmed()).isZero();
            assertThat(response.getTotalPointsAwarded()).isZero();
        }

        @Test
        @DisplayName("Should skip already confirmed applications")
        void concludeOpportunity_SkipAlreadyConfirmed_Success() {
            application.setParticipationConfirmed(true);
            application.setPointsAwarded(100);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalParticipantsConfirmed()).isZero();
            verify(volunteerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should set concluded timestamp")
        void concludeOpportunity_SetsConcludedAt_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getConcludedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should return correct participant summary details")
        void concludeOpportunity_CorrectParticipantSummary_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getConfirmedParticipants()).hasSize(1);
            ConcludeOpportunityResponse.ParticipantSummary participant = response.getConfirmedParticipants().get(0);
            assertThat(participant.getVolunteerId()).isEqualTo(1L);
            assertThat(participant.getVolunteerName()).isEqualTo("John Doe");
            assertThat(participant.getVolunteerEmail()).isEqualTo("john@test.com");
            assertThat(participant.getPointsAwarded()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should save application and volunteer repositories")
        void concludeOpportunity_VerifySaveCalls_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            verify(applicationRepository, times(1)).save(any(Application.class));
            verify(volunteerRepository, times(1)).save(any(Volunteer.class));
            verify(opportunityRepository, times(1)).save(any(Opportunity.class));
        }

        @Test
        @DisplayName("Should return correct opportunity title in response")
        void concludeOpportunity_ResponseContainsTitle_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.emptyList());

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getOpportunityTitle()).isEqualTo("Beach Cleanup");
        }
    }

    @Nested
    @DisplayName("concludeOpportunity - Points Calculation")
    class ConcludeOpportunityPointsTests {

        @ParameterizedTest
        @ValueSource(ints = {10, 50, 100, 250, 500})
        @DisplayName("Should award correct points for different opportunity values")
        void concludeOpportunity_DifferentPointValues_CorrectPoints(int points) {
            opportunity.setPoints(points);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalPointsAwarded()).isEqualTo(points);
        }

        @Test
        @DisplayName("Should accumulate points for volunteer with existing points")
        void concludeOpportunity_AccumulatePoints_Success() {
            volunteer.setTotalPoints(200);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            ConcludeOpportunityResponse.ParticipantSummary participant = response.getConfirmedParticipants().get(0);
            assertThat(participant.getTotalPoints()).isEqualTo(300);
        }

        @Test
        @DisplayName("Should set points awarded on application")
        void concludeOpportunity_SetsApplicationPointsAwarded_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(appCaptor.capture())).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            Application savedApp = appCaptor.getValue();
            assertThat(savedApp.getPointsAwarded()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should calculate total points for multiple participants correctly")
        void concludeOpportunity_MultipleParticipantsTotalPoints_Success() {
            Volunteer volunteer2 = new Volunteer();
            volunteer2.setId(2L);
            volunteer2.setName("Jane");
            volunteer2.setEmail("jane@test.com");
            volunteer2.setTotalPoints(0);

            Volunteer volunteer3 = new Volunteer();
            volunteer3.setId(3L);
            volunteer3.setName("Bob");
            volunteer3.setEmail("bob@test.com");
            volunteer3.setTotalPoints(0);

            Application application2 = new Application();
            application2.setId(2L);
            application2.setVolunteer(volunteer2);
            application2.setOpportunity(opportunity);
            application2.setStatus(ApplicationStatus.ACCEPTED);
            application2.setParticipationConfirmed(false);

            Application application3 = new Application();
            application3.setId(3L);
            application3.setVolunteer(volunteer3);
            application3.setOpportunity(opportunity);
            application3.setStatus(ApplicationStatus.ACCEPTED);
            application3.setParticipationConfirmed(false);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L, 3L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.findById(2L)).thenReturn(Optional.of(application2));
            when(applicationRepository.findById(3L)).thenReturn(Optional.of(application3));
            when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalPointsAwarded()).isEqualTo(300);
        }

        @Test
        @DisplayName("Should set confirmedAt on application when concluding")
        void concludeOpportunity_SetsConfirmedAtOnApplication_Success() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(appCaptor.capture())).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            assertThat(appCaptor.getValue().getConfirmedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("concludeOpportunity - Error Cases")
    class ConcludeOpportunityErrorTests {

        @Test
        @DisplayName("Should throw exception when opportunity not found")
        void concludeOpportunity_OpportunityNotFound_ThrowsException() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Opportunity not found");
        }

        @Test
        @DisplayName("Should throw exception when wrong promoter tries to conclude")
        void concludeOpportunity_WrongPromoter_ThrowsException() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(2L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only the promoter who created this opportunity can conclude it");
        }

        @Test
        @DisplayName("Should throw exception when opportunity already concluded")
        void concludeOpportunity_AlreadyConcluded_ThrowsException() {
            opportunity.setStatus(OpportunityStatus.CONCLUDED);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Opportunity is already concluded");
        }

        @Test
        @DisplayName("Should throw exception when application not found")
        void concludeOpportunity_ApplicationNotFound_ThrowsException() {
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(999L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Application not found");
        }

        @Test
        @DisplayName("Should throw exception when application belongs to different opportunity")
        void concludeOpportunity_ApplicationFromDifferentOpportunity_ThrowsException() {
            Opportunity otherOpportunity = new Opportunity();
            otherOpportunity.setId(2L);
            application.setOpportunity(otherOpportunity);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not belong to this opportunity");
        }

        @Test
        @DisplayName("Should throw exception when application is not accepted")
        void concludeOpportunity_NotAccepted_ThrowsException() {
            application.setStatus(ApplicationStatus.PENDING);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only accepted applications can have participation confirmed");
        }

        @Test
        @DisplayName("Should throw exception when application is rejected")
        void concludeOpportunity_Rejected_ThrowsException() {
            application.setStatus(ApplicationStatus.REJECTED);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only accepted applications");
        }

        @Test
        @DisplayName("Should not save opportunity when application validation fails")
        void concludeOpportunity_ValidationFails_NoOpportunitySave() {
            application.setStatus(ApplicationStatus.PENDING);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.concludeOpportunity(1L, request))
                    .isInstanceOf(IllegalStateException.class);

            verify(opportunityRepository, never()).save(any(Opportunity.class));
        }
    }

    @Nested
    @DisplayName("confirmParticipation - Individual Confirmation")
    class ConfirmParticipationTests {

        @Test
        @DisplayName("Should confirm individual participation successfully")
        void confirmParticipation_Success() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));

            ApplicationResponse response = opportunityService.confirmParticipation(1L, 1L);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(any(Application.class));
            verify(volunteerRepository).save(any(Volunteer.class));
        }

        @Test
        @DisplayName("Should throw exception when application not found for confirmation")
        void confirmParticipation_ApplicationNotFound_ThrowsException() {
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> opportunityService.confirmParticipation(999L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Application not found");
        }

        @Test
        @DisplayName("Should throw exception when wrong promoter tries to confirm")
        void confirmParticipation_WrongPromoter_ThrowsException() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 2L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only the promoter who created this opportunity can confirm participation");
        }

        @Test
        @DisplayName("Should throw exception when application not accepted for confirmation")
        void confirmParticipation_NotAccepted_ThrowsException() {
            application.setStatus(ApplicationStatus.PENDING);
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only accepted applications");
        }

        @Test
        @DisplayName("Should throw exception when participation already confirmed")
        void confirmParticipation_AlreadyConfirmed_ThrowsException() {
            application.setParticipationConfirmed(true);
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> opportunityService.confirmParticipation(1L, 1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Participation is already confirmed");
        }

        @Test
        @DisplayName("Should set confirmation timestamp")
        void confirmParticipation_SetsConfirmedAt() {
            ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(appCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));

            opportunityService.confirmParticipation(1L, 1L);

            assertThat(appCaptor.getValue().getConfirmedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should correctly award points on individual confirmation")
        void confirmParticipation_PointsCorrectlyAwarded() {
            ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(appCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));

            opportunityService.confirmParticipation(1L, 1L);

            assertThat(appCaptor.getValue().getPointsAwarded()).isEqualTo(100);
            assertThat(appCaptor.getValue().getParticipationConfirmed()).isTrue();
        }

        @Test
        @DisplayName("Should accumulate volunteer points on individual confirmation")
        void confirmParticipation_VolunteerPointsAccumulated() {
            volunteer.setTotalPoints(200);
            ArgumentCaptor<Volunteer> volCaptor = ArgumentCaptor.forClass(Volunteer.class);

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(volCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            opportunityService.confirmParticipation(1L, 1L);

            assertThat(volCaptor.getValue().getTotalPoints()).isEqualTo(300);
        }
    }

    @Nested
    @DisplayName("getOpportunitiesByStatus")
    class GetOpportunitiesByStatusTests {

        @Test
        @DisplayName("Should return concluded opportunities")
        void getByStatus_Concluded_Success() {
            opportunity.setStatus(OpportunityStatus.CONCLUDED);
            when(opportunityRepository.findByStatus(OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.singletonList(opportunity));

            List<OpportunityResponse> result = opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        }

        @Test
        @DisplayName("Should return open opportunities")
        void getByStatus_Open_Success() {
            when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
                    .thenReturn(Collections.singletonList(opportunity));

            List<OpportunityResponse> result = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OpportunityStatus.OPEN);
        }

        @Test
        @DisplayName("Should return empty list when no opportunities with status")
        void getByStatus_NoResults_ReturnsEmpty() {
            when(opportunityRepository.findByStatus(OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.emptyList());

            List<OpportunityResponse> result = opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getOpportunitiesByPromoterAndStatus")
    class GetByPromoterAndStatusTests {

        @Test
        @DisplayName("Should return opportunities by promoter and status")
        void getByPromoterAndStatus_Success() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.singletonList(opportunity));

            List<OpportunityResponse> result = opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when promoter not found")
        void getByPromoterAndStatus_PromoterNotFound_ThrowsException() {
            when(promoterRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.CONCLUDED))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found");
        }

        @Test
        @DisplayName("Should return empty list when no matching opportunities")
        void getByPromoterAndStatus_NoResults_ReturnsEmpty() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
                    .thenReturn(Collections.emptyList());

            List<OpportunityResponse> result = opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAcceptedApplicationsForOpportunity")
    class GetAcceptedApplicationsTests {

        @Test
        @DisplayName("Should return accepted applications")
        void getAcceptedApplications_Success() {
            when(opportunityRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByOpportunityIdAndStatus(1L, ApplicationStatus.ACCEPTED))
                    .thenReturn(Collections.singletonList(application));

            List<ApplicationResponse> result = opportunityService.getAcceptedApplicationsForOpportunity(1L);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when opportunity not found")
        void getAcceptedApplications_OpportunityNotFound_ThrowsException() {
            when(opportunityRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> opportunityService.getAcceptedApplicationsForOpportunity(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Opportunity not found");
        }

        @Test
        @DisplayName("Should return empty list when no accepted applications")
        void getAcceptedApplications_NoResults_ReturnsEmpty() {
            when(opportunityRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByOpportunityIdAndStatus(1L, ApplicationStatus.ACCEPTED))
                    .thenReturn(Collections.emptyList());

            List<ApplicationResponse> result = opportunityService.getAcceptedApplicationsForOpportunity(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countConcludedOpportunitiesByPromoter")
    class CountConcludedTests {

        @Test
        @DisplayName("Should return count of concluded opportunities")
        void countConcluded_Success() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED)).thenReturn(5L);

            long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should return zero when no concluded opportunities")
        void countConcluded_Zero_Success() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED)).thenReturn(0L);

            long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

            assertThat(count).isZero();
        }

        @Test
        @DisplayName("Should throw exception when promoter not found")
        void countConcluded_PromoterNotFound_ThrowsException() {
            when(promoterRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> opportunityService.countConcludedOpportunitiesByPromoter(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found");
        }

        @Test
        @DisplayName("Should return high count of concluded opportunities")
        void countConcluded_HighCount_Success() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED)).thenReturn(100L);

            long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

            assertThat(count).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("Additional Edge Cases")
    class AdditionalEdgeCasesTests {

        @Test
        @DisplayName("Should handle opportunity with zero points")
        void concludeOpportunity_ZeroPoints_Success() {
            opportunity.setPoints(0);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalPointsAwarded()).isZero();
        }

        @Test
        @DisplayName("Should verify opportunity status changes to CONCLUDED")
        void concludeOpportunity_StatusChange_Success() {
            ArgumentCaptor<Opportunity> oppCaptor = ArgumentCaptor.forClass(Opportunity.class);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(oppCaptor.capture())).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            assertThat(oppCaptor.getValue().getStatus()).isEqualTo(OpportunityStatus.CONCLUDED);
        }

        @Test
        @DisplayName("Should verify application participation confirmed flag is set")
        void concludeOpportunity_ParticipationConfirmedFlag_Success() {
            ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.singletonList(1L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(appCaptor.capture())).thenReturn(application);
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            assertThat(appCaptor.getValue().getParticipationConfirmed()).isTrue();
        }

        @Test
        @DisplayName("Should handle mixed confirmed and unconfirmed applications")
        void concludeOpportunity_MixedConfirmedAndUnconfirmed_Success() {
            Application confirmedApp = new Application();
            confirmedApp.setId(2L);
            confirmedApp.setVolunteer(volunteer);
            confirmedApp.setOpportunity(opportunity);
            confirmedApp.setStatus(ApplicationStatus.ACCEPTED);
            confirmedApp.setParticipationConfirmed(true);
            confirmedApp.setPointsAwarded(100);

            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Arrays.asList(1L, 2L));

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.findById(2L)).thenReturn(Optional.of(confirmedApp));
            when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(i -> i.getArgument(0));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            ConcludeOpportunityResponse response = opportunityService.concludeOpportunity(1L, request);

            assertThat(response.getTotalParticipantsConfirmed()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should set concludedAt on opportunity entity")
        void concludeOpportunity_SetsConcludedAtOnEntity_Success() {
            ArgumentCaptor<Opportunity> oppCaptor = ArgumentCaptor.forClass(Opportunity.class);
            ConfirmParticipationRequest request = new ConfirmParticipationRequest(1L, Collections.emptyList());

            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(opportunityRepository.save(oppCaptor.capture())).thenReturn(opportunity);

            opportunityService.concludeOpportunity(1L, request);

            assertThat(oppCaptor.getValue().getConcludedAt()).isNotNull();
        }
    }
}
