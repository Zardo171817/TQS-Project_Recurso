package com.example.demo.repository;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long>, JpaSpecificationExecutor<Opportunity> {

    List<Opportunity> findByPromoterId(Long promoterId);

    List<Opportunity> findByTitleContainingIgnoreCase(String title);

    List<Opportunity> findByCategoryIgnoreCase(String category);

    List<Opportunity> findBySkillsContainingIgnoreCase(String skills);

    List<Opportunity> findByDurationBetween(Integer minDuration, Integer maxDuration);

    List<Opportunity> findByDurationLessThanEqual(Integer maxDuration);

    List<Opportunity> findByDurationGreaterThanEqual(Integer minDuration);

    @Query("SELECT DISTINCT o.category FROM Opportunity o ORDER BY o.category")
    List<String> findAllCategories();

    List<Opportunity> findByStatus(OpportunityStatus status);

    List<Opportunity> findByPromoterIdAndStatus(Long promoterId, OpportunityStatus status);

    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.promoter.id = :promoterId AND o.status = :status")
    long countByPromoterIdAndStatus(@Param("promoterId") Long promoterId, @Param("status") OpportunityStatus status);
}
