package com.truthlens.repository;

import com.truthlens.entity.FactCheckRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactCheckRepository extends JpaRepository<FactCheckRecord, Long> {
    // Custom query to find records that contain any of our keywords
    List<FactCheckRecord> findByClaimTextContainingIgnoreCase(String keyword);
}