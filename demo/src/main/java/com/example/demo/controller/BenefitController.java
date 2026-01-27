package com.example.demo.controller;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.UpdateBenefitRequest;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.service.BenefitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benefits")
public class BenefitController {

    private final BenefitService benefitService;

    public BenefitController(BenefitService benefitService) {
        this.benefitService = benefitService;
    }

    @GetMapping
    public ResponseEntity<List<BenefitResponse>> getAllActiveBenefits() {
        List<BenefitResponse> benefits = benefitService.getAllActiveBenefits();
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BenefitResponse> getBenefitById(@PathVariable Long id) {
        BenefitResponse benefit = benefitService.getBenefitById(id);
        return ResponseEntity.ok(benefit);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BenefitResponse>> getBenefitsByCategory(@PathVariable BenefitCategory category) {
        List<BenefitResponse> benefits = benefitService.getBenefitsByCategory(category);
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/volunteer/{volunteerId}/affordable")
    public ResponseEntity<List<BenefitResponse>> getAffordableBenefitsForVolunteer(@PathVariable Long volunteerId) {
        List<BenefitResponse> benefits = benefitService.getAffordableBenefitsForVolunteer(volunteerId);
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<BenefitResponse>> getBenefitsByProvider(@PathVariable String provider) {
        List<BenefitResponse> benefits = benefitService.getBenefitsByProvider(provider);
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/providers")
    public ResponseEntity<List<String>> getAllProviders() {
        List<String> providers = benefitService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/sorted/points-asc")
    public ResponseEntity<List<BenefitResponse>> getBenefitsSortedByPointsAsc() {
        List<BenefitResponse> benefits = benefitService.getBenefitsSortedByPointsAsc();
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/sorted/points-desc")
    public ResponseEntity<List<BenefitResponse>> getBenefitsSortedByPointsDesc() {
        List<BenefitResponse> benefits = benefitService.getBenefitsSortedByPointsDesc();
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/volunteer/{volunteerId}/catalog")
    public ResponseEntity<List<BenefitResponse>> getCatalogForVolunteer(@PathVariable Long volunteerId) {
        List<BenefitResponse> catalog = benefitService.getCatalogForVolunteer(volunteerId);
        return ResponseEntity.ok(catalog);
    }

    @PostMapping("/partner")
    public ResponseEntity<BenefitResponse> createPartnerBenefit(@Valid @RequestBody CreateBenefitRequest request) {
        BenefitResponse benefit = benefitService.createPartnerBenefit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(benefit);
    }

    @PutMapping("/partner/{id}")
    public ResponseEntity<BenefitResponse> updatePartnerBenefit(@PathVariable Long id,
                                                                 @Valid @RequestBody UpdateBenefitRequest request) {
        BenefitResponse benefit = benefitService.updatePartnerBenefit(id, request);
        return ResponseEntity.ok(benefit);
    }

    @DeleteMapping("/partner/{id}")
    public ResponseEntity<Void> deactivatePartnerBenefit(@PathVariable Long id) {
        benefitService.deactivatePartnerBenefit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partner")
    public ResponseEntity<List<BenefitResponse>> getPartnerBenefits() {
        List<BenefitResponse> benefits = benefitService.getPartnerBenefits();
        return ResponseEntity.ok(benefits);
    }

    @GetMapping("/partner/provider/{provider}")
    public ResponseEntity<List<BenefitResponse>> getPartnerBenefitsByProvider(@PathVariable String provider) {
        List<BenefitResponse> benefits = benefitService.getPartnerBenefitsByProvider(provider);
        return ResponseEntity.ok(benefits);
    }
}
