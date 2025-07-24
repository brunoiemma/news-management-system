package com.example.demo.repository;

import com.example.demo.model.VisitorStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorStatsRepository extends JpaRepository<VisitorStats, Long> {
}
