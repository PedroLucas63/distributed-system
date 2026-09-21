package br.ufrn.dimap.types;

public record InstallmentsRequest(
        double principal,
        double rate,
        int numberOfInstallments
) {
}
