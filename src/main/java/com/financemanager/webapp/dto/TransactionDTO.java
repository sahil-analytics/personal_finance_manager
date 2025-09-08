package com.financemanager.webapp.dto;

import com.financemanager.webapp.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

// Used for creating, viewing, and updating transactions
public class TransactionDTO {

    private Long id;
    private TransactionType type; // INCOME or EXPENSE
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Long categoryId; // ID of the associated category
    private String categoryName; // Optional: Include category name for easier display

    // We don't include userId here, as it's handled via path parameters

    // Constructors
    public TransactionDTO() {
    }

    public TransactionDTO(Long id, TransactionType type, BigDecimal amount, String description, LocalDate date, Long categoryId, String categoryName) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // toString() - Optional
    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}