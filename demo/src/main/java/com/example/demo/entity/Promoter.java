package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "promoter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opportunity> opportunities = new ArrayList<>();
}
