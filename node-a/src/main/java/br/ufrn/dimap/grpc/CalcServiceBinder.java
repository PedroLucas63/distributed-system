package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import io.grpc.ServerServiceDefinition;

public class CalcServiceBinder implements IServiceBinder {
    @Override
    public ServerServiceDefinition bind() {
        return CalcServiceGrpc.bindService(
            new CalcServiceImpl()
        );
    }
}
