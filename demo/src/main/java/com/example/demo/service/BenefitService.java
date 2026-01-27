package com.example.demo.service;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.UpdateBenefitRequest;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BenefitService {

    private final BenefitRepository benefitRepository;
    private final VolunteerRepository volunteerRepository;

    public BenefitService(BenefitRepository benefitRepository, VolunteerRepository volunteerRepository) {
        this.benefitRepository = benefitRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getAllActiveBenefits() {
        return benefitRepository.findByActiveTrue().stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BenefitResponse getBenefitById(Long id) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit not found with id: " + id));
        return BenefitResponse.fromEntity(benefit);
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getBenefitsByCategory(BenefitCategory category) {
        return benefitRepository.findByCategoryAndActiveTrue(category).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getAffordableBenefitsForVolunteer(Long volunteerId) {
        Integer volunteerPoints = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId))
                .getTotalPoints();

        return benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(volunteerPoints).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getBenefitsByProvider(String provider) {
        return benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue(provider).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllProviders() {
        return benefitRepository.findAllActiveProviders();
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getBenefitsSortedByPointsAsc() {
        return benefitRepository.findByActiveTrueOrderByPointsRequiredAsc().stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getBenefitsSortedByPointsDesc() {
        return benefitRepository.findByActiveTrueOrderByPointsRequiredDesc().stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getCatalogForVolunteer(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return benefitRepository.findByActiveTrueOrderByPointsRequiredAsc().stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public BenefitResponse createPartnerBenefit(CreateBenefitRequest request) {
        Benefit benefit = new Benefit();
        benefit.setName(request.getName());
        benefit.setDescription(request.getDescription());
        benefit.setPointsRequired(request.getPointsRequired());
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider(request.getProvider());
        benefit.setImageUrl(request.getImageUrl());

        Benefit saved = benefitRepository.save(benefit);
        return BenefitResponse.fromEntity(saved);
    }

    @Transactional
    public BenefitResponse updatePartnerBenefit(Long id, UpdateBenefitRequest request) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit not found with id: " + id));

        if (benefit.getCategory() != BenefitCategory.PARTNER) {
            throw new IllegalStateException("Only PARTNER benefits can be updated by partners");
        }

        if (request.getName() != null) {
            benefit.setName(request.getName());
        }
        if (request.getDescription() != null) {
            benefit.setDescription(request.getDescription());
        }
        if (request.getPointsRequired() != null) {
            benefit.setPointsRequired(request.getPointsRequired());
        }
        if (request.getProvider() != null) {
            benefit.setProvider(request.getProvider());
        }
        if (request.getImageUrl() != null) {
            benefit.setImageUrl(request.getImageUrl());
        }

        Benefit updated = benefitRepository.save(benefit);
        return BenefitResponse.fromEntity(updated);
    }

    @Transactional
    public void deactivatePartnerBenefit(Long id) {
        Benefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit not found with id: " + id));

        if (benefit.getCategory() != BenefitCategory.PARTNER) {
            throw new IllegalStateException("Only PARTNER benefits can be deactivated by partners");
        }

        benefit.setActive(false);
        benefitRepository.save(benefit);
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getPartnerBenefits() {
        return benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getPartnerBenefitsByProvider(String provider) {
        return benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue(provider).stream()
                .filter(b -> b.getCategory() == BenefitCategory.PARTNER)
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
