package com.example.demo.repository;

import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {

    List<Redemption> findByVolunteerId(Long volunteerId);

    List<Redemption> findByVolunteerIdAndStatus(Long volunteerId, RedemptionStatus status);

    List<Redemption> findByBenefitId(Long benefitId);

    List<Redemption> findByVolunteerIdOrderByRedeemedAtDesc(Long volunteerId);

    @Query("SELECT COALESCE(SUM(r.pointsSpent), 0) FROM Redemption r WHERE r.volunteer.id = :volunteerId AND r.status = 'COMPLETED'")
    Integer sumPointsSpentByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT COUNT(r) FROM Redemption r WHERE r.volunteer.id = :volunteerId AND r.status = 'COMPLETED'")
    Long countCompletedByVolunteerId(@Param("volunteerId") Long volunteerId);

    List<Redemption> findByBenefitProviderIgnoreCase(String provider);

    List<Redemption> findByBenefitProviderIgnoreCaseAndStatus(String provider, RedemptionStatus status);

    @Query("SELECT COUNT(r) FROM Redemption r WHERE r.benefit.provider = :provider AND r.status = 'COMPLETED'")
    Long countCompletedByProvider(@Param("provider") String provider);

    @Query("SELECT COALESCE(SUM(r.pointsSpent), 0) FROM Redemption r WHERE r.benefit.provider = :provider AND r.status = 'COMPLETED'")
    Long sumPointsSpentByProvider(@Param("provider") String provider);

    @Query("SELECT COUNT(r) FROM Redemption r WHERE r.benefit.id = :benefitId AND r.status = 'COMPLETED'")
    Long countCompletedByBenefitId(@Param("benefitId") Long benefitId);

    @Query("SELECT COALESCE(SUM(r.pointsSpent), 0) FROM Redemption r WHERE r.benefit.id = :benefitId AND r.status = 'COMPLETED'")
    Long sumPointsSpentByBenefitId(@Param("benefitId") Long benefitId);

    List<Redemption> findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc(String provider);
}
