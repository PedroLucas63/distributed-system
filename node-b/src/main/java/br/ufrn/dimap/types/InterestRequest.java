package br.ufrn.dimap.types;

public record InterestRequest(
        double principal,
        double rate,
        int periods
) {
}
