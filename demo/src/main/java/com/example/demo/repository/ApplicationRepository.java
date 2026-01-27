package com.example.demo.repository;

import com.example.demo.entity.Application;
import com.example.demo.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Application> findByOpportunityPromoterId(Long promoterId);

    List<Application> findByOpportunityIdAndStatus(Long opportunityId, ApplicationStatus status);

    List<Application> findByOpportunityIdAndParticipationConfirmed(Long opportunityId, Boolean confirmed);

    @Query("SELECT a FROM Application a WHERE a.opportunity.id = :opportunityId AND a.status = :status AND a.participationConfirmed = :confirmed")
    List<Application> findByOpportunityIdAndStatusAndParticipationConfirmed(
            @Param("opportunityId") Long opportunityId,
            @Param("status") ApplicationStatus status,
            @Param("confirmed") Boolean confirmed);

    @Query("SELECT COALESCE(SUM(a.pointsAwarded), 0) FROM Application a WHERE a.volunteer.id = :volunteerId")
    Integer sumPointsAwardedByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.opportunity.id = :opportunityId AND a.participationConfirmed = true")
    long countConfirmedByOpportunityId(@Param("opportunityId") Long opportunityId);

    List<Application> findByVolunteerIdAndParticipationConfirmed(Long volunteerId, Boolean confirmed);
}
