package br.ufrn.dimap.types;

public record CreateAccountRequest(
    String name,
    long accountNumber
) {
}
