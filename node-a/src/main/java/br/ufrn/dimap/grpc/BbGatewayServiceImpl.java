package br.ufrn.dimap.grpc;

import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.grpc.BankOfBrazilServiceGrpc.BankOfBrazilServiceImplBase;
import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BbGatewayServiceImpl extends BankOfBrazilServiceImplBase
{
    private final String prefix;
    private final NodeManager nodeManager;

    public BbGatewayServiceImpl(String prefix, NodeManager nodeManager) {
        this.prefix = prefix;
        this.nodeManager = nodeManager;
    }

    @Override
    public void createAccount(
        CreateAccountRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        var channel = getChannel();
        var stub = BankOfBrazilServiceGrpc.newStub(channel);

        stub.createAccount(request, responseObserver);
    }

    @Override
    public void withdraw(
        TransactionRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        var channel = getChannel();
        var stub = BankOfBrazilServiceGrpc.newStub(channel);

        stub.withdraw(request, responseObserver);
    }

    @Override
    public void deposit(
        TransactionRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        var channel = getChannel();
        var stub = BankOfBrazilServiceGrpc.newStub(channel);

        stub.deposit(request, responseObserver);
    }

    @Override
    public void getAccount(
        BalanceRequest request,
        StreamObserver<AccountResponseWithSuccess> responseObserver
    ) {
        var channel = getChannel();
        var stub = BankOfBrazilServiceGrpc.newStub(channel);

        stub.getAccount(request, responseObserver);
    }

    @Override
    public void getAllAccounts(
        Empty request,
        StreamObserver<AccountResponse> responseObserver
    ) {
        var channel = getChannel();
        var stub = BankOfBrazilServiceGrpc.newStub(channel);

        stub.getAllAccounts(request, responseObserver);
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
