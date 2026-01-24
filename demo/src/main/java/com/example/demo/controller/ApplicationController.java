package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByOpportunity(
            @PathVariable Long opportunityId) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByOpportunity(opportunityId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByVolunteer(
            @PathVariable Long volunteerId) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByVolunteer(volunteerId);
        return ResponseEntity.ok(responses);
    }
}
