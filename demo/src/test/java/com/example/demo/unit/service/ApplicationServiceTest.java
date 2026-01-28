package com.example.demo.unit.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("ApplicationService Unit Tests")
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private PromoterRepository promoterRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Volunteer volunteer;
    private Opportunity opportunity;
    private Promoter promoter;
    private Application application;
    private CreateApplicationRequest createRequest;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@example.com");
        promoter.setOrganization("Test Org");

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");
        volunteer.setPhone("123456789");
        volunteer.setTotalPoints(0);

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(4);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.PENDING);
        application.setMotivation("I want to help");
        application.setAppliedAt(LocalDateTime.now());

        createRequest = new CreateApplicationRequest();
        createRequest.setOpportunityId(1L);
        createRequest.setVolunteerEmail("john@example.com");
        createRequest.setVolunteerName("John Doe");
        createRequest.setVolunteerPhone("123456789");
        createRequest.setVolunteerSkills("Java");
        createRequest.setMotivation("I want to help");
    }

    @Nested
    @DisplayName("Create Application Tests")
    class CreateApplicationTests {

        @Test
        @DisplayName("Should create application with existing volunteer")
        void shouldCreateApplicationWithExistingVolunteer() {
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer));
            when(applicationRepository.existsByVolunteerIdAndOpportunityId(1L, 1L)).thenReturn(false);
            when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
                Application a = invocation.getArgument(0);
                a.setId(1L);
                return a;
            });

            ApplicationResponse response = applicationService.createApplication(createRequest);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(any(Application.class));
            verify(volunteerRepository, never()).save(any(Volunteer.class)); // Did not create new volunteer
        }

        @Test
        @DisplayName("Should create application and new volunteer")
        void shouldCreateApplicationAndNewVolunteer() {
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(volunteerRepository.findByEmail("newvolunteer@example.com")).thenReturn(Optional.empty());
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
                Volunteer v = invocation.getArgument(0);
                v.setId(2L);
                return v;
            });
            when(applicationRepository.existsByVolunteerIdAndOpportunityId(any(), any())).thenReturn(false);
            when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
                Application a = invocation.getArgument(0);
                a.setId(1L);
                return a;
            });

            createRequest.setVolunteerEmail("newvolunteer@example.com");
            createRequest.setVolunteerName("New Volunteer");

            ApplicationResponse response = applicationService.createApplication(createRequest);

            assertThat(response).isNotNull();
            verify(volunteerRepository).save(any(Volunteer.class)); // Created new volunteer
        }

        @Test
        @DisplayName("Should fail when opportunity not found")
        void shouldFailWhenOpportunityNotFound() {
            when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());
            createRequest.setOpportunityId(999L);

            assertThatThrownBy(() -> applicationService.createApplication(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Opportunity not found");
        }

        @Test
        @DisplayName("Should fail for duplicate application")
        void shouldFailForDuplicateApplication() {
            when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
            when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer));
            when(applicationRepository.existsByVolunteerIdAndOpportunityId(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> applicationService.createApplication(createRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already applied");
        }
    }

    @Nested
    @DisplayName("Get Application Tests")
    class GetApplicationTests {

        @Test
        @DisplayName("Should get application by ID")
        void shouldGetApplicationById() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            ApplicationResponse response = applicationService.getApplicationById(1L);

            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception for non-existent application")
        void shouldThrowExceptionForNonExistent() {
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getApplicationById(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get applications by opportunity")
        void shouldGetApplicationsByOpportunity() {
            when(opportunityRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByOpportunityId(1L))
                    .thenReturn(Collections.singletonList(application));

            List<ApplicationResponse> responses = applicationService.getApplicationsByOpportunity(1L);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception if opportunity not found when getting applications")
        void shouldThrowExceptionIfOpportunityNotFound() {
            when(opportunityRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> applicationService.getApplicationsByOpportunity(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get applications by volunteer")
        void shouldGetApplicationsByVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerId(1L))
                    .thenReturn(Collections.singletonList(application));

            List<ApplicationResponse> responses = applicationService.getApplicationsByVolunteer(1L);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should get applications by promoter")
        void shouldGetApplicationsByPromoter() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByOpportunityPromoterId(1L))
                    .thenReturn(Collections.singletonList(application));

            List<ApplicationResponse> responses = applicationService.getApplicationsByPromoter(1L);

            assertThat(responses).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Application Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update application status to ACCEPTED")
        void shouldUpdateStatusToAccepted() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);

            ApplicationResponse response = applicationService.updateApplicationStatus(1L, ApplicationStatus.ACCEPTED);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(argThat(a -> a.getStatus() == ApplicationStatus.ACCEPTED));
        }

        @Test
        @DisplayName("Should update application status to REJECTED")
        void shouldUpdateStatusToRejected() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);

            ApplicationResponse response = applicationService.updateApplicationStatus(1L, ApplicationStatus.REJECTED);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(argThat(a -> a.getStatus() == ApplicationStatus.REJECTED));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent application")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.updateApplicationStatus(999L, ApplicationStatus.ACCEPTED))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
