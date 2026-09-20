package br.ufrn.dimap.types;

import java.math.BigDecimal;

public record TransactionRequest(
    long accountNumber,
    BigDecimal amount
) {
}
