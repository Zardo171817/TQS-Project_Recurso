package com.example.demo.repository;

import com.example.demo.entity.Application;
import com.example.demo.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByVolunteerId(Long volunteerId);
    List<Application> findByOpportunityId(Long opportunityId);
    Optional<Application> findByVolunteerIdAndOpportunityId(Long volunteerId, Long opportunityId);
    boolean existsByVolunteerIdAndOpportunityId(Long volunteerId, Long opportunityId);
    List<Application> findByStatus(ApplicationStatus status);
}
