package com.example.demo.controller;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
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
}
