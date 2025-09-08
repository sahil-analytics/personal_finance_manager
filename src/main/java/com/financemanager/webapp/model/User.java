package com.financemanager.webapp.model;

import jakarta.persistence.*; // Using jakarta for Spring Boot 3+

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users") // Optional: Specify table name, default is class name
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use database auto-increment
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255) // Store password (plain text for now, NOT SECURE)
    private String password;

    @Column(name = "preferred_currency", length = 3) // e.g., "USD"
    private String preferredCurrency;

    // --- Relationships ---

    // One user can have many categories
    // mappedBy indicates the field in the Category entity that owns the relationship
    // CascadeType.ALL: If user is deleted, their categories are also deleted.
    // OrphanRemoval=true: If a category is removed from this list, it gets deleted from DB.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    // One user can have many transactions
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // --- Constructors ---
    public User() {
    }

    public User(String name, String email, String password, String preferredCurrency) {
        this.name = name;
        this.email = email;
        this.password = password; // In real app, hash the password here or in service
        this.preferredCurrency = preferredCurrency;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // In real app, hash the password here or in service
        this.password = password;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // --- Helper methods for bidirectional relationships (optional but good practice) ---
    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setUser(null);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setUser(this);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setUser(null);
    }

    // --- equals, hashCode, toString (optional but recommended) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Use email for equality check, assuming it's unique and stable
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        // Use email for hash code
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                // Avoid logging password
                ", preferredCurrency='" + preferredCurrency + '\'' +
                '}';
    }
}