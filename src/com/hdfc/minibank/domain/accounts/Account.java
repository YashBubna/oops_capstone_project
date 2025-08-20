package com.hdfc.minibank.domain.accounts;

import com.hdfc.minibank.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Account {
    protected String accountNumber;
    protected BigDecimal balance;
    protected LocalDateTime createdAt;

    public Account(BigDecimal balance) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public synchronized void deposit(BigDecimal amount){
        balance =  balance.add(amount);
    }

    public synchronized void withdraw(BigDecimal amount) throws InsufficientBalanceException{
        if(!canWithdraw(amount)){
            throw new InsufficientBalanceException("Insufficient Balance for withdrawal");
        }
        balance = balance.subtract(amount);
    }

    public abstract boolean canWithdraw(BigDecimal amount);
    public abstract BigDecimal getMinimumBalance();
    public abstract BigDecimal getInterest();
    public abstract double getInterestRate();

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
