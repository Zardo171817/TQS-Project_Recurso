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


/*"Este teste valida a lógica de negócio do ApplicationService, que
gere as candidaturas de voluntários a oportunidades."*/

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
    private Application application;
    private CreateApplicationRequest createRequest;

    @BeforeEach
    void setUp() {
        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setOrganization("Test Org");

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opportunity");
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        application = new Application();
        application.setId(1L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        createRequest = new CreateApplicationRequest();
        createRequest.setOpportunityId(1L);
        createRequest.setVolunteerEmail("john@example.com");
        createRequest.setVolunteerName("John Doe");
        createRequest.setMotivation("I want to help");
    }

    @Test
    @DisplayName("Should create application successfully")
    void shouldCreateApplication() {
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
    }

    @Test
    @DisplayName("Should fail for duplicate application")
    void shouldFailForDuplicateApplication() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer));
        when(applicationRepository.existsByVolunteerIdAndOpportunityId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> applicationService.createApplication(createRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should get application by ID")
    void shouldGetApplicationById() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        ApplicationResponse response = applicationService.getApplicationById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get applications by opportunity")
    void shouldGetApplicationsByOpportunity() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByOpportunityId(1L)).thenReturn(Collections.singletonList(application));

        List<ApplicationResponse> responses = applicationService.getApplicationsByOpportunity(1L);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should update application status")
    void shouldUpdateApplicationStatus() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        ApplicationResponse response = applicationService.updateApplicationStatus(1L, ApplicationStatus.ACCEPTED);

        assertThat(response).isNotNull();
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw exception for non-existent application")
    void shouldThrowExceptionForNonExistent() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getApplicationById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
