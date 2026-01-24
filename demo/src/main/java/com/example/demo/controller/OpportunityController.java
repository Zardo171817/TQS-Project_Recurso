package com.example.demo.controller;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.dto.UpdateOpportunityRequest;
import com.example.demo.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @PostMapping
    public ResponseEntity<OpportunityResponse> createOpportunity(
            @Valid @RequestBody CreateOpportunityRequest request) {
        OpportunityResponse response = opportunityService.createOpportunity(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getOpportunityById(@PathVariable Long id) {
        OpportunityResponse response = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAllOpportunities() {
        List<OpportunityResponse> opportunities = opportunityService.getAllOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/promoter/{promoterId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByPromoter(
            @PathVariable Long promoterId) {
        List<OpportunityResponse> opportunities =
                opportunityService.getOpportunitiesByPromoter(promoterId);
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OpportunityResponse>> filterOpportunities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration) {
        List<OpportunityResponse> opportunities =
                opportunityService.filterOpportunitiesByParams(category, skills, minDuration, maxDuration);
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = opportunityService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> updateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOpportunityRequest request) {
        OpportunityResponse response = opportunityService.updateOpportunity(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
}
