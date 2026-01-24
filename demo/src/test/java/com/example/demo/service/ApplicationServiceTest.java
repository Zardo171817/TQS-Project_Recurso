package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.entity.Application;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Opportunity testOpportunity;
    private Volunteer testVolunteer;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test Opportunity");

        testVolunteer = new Volunteer();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");

        testApplication = new Application();
        testApplication.setId(1L);
        testApplication.setVolunteer(testVolunteer);
        testApplication.setOpportunity(testOpportunity);
        testApplication.setStatus(ApplicationStatus.PENDING);
        testApplication.setMotivation("I want to help");
        testApplication.setAppliedAt(LocalDateTime.now());
    }

    // createApplication - 2 testes estratégicos
    @Test
    void whenCreateApplication_thenSuccess() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(1L);
        request.setVolunteerName("New Volunteer");
        request.setVolunteerEmail("new@test.com");

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(volunteerRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(testVolunteer);
        when(applicationRepository.existsByVolunteerIdAndOpportunityId(any(), any())).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        ApplicationResponse response = applicationService.createApplication(request);

        assertNotNull(response);
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
    }

    @Test
    void whenCreateDuplicateApplication_thenThrowException() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(1L);
        request.setVolunteerEmail("volunteer@test.com");

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(volunteerRepository.findByEmail("volunteer@test.com")).thenReturn(Optional.of(testVolunteer));
        when(applicationRepository.existsByVolunteerIdAndOpportunityId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
            applicationService.createApplication(request));
    }

    // getApplicationById - sucesso e erro
    @Test
    void whenGetApplicationById_thenReturnApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        ApplicationResponse response = applicationService.getApplicationById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getVolunteerName());
    }

    @Test
    void whenGetApplicationByIdNotFound_thenThrowException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            applicationService.getApplicationById(999L));
    }

    // getApplicationsByOpportunity - sucesso e erro
    @Test
    void whenGetApplicationsByOpportunity_thenReturnList() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByOpportunityId(1L)).thenReturn(Arrays.asList(testApplication));

        List<ApplicationResponse> responses = applicationService.getApplicationsByOpportunity(1L);

        assertEquals(1, responses.size());
        assertEquals("Test Opportunity", responses.get(0).getOpportunityTitle());
    }

    @Test
    void whenGetApplicationsByOpportunityNotFound_thenThrowException() {
        when(opportunityRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            applicationService.getApplicationsByOpportunity(999L));
    }

    // getApplicationsByVolunteer - sucesso e erro
    @Test
    void whenGetApplicationsByVolunteer_thenReturnList() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByVolunteerId(1L)).thenReturn(Arrays.asList(testApplication));

        List<ApplicationResponse> responses = applicationService.getApplicationsByVolunteer(1L);

        assertEquals(1, responses.size());
        assertEquals("volunteer@test.com", responses.get(0).getVolunteerEmail());
    }

    @Test
    void whenGetApplicationsByVolunteerNotFound_thenThrowException() {
        when(volunteerRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            applicationService.getApplicationsByVolunteer(999L));
    }
}
