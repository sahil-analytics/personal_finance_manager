package com.financemanager.webapp.controller;

import com.financemanager.webapp.dto.ChartDataDTO;
import com.financemanager.webapp.dto.SummaryDTO;
import com.financemanager.webapp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}/reports") // Reports are user-specific
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam Optional<Integer> month) { // Month is optional for yearly summary

        SummaryDTO summary;
        if (month.isPresent()) {
            summary = reportService.getMonthlySummary(userId, year, month.get());
        } else {
            summary = reportService.getYearlySummary(userId, year);
        }
        // Assume service returns a non-null DTO, possibly with zero values if no data
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/category-chart")
    public ResponseEntity<ChartDataDTO> getCategorySpendingChartData(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) { // Chart usually makes sense for a specific month

        ChartDataDTO chartData = reportService.getCategorySpendingChartData(userId, year, month);
        // Assume service returns a non-null DTO
        return ResponseEntity.ok(chartData);
    }
}