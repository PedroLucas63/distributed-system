package br.ufrn.dimap.api.types;

public record NodeInfo(
    String address,
    String pathPrefix,
    int udpPort,
    int httpPort,
    int grpcPort
) {
}
