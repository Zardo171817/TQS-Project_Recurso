package com.example.demo.repository;

import com.example.demo.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByPromoterId(Long promoterId);

    List<Opportunity> findByTitleContainingIgnoreCase(String title);
}
