package com.example.demo.repository;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    List<Benefit> findByActiveTrue();

    List<Benefit> findByCategory(BenefitCategory category);

    List<Benefit> findByCategoryAndActiveTrue(BenefitCategory category);

    List<Benefit> findByPointsRequiredLessThanEqualAndActiveTrue(Integer points);

    List<Benefit> findByProviderContainingIgnoreCase(String provider);

    List<Benefit> findByProviderContainingIgnoreCaseAndActiveTrue(String provider);

    @Query("SELECT DISTINCT b.provider FROM Benefit b WHERE b.active = true ORDER BY b.provider")
    List<String> findAllActiveProviders();

    List<Benefit> findByActiveTrueOrderByPointsRequiredAsc();

    List<Benefit> findByActiveTrueOrderByPointsRequiredDesc();
}
