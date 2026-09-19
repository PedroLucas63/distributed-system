package br.ufrn.dimap.api.types;

public record SubscribeRequest(
    NodeInfo nodeInfo,
    String password
) {
}
