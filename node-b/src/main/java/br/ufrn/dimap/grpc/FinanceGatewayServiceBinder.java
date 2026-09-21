package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import br.ufrn.dimap.api.managers.NodeManager;
import io.grpc.ServerServiceDefinition;

public class FinanceGatewayServiceBinder implements IServiceBinder {
    private final String prefix;
    private final NodeManager nodeManager;

    public FinanceGatewayServiceBinder(String prefix, NodeManager nodeManager) {
        this.prefix = prefix;
        this.nodeManager = nodeManager;
    }

    @Override
    public ServerServiceDefinition bind() {
        return FinanceServiceGrpc.bindService(
            new FinanceGatewayServiceImpl(prefix, nodeManager)
        );
    }
}
