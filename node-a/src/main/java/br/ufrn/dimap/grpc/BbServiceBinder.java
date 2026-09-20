package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import br.ufrn.dimap.application.service.Bank;
import io.grpc.ServerServiceDefinition;

public class BbServiceBinder implements IServiceBinder {
    private final Bank bank;

    public BbServiceBinder(Bank bank) {
        this.bank = bank;
    }

    @Override
    public ServerServiceDefinition bind() {
        return BankOfBrazilServiceGrpc.bindService(
            new BbServiceImpl(bank)
        );
    }
}
