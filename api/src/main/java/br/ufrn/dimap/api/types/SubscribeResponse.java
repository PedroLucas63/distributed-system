package br.ufrn.dimap.api.types;

public record SubscribeResponse(
    boolean success,
    long nodeId
) {
}
