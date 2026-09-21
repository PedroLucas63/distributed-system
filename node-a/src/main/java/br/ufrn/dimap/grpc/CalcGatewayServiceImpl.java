package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.managers.NodeManager;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalcGatewayServiceImpl extends CalcServiceGrpc.CalcServiceImplBase
{
    private final String prefix;
    private final NodeManager nodeManager;

    public CalcGatewayServiceImpl(String prefix, NodeManager nodeManager) {
        this.prefix = prefix;
        this.nodeManager = nodeManager;
    }

    @Override
    public void add(
        OperationRequest request,
        StreamObserver<OperationResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = CalcServiceGrpc.newStub(channel);

        stub.add(request, responseObserver);
    }

    @Override
    public void sub(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = CalcServiceGrpc.newStub(channel);

        stub.sub(request, responseObserver);
    }

    @Override
    public void mult(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = CalcServiceGrpc.newStub(channel);

        stub.mult(request, responseObserver);
    }

    @Override
    public void div(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = CalcServiceGrpc.newStub(channel);

        stub.div(request, responseObserver);
    }

    private Channel getChannel() {
        var connection = nodeManager.getNextLiveNode(prefix);
        if (connection == null) {
            throw Status.UNAVAILABLE
                .withDescription("No nodes available")
                .asRuntimeException();
        }

        return connection.getGrpcChannel();
    }
}
