package br.ufrn.dimap.api.grpc;

import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.options.GatewayOptions;
import io.grpc.ServerServiceDefinition;

public class GatewayServiceBinder implements IServiceBinder {
    private final GatewayOptions gatewayOptions;
    private final NodeManager nodeManager;

    public GatewayServiceBinder(GatewayOptions gatewayOptions, NodeManager nodeManager) {
        this.gatewayOptions = gatewayOptions;
        this.nodeManager = nodeManager;
    }

    @Override
    public ServerServiceDefinition bind() {
        return GatewayServiceGrpc.bindService(
            new GatewayServiceImpl(gatewayOptions, nodeManager)
        );
    }
}
