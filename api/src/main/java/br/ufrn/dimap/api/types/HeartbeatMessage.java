package br.ufrn.dimap.api.types;

public record HeartbeatMessage(
    long nodeId,
    String password
) {
}
