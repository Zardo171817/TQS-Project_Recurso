package com.example.demo.repository;

import com.example.demo.entity.Promoter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoterRepository extends JpaRepository<Promoter, Long> {

    Optional<Promoter> findByEmail(String email);

    boolean existsByEmail(String email);
}
