package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.PointsHistoryResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.service.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
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
