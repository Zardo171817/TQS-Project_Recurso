package com.example.demo.controller;

import com.example.demo.entity.Promoter;
import com.example.demo.repository.PromoterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promoters")
public class PromoterController {

    private final PromoterRepository promoterRepository;

    public PromoterController(PromoterRepository promoterRepository) {
        this.promoterRepository = promoterRepository;
    }

    @GetMapping
    public ResponseEntity<List<Promoter>> getAllPromoters() {
        List<Promoter> promoters = promoterRepository.findAll();
        return ResponseEntity.ok(promoters);
    }
}
