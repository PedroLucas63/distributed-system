package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import io.grpc.ServerServiceDefinition;
import br.ufrn.dimap.service.MasterBank;

public class MasterServiceBinder implements IServiceBinder {
    private final MasterBank bank;

    public MasterServiceBinder(MasterBank bank) {
        this.bank = bank;
    }

    @Override
    public ServerServiceDefinition bind() {
        return MasterBankServiceGrpc.bindService(
            new MasterServiceImpl(bank)
        );
    }
}
