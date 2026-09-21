package br.ufrn.dimap.api.types;

public record NodeInfo(
    Long id,
    String address,
    String prefix,
    int udpPort,
    int httpPort,
    int grpcPort,
    Integer configPort
) {
}
