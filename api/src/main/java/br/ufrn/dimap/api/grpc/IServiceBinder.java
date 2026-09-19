package br.ufrn.dimap.api.grpc;

import io.grpc.ServerServiceDefinition;

public interface IServiceBinder {
    ServerServiceDefinition bind();
}
