package br.ufrn.dimap.api.grpc;

import br.ufrn.dimap.api.grpc.GatewayServiceGrpc.GatewayServiceImplBase;
import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.options.GatewayOptions;
import br.ufrn.dimap.api.types.NodeInfo;
import io.grpc.stub.StreamObserver;

public class GatewayServiceImpl extends GatewayServiceImplBase {
    private final GatewayOptions options;
    private final NodeManager  nodeManager;

    public GatewayServiceImpl(GatewayOptions options, NodeManager nodeManager) {
        this.options = options;
        this.nodeManager = nodeManager;
    }

    @Override
    public void pulse(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        if(!request.getPassword().equals(options.getPassword())) {
            responseObserver.onNext(
                HeartbeatResponse
                    .newBuilder()
                        .setSuccess(false)
                        .build()
            );
        } else {
            nodeManager.registerHeartbeat(request.getNodeId());

            responseObserver.onNext(
                HeartbeatResponse
                    .newBuilder()
                        .setSuccess(true)
                        .build()
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void subscriber(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        if(!request.getPassword().equals(options.getPassword())) {
            responseObserver.onNext(
                SubscribeResponse
                    .newBuilder()
                        .setSuccess(false)
                        .build()
            );
        } else {
            var requestInfo = request.getInfo();
            Integer configPort = null;
            if (requestInfo.hasConfigPort()) {
                configPort = requestInfo.getConfigPort();
            }

            var info = new NodeInfo(
                requestInfo.getAddress(),
                requestInfo.getPrefix(),
                requestInfo.getUdpPort(),
                requestInfo.getHttpPort(),
                requestInfo.getGrpcPort(),
                configPort
            );

            var nodeId = nodeManager.registerNode(info);

            responseObserver.onNext(
                SubscribeResponse
                    .newBuilder()
                        .setSuccess(true)
                        .setNodeId(nodeId)
                        .build()
            );
        }

        responseObserver.onCompleted();
    }
    @Override
    public void getLiveNodes(LiveNodesRequest request, StreamObserver<NodeInfoMessage> responseObserver) {
        if(request.getPassword().equals(options.getPassword())) {
            var nodes = nodeManager.getLiveNodesBySamePrefix(request.getNodeId());

            for (var node : nodes) {
                var message = NodeInfoMessage
                    .newBuilder()
                        .setAddress(node.address())
                        .setPrefix(node.prefix())
                        .setUdpPort(node.udpPort())
                        .setHttpPort(node.httpPort())
                        .setGrpcPort(node.grpcPort())
                        .build();
                responseObserver.onNext(message);
            }
        }

        responseObserver.onCompleted();
    }
}
