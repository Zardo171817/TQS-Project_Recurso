package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String skills;

    @Column(length = 500)
    private String interests;

    @Column(length = 500)
    private String availability;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false)
    private Integer totalPoints = 0;

    @Column
    private LocalDateTime profileCreatedAt;

    @Column
    private LocalDateTime profileUpdatedAt;

    @PrePersist
    protected void onCreate() {
        if (totalPoints == null) {
            totalPoints = 0;
        }
    }
}
