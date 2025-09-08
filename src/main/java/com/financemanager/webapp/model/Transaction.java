package com.financemanager.webapp.model;

import jakarta.persistence.*; // Or javax.persistence.*
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Store enum name ("INCOME", "EXPENSE") in DB
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4) // Good precision for currency
    private BigDecimal amount;

    @Column(length = 255) // Optional description
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    // --- Relationships ---

    // Many transactions belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many transactions belong to one category
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // --- Constructors ---
    public Transaction() {
    }

    public Transaction(TransactionType type, BigDecimal amount, String description, LocalDate date, User user, Category category) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.user = user;
        this.category = category;
    }

    // --- Getters and Setters ---
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // --- equals, hashCode, toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        // Check equality based on ID if available
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        // Avoid using mutable fields or relationships in equals/hashCode if possible
        // If no ID, equality is complex for entities. Often, reference equality is sufficient
        // or rely solely on the database primary key.
        return false; // Or a more complex comparison if needed before persistence
    }

    @Override
    public int hashCode() {
        // Use ID if available
        if (id != null) {
            return Objects.hash(id);
        }
        // Fallback - potentially problematic if fields change before ID assignment
        return Objects.hash(type, amount, description, date, user, category);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", userId=" + (user != null ? user.getId() : null) +
                ", categoryId=" + (category != null ? category.getId() : null) +
                '}';
    }
}