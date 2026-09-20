package br.ufrn.dimap.grpc;

import br.ufrn.dimap.application.domain.Account;
import br.ufrn.dimap.application.service.Bank;
import br.ufrn.dimap.grpc.BankOfBrazilServiceGrpc.BankOfBrazilServiceImplBase;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.math.BigDecimal;

public class BbServiceImpl extends BankOfBrazilServiceImplBase {
    private final Bank bank;

    public BbServiceImpl(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void createAccount(
        CreateAccountRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        try {
            var account = bank.createAccount(request.getName(), request.getAccountNumber());
            responseObserver.onNext(toResponseWithSuccess(account));
        } catch (IllegalStateException e) {
            responseObserver.onNext(toResponseWithSuccess(null));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(
        TransactionRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        try {
            var account = bank.withdraw(
                request.getAccountNumber(),
                new BigDecimal(request.getAmount())
            );
            responseObserver.onNext(toResponseWithSuccess(account));
        } catch (IllegalStateException e) {
            responseObserver.onNext(toResponseWithSuccess(null));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void deposit(
        TransactionRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        try {
            var account = bank.deposit(
                request.getAccountNumber(),
                new BigDecimal(request.getAmount())
            );
            responseObserver.onNext(toResponseWithSuccess(account));
        } catch (IllegalStateException e) {
            responseObserver.onNext(toResponseWithSuccess(null));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getAccount(
        BalanceRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        var account = bank.getAccount(request.getAccountNumber());
        responseObserver.onNext(toResponseWithSuccess(account));

        responseObserver.onCompleted();
    }

    @Override
    public void getAllAccounts(
        Empty request,
        StreamObserver<AccountResponse> responseObserver
    ) {
        var accounts = bank.getAccounts();
        accounts.forEach(account -> {
            responseObserver.onNext(toResponse(account));
        });

        responseObserver.onCompleted();
    }

    private static AccountResponse toResponse(Account account) {
        if (account == null)
            return null;

        return AccountResponse.newBuilder()
            .setName(account.getName())
            .setAccountNumber(account.getAccountNumber())
            .setBalance(account.getBalance().doubleValue())
            .build();
    }

    private static AccountResponseWithSuccess toResponseWithSuccess(Account account) {
        if (account == null)
            return AccountResponseWithSuccess.newBuilder()
                .setSuccess(false)
                .build();

        return AccountResponseWithSuccess.newBuilder()
            .setSuccess(true)
            .setAccount(toResponse(account))
            .build();
    }
}
