package com.truthlens.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fact_check_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactCheckRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String claimText; // The known fake/true news

    @Column(nullable = false)
    private String verdict; // LIKELY TRUE, LIKELY FALSE, etc.

    private String sourceName; // e.g., Local Database, AltNews

    @Column(columnDefinition = "TEXT")
    private String explanation;
}