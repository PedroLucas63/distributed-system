package br.ufrn.dimap.api.grpc;

import br.ufrn.dimap.api.grpc.GatewayServiceGrpc.GatewayServiceImplBase;
import io.grpc.stub.StreamObserver;


public class GatewayServiceImpl extends GatewayServiceImplBase {
    @Override
    public void pulse(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        super.pulse(request, responseObserver);
    }

    @Override
    public void subscriber(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        super.subscriber(request, responseObserver);
    }
    @Override
    public void getLiveNodes(LiveNodesRequest request, StreamObserver<NodeInfoMessage> responseObserver) {
        super.getLiveNodes(request, responseObserver);

    }
}
