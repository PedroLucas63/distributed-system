package br.ufrn.dimap.types;


public record InstallmentResponse(
        int number,
        double interest,
        double amortization,
        double payment,
        double remainingBalance
) {
}
