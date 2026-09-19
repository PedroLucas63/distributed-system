package br.ufrn.dimap.api.types;

public record LiveNodesRequest(
    long nodeId,
    String password
) {
}
