package com.example.demo.service;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.UpdatePromoterProfileRequest;
import com.example.demo.dto.PromoterProfileResponse;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PromoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromoterProfileService {

    private final PromoterRepository promoterRepository;

    public PromoterProfileService(PromoterRepository promoterRepository) {
        this.promoterRepository = promoterRepository;
    }

    @Transactional
    public PromoterProfileResponse createProfile(CreatePromoterProfileRequest request) {
        if (promoterRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Promoter promoter = new Promoter();
        promoter.setName(request.getName());
        promoter.setEmail(request.getEmail());
        promoter.setOrganization(request.getOrganization());
        promoter.setDescription(request.getDescription());
        promoter.setPhone(request.getPhone());
        promoter.setWebsite(request.getWebsite());
        promoter.setAddress(request.getAddress());
        promoter.setLogoUrl(request.getLogoUrl());
        promoter.setOrganizationType(request.getOrganizationType());
        promoter.setAreaOfActivity(request.getAreaOfActivity());
        promoter.setFoundedYear(request.getFoundedYear());
        promoter.setNumberOfEmployees(request.getNumberOfEmployees());
        promoter.setSocialMedia(request.getSocialMedia());
        promoter.setProfileCreatedAt(LocalDateTime.now());

        Promoter savedPromoter = promoterRepository.save(promoter);
        return PromoterProfileResponse.fromEntity(savedPromoter);
    }

    @Transactional
    public PromoterProfileResponse updateProfile(Long promoterId, UpdatePromoterProfileRequest request) {
        Promoter promoter = promoterRepository.findById(promoterId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoter not found with id: " + promoterId));

        if (!request.hasUpdates()) {
            throw new IllegalArgumentException("No fields to update provided");
        }

        if (request.getName() != null) {
            promoter.setName(request.getName());
        }
        if (request.getOrganization() != null) {
            promoter.setOrganization(request.getOrganization());
        }
        if (request.getDescription() != null) {
            promoter.setDescription(request.getDescription());
        }
        if (request.getPhone() != null) {
            promoter.setPhone(request.getPhone());
        }
        if (request.getWebsite() != null) {
            promoter.setWebsite(request.getWebsite());
        }
        if (request.getAddress() != null) {
            promoter.setAddress(request.getAddress());
        }
        if (request.getLogoUrl() != null) {
            promoter.setLogoUrl(request.getLogoUrl());
        }
        if (request.getOrganizationType() != null) {
            promoter.setOrganizationType(request.getOrganizationType());
        }
        if (request.getAreaOfActivity() != null) {
            promoter.setAreaOfActivity(request.getAreaOfActivity());
        }
        if (request.getFoundedYear() != null) {
            promoter.setFoundedYear(request.getFoundedYear());
        }
        if (request.getNumberOfEmployees() != null) {
            promoter.setNumberOfEmployees(request.getNumberOfEmployees());
        }
        if (request.getSocialMedia() != null) {
            promoter.setSocialMedia(request.getSocialMedia());
        }

        promoter.setProfileUpdatedAt(LocalDateTime.now());
        Promoter updatedPromoter = promoterRepository.save(promoter);
        return PromoterProfileResponse.fromEntity(updatedPromoter);
    }

    @Transactional(readOnly = true)
    public PromoterProfileResponse getProfile(Long promoterId) {
        Promoter promoter = promoterRepository.findById(promoterId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoter not found with id: " + promoterId));
        return PromoterProfileResponse.fromEntity(promoter);
    }

    @Transactional(readOnly = true)
    public PromoterProfileResponse getProfileByEmail(String email) {
        Promoter promoter = promoterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Promoter not found with email: " + email));
        return PromoterProfileResponse.fromEntity(promoter);
    }

    @Transactional(readOnly = true)
    public List<PromoterProfileResponse> getAllProfiles() {
        return promoterRepository.findAll().stream()
                .map(PromoterProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromoterProfileResponse> findPromotersByOrganization(String organization) {
        return promoterRepository.findByOrganizationContainingIgnoreCase(organization).stream()
                .map(PromoterProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromoterProfileResponse> findPromotersByAreaOfActivity(String areaOfActivity) {
        return promoterRepository.findByAreaOfActivityContainingIgnoreCase(areaOfActivity).stream()
                .map(PromoterProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromoterProfileResponse> findPromotersByOrganizationType(String organizationType) {
        return promoterRepository.findByOrganizationTypeContainingIgnoreCase(organizationType).stream()
                .map(PromoterProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProfile(Long promoterId) {
        if (!promoterRepository.existsById(promoterId)) {
            throw new ResourceNotFoundException("Promoter not found with id: " + promoterId);
        }
        promoterRepository.deleteById(promoterId);
    }
}
