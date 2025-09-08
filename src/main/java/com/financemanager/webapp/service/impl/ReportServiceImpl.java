package com.financemanager.webapp.service.impl;

import com.financemanager.webapp.dto.ChartDataDTO;
import com.financemanager.webapp.dto.SummaryDTO;
import com.financemanager.webapp.exception.ResourceNotFoundException;
import com.financemanager.webapp.model.Transaction;
import com.financemanager.webapp.model.TransactionType;
import com.financemanager.webapp.repository.TransactionRepository;
import com.financemanager.webapp.repository.UserRepository;
import com.financemanager.webapp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository; // Inject to check if user exists

    @Override
    @Transactional(readOnly = true)
    public SummaryDTO getMonthlySummary(Long userId, int year, int month) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return calculateSummary(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryDTO getYearlySummary(Long userId, int year) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        LocalDate startDate = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(year, Month.DECEMBER, 31);

        return calculateSummary(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public ChartDataDTO getCategorySpendingChartData(Long userId, int year, int month) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> expenses = transactionRepository.findByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate);

        // Group by category name and sum amounts
        Map<String, BigDecimal> spendingByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(), // Group by category name
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add) // Sum amounts
                ));

        // Prepare data for ChartDataDTO
        List<String> labels = new ArrayList<>(spendingByCategory.keySet());
        List<BigDecimal> values = new ArrayList<>(spendingByCategory.values());

        return new ChartDataDTO(labels, values);
    }


    // --- Helper method to calculate summary ---
    private SummaryDTO calculateSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> incomeTransactions = transactionRepository.findByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, startDate, endDate);

        List<Transaction> expenseTransactions = transactionRepository.findByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate);

        BigDecimal totalIncome = incomeTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenseTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SummaryDTO(totalIncome, totalExpenses);

        // Alternative using custom repository methods with SUM aggregate (potentially more efficient):
        /*
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
            userId, TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
            userId, TransactionType.EXPENSE, startDate, endDate);
        return new SummaryDTO(totalIncome, totalExpenses);
        */
    }
}