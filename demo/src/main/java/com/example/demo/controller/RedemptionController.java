package com.example.demo.controller;

import com.example.demo.dto.PartnerRedemptionStatsResponse;
import com.example.demo.dto.RedeemPointsRequest;
import com.example.demo.dto.RedemptionResponse;
import com.example.demo.service.RedemptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redemptions")
public class RedemptionController {

    private final RedemptionService redemptionService;

    public RedemptionController(RedemptionService redemptionService) {
        this.redemptionService = redemptionService;
    }

    @PostMapping
    public ResponseEntity<RedemptionResponse> redeemPoints(@Valid @RequestBody RedeemPointsRequest request) {
        RedemptionResponse redemption = redemptionService.redeemPoints(request);
        return new ResponseEntity<>(redemption, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RedemptionResponse> getRedemptionById(@PathVariable Long id) {
        RedemptionResponse redemption = redemptionService.getRedemptionById(id);
        return ResponseEntity.ok(redemption);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<RedemptionResponse>> getRedemptionsByVolunteer(@PathVariable Long volunteerId) {
        List<RedemptionResponse> redemptions = redemptionService.getRedemptionsByVolunteer(volunteerId);
        return ResponseEntity.ok(redemptions);
    }

    @GetMapping("/volunteer/{volunteerId}/completed")
    public ResponseEntity<List<RedemptionResponse>> getCompletedRedemptionsByVolunteer(@PathVariable Long volunteerId) {
        List<RedemptionResponse> redemptions = redemptionService.getCompletedRedemptionsByVolunteer(volunteerId);
        return ResponseEntity.ok(redemptions);
    }

    @GetMapping("/volunteer/{volunteerId}/total-spent")
    public ResponseEntity<Integer> getTotalPointsSpent(@PathVariable Long volunteerId) {
        Integer totalSpent = redemptionService.getTotalPointsSpent(volunteerId);
        return ResponseEntity.ok(totalSpent);
    }

    @GetMapping("/volunteer/{volunteerId}/count")
    public ResponseEntity<Long> getRedemptionCount(@PathVariable Long volunteerId) {
        Long count = redemptionService.getRedemptionCount(volunteerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/partner/{provider}")
    public ResponseEntity<List<RedemptionResponse>> getRedemptionsByProvider(@PathVariable String provider) {
        List<RedemptionResponse> redemptions = redemptionService.getRedemptionsByProvider(provider);
        return ResponseEntity.ok(redemptions);
    }

    @GetMapping("/partner/{provider}/stats")
    public ResponseEntity<PartnerRedemptionStatsResponse> getPartnerRedemptionStats(@PathVariable String provider) {
        PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats(provider);
        return ResponseEntity.ok(stats);
    }
}
