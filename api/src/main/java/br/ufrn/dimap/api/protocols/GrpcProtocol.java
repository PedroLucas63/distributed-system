package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.grpc.IServiceBinder;
import br.ufrn.dimap.api.options.ServerOptions;
import io.grpc.ServerBuilder;

import java.util.List;

public class GrpcProtocol implements IProtocol {
    private final ServerOptions options;
    private final List<IServiceBinder> binders;

    public GrpcProtocol(ServerOptions options, List<IServiceBinder> binders) {
        this.options = options;
        this.binders = binders;
    }

    @Override
    public void start() {
        var serverBuilder = ServerBuilder.forPort(options.getGrpcPort());

        for (var binder : binders) {
            serverBuilder.addService(binder.bind());
        }

        var server = serverBuilder.build();

        try {
            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            System.out.println("[GRPC] Error: " + e.getMessage());
        } finally {
            server.shutdown();
        }
    }
}
