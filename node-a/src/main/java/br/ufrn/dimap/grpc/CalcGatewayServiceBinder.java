package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import br.ufrn.dimap.api.managers.NodeManager;
import io.grpc.ServerServiceDefinition;

public class CalcGatewayServiceBinder implements IServiceBinder {
    private final String prefix;
    private final NodeManager nodeManager;

    public CalcGatewayServiceBinder(String prefix, NodeManager nodeManager) {
        this.prefix = prefix;
        this.nodeManager = nodeManager;
    }

    @Override
    public ServerServiceDefinition bind() {
        return CalcServiceGrpc.bindService(
            new CalcGatewayServiceImpl(prefix, nodeManager)
        );
    }
}
