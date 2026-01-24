package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.entity.Application;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VolunteerRepository volunteerRepository;
    private final OpportunityRepository opportunityRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              VolunteerRepository volunteerRepository,
                              OpportunityRepository opportunityRepository) {
        this.applicationRepository = applicationRepository;
        this.volunteerRepository = volunteerRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional
    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + request.getOpportunityId()));

        Volunteer volunteer = volunteerRepository.findByEmail(request.getVolunteerEmail())
                .orElseGet(() -> {
                    Volunteer newVolunteer = new Volunteer();
                    newVolunteer.setName(request.getVolunteerName());
                    newVolunteer.setEmail(request.getVolunteerEmail());
                    newVolunteer.setPhone(request.getVolunteerPhone());
                    newVolunteer.setSkills(request.getVolunteerSkills());
                    return volunteerRepository.save(newVolunteer);
                });

        if (applicationRepository.existsByVolunteerIdAndOpportunityId(volunteer.getId(), opportunity.getId())) {
            throw new IllegalStateException("You have already applied to this opportunity");
        }

        Application application = new Application();
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setMotivation(request.getMotivation());

        Application savedApplication = applicationRepository.save(application);
        return ApplicationResponse.fromEntity(savedApplication);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));
        return ApplicationResponse.fromEntity(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByOpportunity(Long opportunityId) {
        if (!opportunityRepository.existsById(opportunityId)) {
            throw new ResourceNotFoundException("Opportunity not found with id: " + opportunityId);
        }
        return applicationRepository.findByOpportunityId(opportunityId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByVolunteer(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return applicationRepository.findByVolunteerId(volunteerId).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
