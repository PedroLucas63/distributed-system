package br.ufrn.dimap.application.service;

import br.ufrn.dimap.application.domain.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank {
    protected final Map<Long, Account> accounts;

    public Bank() {
        this.accounts = new HashMap<>();
    }

    public Account getAccount(Long id) {
        return this.accounts.get(id);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(this.accounts.values());
    }

    public Account createAccount(String name, Long accountNumber) throws IllegalStateException {
        if (this.accounts.containsKey(accountNumber)) {
            throw new IllegalStateException("Account already exists");
        }

        Account account = new Account(name, accountNumber);

        accounts.put(accountNumber, account);

        return account;
    }

    public Account deposit(Long accountNumber, BigDecimal amount) throws IllegalStateException {
        var account = this.accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalStateException("Account not found");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Amount must be greater than zero");
        }

        account.deposit(amount);
        return account;
    }

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

        account.withdraw(amount);
        return account;
    }
}
