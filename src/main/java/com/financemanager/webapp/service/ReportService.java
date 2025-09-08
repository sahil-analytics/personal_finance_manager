package com.financemanager.webapp.service;

import com.financemanager.webapp.dto.ChartDataDTO;
import com.financemanager.webapp.dto.SummaryDTO;

public interface ReportService {
    SummaryDTO getMonthlySummary(Long userId, int year, int month);
    SummaryDTO getYearlySummary(Long userId, int year);
    ChartDataDTO getCategorySpendingChartData(Long userId, int year, int month);
}