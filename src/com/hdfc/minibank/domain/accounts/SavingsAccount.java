package com.hdfc.minibank.domain.accounts;

import java.math.BigDecimal;

public class SavingsAccount extends Account{

    private static final BigDecimal MIN_BALANCE = BigDecimal.valueOf(1000);
    private static final double INTEREST_RATE = 4.5;

    public SavingsAccount(BigDecimal balance) {
        super(balance);
    }

    @Override
    public boolean canWithdraw(BigDecimal amount) {
        return balance.subtract(amount).compareTo(MIN_BALANCE) >= 0;
    }

    @Override
    public BigDecimal getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public BigDecimal getInterest() {
        return balance.multiply(BigDecimal.valueOf(INTEREST_RATE/100));
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}
