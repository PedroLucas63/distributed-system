package br.ufrn.dimap.service;

import br.ufrn.dimap.application.domain.Account;
import br.ufrn.dimap.application.service.Bank;

import java.math.BigDecimal;

public class MasterBank extends Bank {
    private BigDecimal amountAvailable;

    public MasterBank() {
        super();
        amountAvailable = BigDecimal.ZERO;
    }

    @Override
    public Account deposit(Long accountNumber, BigDecimal amount) throws IllegalStateException {
        var account = super.deposit(accountNumber, amount);
        amountAvailable = amountAvailable.add(amount.multiply(BigDecimal.valueOf(0.8)));
        return account;
    }


    @Override
    public Account withdraw(Long accountNumber, BigDecimal amount) throws IllegalStateException {
        var account = this.accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalStateException("Account not found");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Amount must be greater than zero");
        }

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalStateException("Amount cannot be greater than value");
        }

        if (amount.compareTo(amountAvailable) > 0) {
            amount = amountAvailable;
        }

        account.withdraw(amount);
        return account;
    }
}
