package br.ufrn.dimap.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.math.BigDecimal;

public class CalcServiceImpl extends CalcServiceGrpc.CalcServiceImplBase {
    @Override
    public void add(
        OperationRequest request,
        StreamObserver<OperationResponse> responseObserver
    ) {
        var result = request.getFirst() + request.getSecond();
        var response =  OperationResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sub(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var result = request.getFirst() - request.getSecond();
        var response =  OperationResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mult(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var result = request.getFirst() * request.getSecond();
        var response =  OperationResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void div(
            OperationRequest request,
            StreamObserver<OperationResponse> responseObserver
    ) {
        var result = request.getFirst() / request.getSecond();
        var response =  OperationResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
