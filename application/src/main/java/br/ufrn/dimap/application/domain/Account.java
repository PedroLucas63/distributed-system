package br.ufrn.dimap.application.domain;

import java.math.BigDecimal;

public class Account {
    private final String name;
    private final long accountNumber;
    private BigDecimal balance;

    public Account(String name, long accountNumber) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
    }

    public String getName() {
        return name;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) return;
        this.balance = balance.subtract(amount);
    }
}
