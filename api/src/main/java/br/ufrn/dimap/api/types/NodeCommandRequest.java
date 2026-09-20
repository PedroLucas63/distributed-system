package br.ufrn.dimap.api.types;

public record NodeCommandRequest(
    String password,
    String nodePrefix,
    String command
) {
}
