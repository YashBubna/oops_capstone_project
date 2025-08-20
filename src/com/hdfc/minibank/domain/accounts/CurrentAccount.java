package com.hdfc.minibank.domain.accounts;

import java.math.BigDecimal;

public class CurrentAccount extends Account{

    private static final BigDecimal MIN_BALANCE = BigDecimal.ZERO;
    private static final double INTEREST_RATE = 0.0;

    public CurrentAccount(BigDecimal balance) {
        super(balance);
    }

    @Override
    public boolean canWithdraw(BigDecimal amount) {
        return balance.subtract(amount).compareTo(MIN_BALANCE) >=0 ;
    }

    @Override
    public BigDecimal getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public BigDecimal getInterest() {
        return BigDecimal.ZERO;
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}
