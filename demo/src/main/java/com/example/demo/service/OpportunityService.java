package com.example.demo.service;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final PromoterRepository promoterRepository;

    public OpportunityService(OpportunityRepository opportunityRepository, PromoterRepository promoterRepository) {
        this.opportunityRepository = opportunityRepository;
        this.promoterRepository = promoterRepository;
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
}
