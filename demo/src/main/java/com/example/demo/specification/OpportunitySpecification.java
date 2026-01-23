package com.example.demo.specification;

import com.example.demo.entity.Opportunity;
import org.springframework.data.jpa.domain.Specification;

public class OpportunitySpecification {

    private OpportunitySpecification() {
        // Utility class
    }

    public static Specification<Opportunity> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("category")),
                category.toLowerCase().trim()
            );
        };
    }

    public static Specification<Opportunity> hasSkillsContaining(String skills) {
        return (root, query, criteriaBuilder) -> {
            if (skills == null || skills.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("skills")),
                "%" + skills.toLowerCase().trim() + "%"
            );
        };
    }

    public static Specification<Opportunity> hasDurationBetween(Integer minDuration, Integer maxDuration) {
        return (root, query, criteriaBuilder) -> {
            if (minDuration == null && maxDuration == null) {
                return criteriaBuilder.conjunction();
            }
            if (minDuration != null && maxDuration != null) {
                return criteriaBuilder.between(root.get("duration"), minDuration, maxDuration);
            }
            if (minDuration != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration);
        };
    }

    public static Specification<Opportunity> withFilters(String category, String skills, Integer minDuration, Integer maxDuration) {
        return Specification
                .where(hasCategory(category))
                .and(hasSkillsContaining(skills))
                .and(hasDurationBetween(minDuration, maxDuration));
    }
}
