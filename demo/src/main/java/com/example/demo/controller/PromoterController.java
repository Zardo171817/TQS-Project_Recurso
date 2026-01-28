package com.example.demo.controller;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.UpdatePromoterProfileRequest;
import com.example.demo.dto.PromoterProfileResponse;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.service.PromoterProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promoters")
public class PromoterController {

    private final PromoterRepository promoterRepository;
    private final PromoterProfileService promoterProfileService;

    public PromoterController(PromoterRepository promoterRepository, PromoterProfileService promoterProfileService) {
        this.promoterRepository = promoterRepository;
        this.promoterProfileService = promoterProfileService;
    }

    @GetMapping
    public ResponseEntity<List<Promoter>> getAllPromoters() {
        List<Promoter> promoters = promoterRepository.findAll();
        return ResponseEntity.ok(promoters);
    }

    @PostMapping("/profile")
    public ResponseEntity<PromoterProfileResponse> createProfile(@Valid @RequestBody CreatePromoterProfileRequest request) {
        PromoterProfileResponse response = promoterProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<PromoterProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromoterProfileRequest request) {
        PromoterProfileResponse response = promoterProfileService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<PromoterProfileResponse> getProfile(@PathVariable Long id) {
        PromoterProfileResponse response = promoterProfileService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/email/{email}")
    public ResponseEntity<PromoterProfileResponse> getProfileByEmail(@PathVariable String email) {
        PromoterProfileResponse response = promoterProfileService.getProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<PromoterProfileResponse>> getAllProfiles() {
        List<PromoterProfileResponse> profiles = promoterProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/organization/{organization}")
    public ResponseEntity<List<PromoterProfileResponse>> findByOrganization(@PathVariable String organization) {
        List<PromoterProfileResponse> profiles = promoterProfileService.findPromotersByOrganization(organization);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/area/{areaOfActivity}")
    public ResponseEntity<List<PromoterProfileResponse>> findByAreaOfActivity(@PathVariable String areaOfActivity) {
        List<PromoterProfileResponse> profiles = promoterProfileService.findPromotersByAreaOfActivity(areaOfActivity);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/type/{organizationType}")
    public ResponseEntity<List<PromoterProfileResponse>> findByOrganizationType(@PathVariable String organizationType) {
        List<PromoterProfileResponse> profiles = promoterProfileService.findPromotersByOrganizationType(organizationType);
        return ResponseEntity.ok(profiles);
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        promoterProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
