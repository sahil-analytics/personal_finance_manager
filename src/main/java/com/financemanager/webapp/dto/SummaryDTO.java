package com.financemanager.webapp.dto;

import java.math.BigDecimal;

// Used to return summary data (monthly or yearly)
public class SummaryDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance; // totalIncome - totalExpenses

    // Constructors
    public SummaryDTO() {
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
    }

    public SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpenses) {
        this.totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        this.totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
        this.balance = this.totalIncome.subtract(this.totalExpenses);
    }

    // Getters and Setters
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        updateBalance();
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
        updateBalance();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // Private helper to keep balance consistent
    private void updateBalance() {
        this.balance = (this.totalIncome != null ? this.totalIncome : BigDecimal.ZERO)
                .subtract(this.totalExpenses != null ? this.totalExpenses : BigDecimal.ZERO);
    }

    // toString() - Optional
    @Override
    public String toString() {
        return "SummaryDTO{" +
                "totalIncome=" + totalIncome +
                ", totalExpenses=" + totalExpenses +
                ", balance=" + balance +
                '}';
    }
}