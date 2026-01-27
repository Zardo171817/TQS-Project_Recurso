package com.example.demo.service;

import com.example.demo.dto.BenefitRedemptionDetailResponse;
import com.example.demo.dto.PartnerRedemptionStatsResponse;
import com.example.demo.dto.RedeemPointsRequest;
import com.example.demo.dto.RedemptionResponse;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final VolunteerRepository volunteerRepository;
    private final BenefitRepository benefitRepository;

    public RedemptionService(RedemptionRepository redemptionRepository,
                             VolunteerRepository volunteerRepository,
                             BenefitRepository benefitRepository) {
        this.redemptionRepository = redemptionRepository;
        this.volunteerRepository = volunteerRepository;
        this.benefitRepository = benefitRepository;
    }

    @Transactional
    public RedemptionResponse redeemPoints(RedeemPointsRequest request) {
        Volunteer volunteer = volunteerRepository.findById(request.getVolunteerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Volunteer not found with id: " + request.getVolunteerId()));

        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Benefit not found with id: " + request.getBenefitId()));

        if (!benefit.getActive()) {
            throw new IllegalStateException("Benefit is not active: " + benefit.getName());
        }

        if (volunteer.getTotalPoints() < benefit.getPointsRequired()) {
            throw new IllegalStateException(
                    "Insufficient points. Required: " + benefit.getPointsRequired()
                            + ", Available: " + volunteer.getTotalPoints());
        }

        volunteer.setTotalPoints(volunteer.getTotalPoints() - benefit.getPointsRequired());
        volunteerRepository.save(volunteer);

        Redemption redemption = new Redemption();
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefit);
        redemption.setPointsSpent(benefit.getPointsRequired());
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());

        Redemption savedRedemption = redemptionRepository.save(redemption);

        return RedemptionResponse.fromEntity(savedRedemption);
    }

    @Transactional(readOnly = true)
    public RedemptionResponse getRedemptionById(Long id) {
        Redemption redemption = redemptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Redemption not found with id: " + id));
        return RedemptionResponse.fromEntity(redemption);
    }

    @Transactional(readOnly = true)
    public List<RedemptionResponse> getRedemptionsByVolunteer(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(volunteerId).stream()
                .map(RedemptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RedemptionResponse> getCompletedRedemptionsByVolunteer(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return redemptionRepository.findByVolunteerIdAndStatus(volunteerId, RedemptionStatus.COMPLETED).stream()
                .map(RedemptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getTotalPointsSpent(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return redemptionRepository.sumPointsSpentByVolunteerId(volunteerId);
    }

    @Transactional(readOnly = true)
    public Long getRedemptionCount(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return redemptionRepository.countCompletedByVolunteerId(volunteerId);
    }

    @Transactional(readOnly = true)
    public List<RedemptionResponse> getRedemptionsByProvider(String provider) {
        List<Benefit> providerBenefits = benefitRepository.findByProviderContainingIgnoreCase(provider);
        if (providerBenefits.isEmpty()) {
            throw new ResourceNotFoundException("No benefits found for provider: " + provider);
        }
        return redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc(provider).stream()
                .map(RedemptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartnerRedemptionStatsResponse getPartnerRedemptionStats(String provider) {
        List<Benefit> providerBenefits = benefitRepository.findByProviderContainingIgnoreCase(provider);
        if (providerBenefits.isEmpty()) {
            throw new ResourceNotFoundException("No benefits found for provider: " + provider);
        }

        List<Benefit> partnerBenefits = providerBenefits.stream()
                .filter(b -> b.getCategory() == BenefitCategory.PARTNER)
                .collect(Collectors.toList());

        if (partnerBenefits.isEmpty()) {
            throw new ResourceNotFoundException("No PARTNER benefits found for provider: " + provider);
        }

        Long totalRedemptions = 0L;
        Long totalPointsRedeemed = 0L;
        List<BenefitRedemptionDetailResponse> benefitDetails = new ArrayList<>();

        for (Benefit benefit : partnerBenefits) {
            Long redemptionCount = redemptionRepository.countCompletedByBenefitId(benefit.getId());
            Long pointsRedeemed = redemptionRepository.sumPointsSpentByBenefitId(benefit.getId());

            totalRedemptions += redemptionCount;
            totalPointsRedeemed += pointsRedeemed;

            BenefitRedemptionDetailResponse detail = new BenefitRedemptionDetailResponse();
            detail.setBenefitId(benefit.getId());
            detail.setBenefitName(benefit.getName());
            detail.setBenefitDescription(benefit.getDescription());
            detail.setPointsRequired(benefit.getPointsRequired());
            detail.setProvider(benefit.getProvider());
            detail.setActive(benefit.getActive());
            detail.setTotalRedemptions(redemptionCount);
            detail.setTotalPointsRedeemed(pointsRedeemed);

            benefitDetails.add(detail);
        }

        List<RedemptionResponse> recentRedemptions = redemptionRepository
                .findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc(provider).stream()
                .map(RedemptionResponse::fromEntity)
                .collect(Collectors.toList());

        PartnerRedemptionStatsResponse stats = new PartnerRedemptionStatsResponse();
        stats.setProvider(provider);
        stats.setTotalBenefits(partnerBenefits.size());
        stats.setTotalRedemptions(totalRedemptions);
        stats.setTotalPointsRedeemed(totalPointsRedeemed);
        stats.setBenefitDetails(benefitDetails);
        stats.setRecentRedemptions(recentRedemptions);

        return stats;
    }
}
