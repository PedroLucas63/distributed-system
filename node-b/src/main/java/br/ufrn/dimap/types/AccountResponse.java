package br.ufrn.dimap.types;

import java.math.BigDecimal;

public record AccountResponse(
    String name,
    long accountNumber,
    BigDecimal balance
) {
}
