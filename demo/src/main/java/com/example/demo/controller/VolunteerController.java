package com.example.demo.controller;

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
}
