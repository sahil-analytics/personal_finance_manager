package com.financemanager.webapp.dto;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String preferredCurrency; // e.g., "USD", "EUR", "GBP"

    // Constructors
    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, String preferredCurrency) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.preferredCurrency = preferredCurrency;
    }

    // Getters and Setters
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

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    // toString() - Optional, useful for debugging
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", preferredCurrency='" + preferredCurrency + '\'' +
                '}';
    }
}
