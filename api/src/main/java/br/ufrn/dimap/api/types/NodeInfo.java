package br.ufrn.dimap.api.types;

public record NodeInfo(
    String address,
    String prefix,
    int udpPort,
    int httpPort,
    int grpcPort,
    Integer configPort
) {
}
