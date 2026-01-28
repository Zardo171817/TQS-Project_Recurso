package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.VolunteerProfileService;
import com.example.demo.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerProfileService volunteerProfileService;

    public VolunteerController(VolunteerService volunteerService, VolunteerProfileService volunteerProfileService) {
        this.volunteerService = volunteerService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @PostMapping("/profile")
    public ResponseEntity<VolunteerProfileResponse> createProfile(
            @Valid @RequestBody CreateVolunteerProfileRequest request) {
        VolunteerProfileResponse response = volunteerProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<VolunteerProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVolunteerProfileRequest request) {
        VolunteerProfileResponse response = volunteerProfileService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<VolunteerProfileResponse> getProfile(@PathVariable Long id) {
        VolunteerProfileResponse response = volunteerProfileService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/email/{email}")
    public ResponseEntity<VolunteerProfileResponse> getProfileByEmail(@PathVariable String email) {
        VolunteerProfileResponse response = volunteerProfileService.getProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<VolunteerProfileResponse>> getAllProfiles() {
        List<VolunteerProfileResponse> profiles = volunteerProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/skills/{skills}")
    public ResponseEntity<List<VolunteerProfileResponse>> findBySkills(@PathVariable String skills) {
        List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersBySkills(skills);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/interests/{interests}")
    public ResponseEntity<List<VolunteerProfileResponse>> findByInterests(@PathVariable String interests) {
        List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByInterests(interests);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/profiles/availability/{availability}")
    public ResponseEntity<List<VolunteerProfileResponse>> findByAvailability(@PathVariable String availability) {
        List<VolunteerProfileResponse> profiles = volunteerProfileService.findVolunteersByAvailability(availability);
        return ResponseEntity.ok(profiles);
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        volunteerProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<VolunteerResponse>> getAllVolunteers() {
        List<VolunteerResponse> volunteers = volunteerService.getAllVolunteers();
        return ResponseEntity.ok(volunteers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerResponse> getVolunteerById(@PathVariable Long id) {
        VolunteerResponse volunteer = volunteerService.getVolunteerById(id);
        return ResponseEntity.ok(volunteer);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<VolunteerResponse> getVolunteerByEmail(@PathVariable String email) {
        VolunteerResponse volunteer = volunteerService.getVolunteerByEmail(email);
        return ResponseEntity.ok(volunteer);
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = volunteerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<VolunteerPointsResponse> getVolunteerPoints(@PathVariable Long id) {
        VolunteerPointsResponse points = volunteerService.getVolunteerPoints(id);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<VolunteerPointsResponse>> getVolunteersRanking() {
        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<VolunteerPointsResponse>> getTopVolunteers(@PathVariable int limit) {
        List<VolunteerPointsResponse> topVolunteers = volunteerService.getTopVolunteers(limit);
        return ResponseEntity.ok(topVolunteers);
    }

    @GetMapping("/{id}/confirmed-participations")
    public ResponseEntity<List<ApplicationResponse>> getConfirmedParticipations(@PathVariable Long id) {
        List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(id);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/{id}/points-history")
    public ResponseEntity<List<PointsHistoryResponse>> getPointsHistory(@PathVariable Long id) {
        List<PointsHistoryResponse> history = volunteerService.getPointsHistory(id);
        return ResponseEntity.ok(history);
    }
}
