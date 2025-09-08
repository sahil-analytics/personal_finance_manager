package com.financemanager.webapp.dto;

public class RegistrationRequest {

    private String name;
    private String email;
    private String password;
    private String preferredCurrency; // e.g., "USD"

    // Constructors
    public RegistrationRequest() {
    }

    public RegistrationRequest(String name, String email, String password, String preferredCurrency) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferredCurrency = preferredCurrency;
    }

    // Getters and Setters
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
        this.password = password;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    // toString() - Optional
    @Override
    public String toString() {
        // Avoid logging password in real applications
        return "RegistrationRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='***'" +
                ", preferredCurrency='" + preferredCurrency + '\'' +
                '}';
    }
}