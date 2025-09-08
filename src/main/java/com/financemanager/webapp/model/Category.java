package com.financemanager.webapp.model;
import jakarta.persistence.*; // Or javax.persistence.*
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // --- Relationships ---

    // Many categories belong to one user
    // FetchType.LAZY: User data is loaded only when explicitly accessed (e.g., category.getUser())
    // JoinColumn specifies the foreign key column in the categories table
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A category must belong to a user
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One category can have many transactions
    // If a category is deleted, we might want to nullify the category_id in transactions
    // or prevent deletion if transactions exist. CascadeType.ALL is usually NOT desired here.
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // --- Constructors ---
    public Category() {
    }

    public Category(String name, User user) {
        this.name = name;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // --- Helper methods (optional) ---
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setCategory(this);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setCategory(null);
    }

    // --- equals, hashCode, toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        // Check equality based on ID if available, otherwise rely on user and name
        if (id != null && category.id != null) {
            return Objects.equals(id, category.id);
        }
        // Be careful comparing entities without IDs or using mutable fields
        return Objects.equals(name, category.name) &&
                Objects.equals(user, category.user); // Relies on User's equals/hashCode
    }

    @Override
    public int hashCode() {
        // Use ID if available, otherwise user and name
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(name, user); // Relies on User's equals/hashCode
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                // Avoid infinite loop by not printing user directly if User.toString prints categories
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}