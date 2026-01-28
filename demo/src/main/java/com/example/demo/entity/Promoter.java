package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promoters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promoter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String organization;

    @Column(length = 1000)
    private String description;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(length = 500)
    private String address;

    @Column(length = 255)
    private String logoUrl;

    @Column(length = 100)
    private String organizationType;

    @Column(length = 500)
    private String areaOfActivity;

    @Column(length = 100)
    private String foundedYear;

    @Column(length = 50)
    private String numberOfEmployees;

    @Column(length = 500)
    private String socialMedia;

    private LocalDateTime profileCreatedAt;

    private LocalDateTime profileUpdatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "promoter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opportunity> opportunities = new ArrayList<>();
}
