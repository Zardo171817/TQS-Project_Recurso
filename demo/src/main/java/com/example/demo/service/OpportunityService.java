package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.specification.OpportunitySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final PromoterRepository promoterRepository;
    private final ApplicationRepository applicationRepository;
    private final VolunteerRepository volunteerRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              PromoterRepository promoterRepository,
                              ApplicationRepository applicationRepository,
                              VolunteerRepository volunteerRepository) {
        this.opportunityRepository = opportunityRepository;
        this.promoterRepository = promoterRepository;
        this.applicationRepository = applicationRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Transactional
    public OpportunityResponse createOpportunity(CreateOpportunityRequest request) {
        Promoter promoter = promoterRepository.findById(request.getPromoterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Promoter not found with id: " + request.getPromoterId()));

        Opportunity opportunity = new Opportunity();
        opportunity.setTitle(request.getTitle());
        opportunity.setDescription(request.getDescription());
        opportunity.setSkills(request.getSkills());
        opportunity.setCategory(request.getCategory());
        opportunity.setDuration(request.getDuration());
        opportunity.setVacancies(request.getVacancies());
        opportunity.setPoints(request.getPoints());
        opportunity.setPromoter(promoter);

        Opportunity savedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityResponse.fromEntity(savedOpportunity);
    }

    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunityById(Long id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + id));
        return OpportunityResponse.fromEntity(opportunity);
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getAllOpportunities() {
        return opportunityRepository.findAll().stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getOpportunitiesByPromoter(Long promoterId) {
        if (!promoterRepository.existsById(promoterId)) {
            throw new ResourceNotFoundException("Promoter not found with id: " + promoterId);
        }
        return opportunityRepository.findByPromoterId(promoterId).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> filterOpportunities(OpportunityFilterRequest filter) {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                filter.getCategory(),
                filter.getSkills(),
                filter.getMinDuration(),
                filter.getMaxDuration()
        );

        return opportunityRepository.findAll(spec).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> filterOpportunitiesByParams(String category, String skills, Integer minDuration, Integer maxDuration) {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                category,
                skills,
                minDuration,
                maxDuration
        );

        return opportunityRepository.findAll(spec).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return opportunityRepository.findAllCategories();
    }

    @Transactional
    public OpportunityResponse updateOpportunity(Long id, UpdateOpportunityRequest request) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + id));

        opportunity.setTitle(request.getTitle());
        opportunity.setDescription(request.getDescription());
        opportunity.setSkills(request.getSkills());
        opportunity.setCategory(request.getCategory());
        opportunity.setDuration(request.getDuration());
        opportunity.setVacancies(request.getVacancies());
        opportunity.setPoints(request.getPoints());

        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityResponse.fromEntity(updatedOpportunity);
    }

    @Transactional
    public void deleteOpportunity(Long id) {
        if (!opportunityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Opportunity not found with id: " + id);
        }
        opportunityRepository.deleteById(id);
    }

    @Transactional
    public ConcludeOpportunityResponse concludeOpportunity(Long opportunityId, ConfirmParticipationRequest request) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Opportunity not found with id: " + opportunityId));

        if (!opportunity.getPromoter().getId().equals(request.getPromoterId())) {
            throw new IllegalStateException("Only the promoter who created this opportunity can conclude it");
        }

        if (opportunity.getStatus() == OpportunityStatus.CONCLUDED) {
            throw new IllegalStateException("Opportunity is already concluded");
        }

        List<ConcludeOpportunityResponse.ParticipantSummary> confirmedParticipants = new ArrayList<>();
        int totalPointsAwarded = 0;

        for (Long applicationId : request.getApplicationIds()) {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Application not found with id: " + applicationId));

            if (!application.getOpportunity().getId().equals(opportunityId)) {
                throw new IllegalStateException("Application " + applicationId + " does not belong to this opportunity");
            }

            if (application.getStatus() != ApplicationStatus.ACCEPTED) {
                throw new IllegalStateException("Only accepted applications can have participation confirmed");
            }

            if (application.getParticipationConfirmed()) {
                continue;
            }

            application.setParticipationConfirmed(true);
            application.setPointsAwarded(opportunity.getPoints());
            application.setConfirmedAt(LocalDateTime.now());
            applicationRepository.save(application);

            Volunteer volunteer = application.getVolunteer();
            volunteer.setTotalPoints(volunteer.getTotalPoints() + opportunity.getPoints());
            volunteerRepository.save(volunteer);

            ConcludeOpportunityResponse.ParticipantSummary summary = new ConcludeOpportunityResponse.ParticipantSummary();
            summary.setVolunteerId(volunteer.getId());
            summary.setVolunteerName(volunteer.getName());
            summary.setVolunteerEmail(volunteer.getEmail());
            summary.setPointsAwarded(opportunity.getPoints());
            summary.setTotalPoints(volunteer.getTotalPoints());
            confirmedParticipants.add(summary);

            totalPointsAwarded += opportunity.getPoints();
        }

        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());
        opportunityRepository.save(opportunity);

        ConcludeOpportunityResponse response = new ConcludeOpportunityResponse();
        response.setOpportunityId(opportunity.getId());
        response.setOpportunityTitle(opportunity.getTitle());
        response.setStatus(opportunity.getStatus());
        response.setConcludedAt(opportunity.getConcludedAt());
        response.setTotalParticipantsConfirmed(confirmedParticipants.size());
        response.setTotalPointsAwarded(totalPointsAwarded);
        response.setConfirmedParticipants(confirmedParticipants);

        return response;
    }

    @Transactional
    public ApplicationResponse confirmParticipation(Long applicationId, Long promoterId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + applicationId));

        Opportunity opportunity = application.getOpportunity();

        if (!opportunity.getPromoter().getId().equals(promoterId)) {
            throw new IllegalStateException("Only the promoter who created this opportunity can confirm participation");
        }

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted applications can have participation confirmed");
        }

        if (application.getParticipationConfirmed()) {
            throw new IllegalStateException("Participation is already confirmed for this application");
        }

        application.setParticipationConfirmed(true);
        application.setPointsAwarded(opportunity.getPoints());
        application.setConfirmedAt(LocalDateTime.now());
        applicationRepository.save(application);

        Volunteer volunteer = application.getVolunteer();
        volunteer.setTotalPoints(volunteer.getTotalPoints() + opportunity.getPoints());
        volunteerRepository.save(volunteer);

        return ApplicationResponse.fromEntity(application);
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getOpportunitiesByStatus(OpportunityStatus status) {
        return opportunityRepository.findByStatus(status).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getOpportunitiesByPromoterAndStatus(Long promoterId, OpportunityStatus status) {
        if (!promoterRepository.existsById(promoterId)) {
            throw new ResourceNotFoundException("Promoter not found with id: " + promoterId);
        }
        return opportunityRepository.findByPromoterIdAndStatus(promoterId, status).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAcceptedApplicationsForOpportunity(Long opportunityId) {
        if (!opportunityRepository.existsById(opportunityId)) {
            throw new ResourceNotFoundException("Opportunity not found with id: " + opportunityId);
        }
        return applicationRepository.findByOpportunityIdAndStatus(opportunityId, ApplicationStatus.ACCEPTED).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countConcludedOpportunitiesByPromoter(Long promoterId) {
        if (!promoterRepository.existsById(promoterId)) {
            throw new ResourceNotFoundException("Promoter not found with id: " + promoterId);
        }
        return opportunityRepository.countByPromoterIdAndStatus(promoterId, OpportunityStatus.CONCLUDED);
    }
}
