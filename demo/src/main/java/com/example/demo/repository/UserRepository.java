package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByUserType(UserType userType);

    List<User> findByActiveTrue();

    Optional<User> findByEmailAndActiveTrue(String email);
}
