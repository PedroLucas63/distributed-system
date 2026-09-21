package br.ufrn.dimap.grpc;

import io.grpc.stub.StreamObserver;

public class FinanceServiceImpl extends FinanceServiceGrpc.FinanceServiceImplBase {

    @Override
    public void simpleInterest(
            InterestRequest request,
            StreamObserver<ValueResponse> responseObserver
    ) {
        var principal = request.getPrincipal();
        var rate = request.getRate() / 100.0;
        var periods = request.getPeriods();

        var result = principal * (1 + rate * periods);

        var response = ValueResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void compoundInterest(
            InterestRequest request,
            StreamObserver<ValueResponse> responseObserver
    ) {
        var principal = request.getPrincipal();
        var rate = request.getRate() / 100.0;
        var periods = request.getPeriods();

        var result = principal * Math.pow(1 + rate, periods);

        var response = ValueResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void discount(
            DiscountRequest request,
            StreamObserver<ValueResponse> responseObserver
    ) {
        var value = request.getValue();
        var percentage = request.getPercentage() / 100.0;

        var result = value * (1 - percentage);

        var response = ValueResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void installments(
            InstallmentsRequest request,
            StreamObserver<InstallmentResponse> responseObserver
    ) {
        var principal = request.getPrincipal();
        var rate = request.getRate() / 100.0;
        var numberOfInstallments = request.getNumberOfInstallments();

        var amortization = principal / numberOfInstallments;
        var balance = principal;

        for (int i = 0; i < numberOfInstallments; i++) {
            var interest = balance * rate;
            var payment = amortization + interest;

            balance -= amortization;

            if (Math.abs(balance) < 0.000001) {
                balance = 0.0;
            }

            var response = InstallmentResponse.newBuilder()
                    .setNumber(i + 1)
                    .setInterest(interest)
                    .setAmortization(amortization)
                    .setPayment(payment)
                    .setRemainingBalance(balance)
                    .build();

            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}