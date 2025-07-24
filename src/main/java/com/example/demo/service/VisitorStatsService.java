package com.example.demo.service;

import com.example.demo.model.VisitorStats;
import com.example.demo.repository.VisitorStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class VisitorStatsService {

    @Autowired
    private VisitorStatsRepository visitorStatsRepository;

    public void recordVisit(HttpServletRequest request, String page) {
        VisitorStats stats = new VisitorStats();
        stats.setIpAddress(request.getRemoteAddr());
        stats.setPage(page);
        stats.setVisitDate(LocalDateTime.now());
        visitorStatsRepository.save(stats);
    }

    public long getTotalVisits() {
        return visitorStatsRepository.count();
    }
}
