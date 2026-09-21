package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.managers.NodeManager;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class FinanceGatewayServiceImpl extends FinanceServiceGrpc.FinanceServiceImplBase
{
    private final String prefix;
    private final NodeManager nodeManager;

    public FinanceGatewayServiceImpl(String prefix, NodeManager nodeManager) {
        this.prefix = prefix;
        this.nodeManager = nodeManager;
    }

    @Override
    public void simpleInterest(
        InterestRequest request,
        StreamObserver<ValueResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = FinanceServiceGrpc.newStub(channel);

        stub.simpleInterest(request, responseObserver);
    }

    @Override
    public void compoundInterest(
            InterestRequest request,
            StreamObserver<ValueResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = FinanceServiceGrpc.newStub(channel);

        stub.compoundInterest(request, responseObserver);
    }

    @Override
    public void discount(
            DiscountRequest request,
            StreamObserver<ValueResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = FinanceServiceGrpc.newStub(channel);

        stub.discount(request, responseObserver);
    }

    @Override
    public void installments(InstallmentsRequest request,
                             StreamObserver<InstallmentResponse> responseObserver) {
        var channel = getChannel();
        var stub = FinanceServiceGrpc.newStub(channel);

        stub.installments(request, responseObserver);
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
