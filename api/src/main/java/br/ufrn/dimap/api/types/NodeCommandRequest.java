package br.ufrn.dimap.api.types;

public record NodeCommandRequest(
    String password,
    long nodeId,
    String command
) {
}
