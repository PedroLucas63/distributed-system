package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import io.grpc.ServerServiceDefinition;

public class FinanceServiceBinder implements IServiceBinder {
    @Override
    public ServerServiceDefinition bind() {
        return FinanceServiceGrpc.bindService(
            new FinanceServiceImpl()
        );
    }
}
