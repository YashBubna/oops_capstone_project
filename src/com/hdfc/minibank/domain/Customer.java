package com.hdfc.minibank.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Customer {
    private final String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;


    public Customer(String customerId, String name, String email, String phoneNumber, LocalDate dateOfBirth) {
        if(!isValidEmail(email) || !isValidPhoneNumber(phoneNumber)){
            throw new IllegalArgumentException("Invalid email or number format");
        }
        this.customerId = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    private boolean isValidEmail(String email){
        return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$", email);
    }

    private boolean isValidPhoneNumber(String phoneNumber){
        return Pattern.matches("^[6-9]\\d{9}$", phoneNumber);
    }

    public String getCustomerId() {
        return customerId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Customer)) return false;
        Customer customer = (Customer) obj;
        return customerId.equals(customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
