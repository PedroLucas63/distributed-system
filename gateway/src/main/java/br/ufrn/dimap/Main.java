package br.ufrn.dimap;

import br.ufrn.dimap.api.Gateway;
import br.ufrn.dimap.api.grpc.GatewayServiceBinder;
import br.ufrn.dimap.api.handlers.GatewayRequestHandler;
import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.options.GatewayOptions;
import br.ufrn.dimap.api.protocols.GrpcProtocol;
import br.ufrn.dimap.api.protocols.HttpGatewayProtocol;
import br.ufrn.dimap.api.protocols.UdpGatewayProtocol;
import br.ufrn.dimap.api.routing.HttpNodeRouter;
import br.ufrn.dimap.api.routing.UdpNodeRouter;
import br.ufrn.dimap.grpc.BbGatewayServiceBinder;
import br.ufrn.dimap.grpc.MasterGatewayServiceBinder;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.options.HttpMessageOptions;
import br.ufrn.dimap.http.types.HttpVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class Main {
    static void main() {
        try {
            var options = getGatewayOptions();
            var httpMessageOptions = getHttpMessageOptions();

            var httpResponseFactory = new HttpResponseFactory(httpMessageOptions);

            var timeout = Duration.ofSeconds(10);
            var nodeManager = new NodeManager(timeout);

            var httpNodeRouter = new HttpNodeRouter(nodeManager, httpResponseFactory);
            var httpRequestHandler = new GatewayRequestHandler(
                options, nodeManager, httpResponseFactory, httpNodeRouter
            );
            var httpProtocol = new HttpGatewayProtocol(
                options, httpRequestHandler, httpResponseFactory
            );

            var udpNodeRouter = new UdpNodeRouter(nodeManager, httpResponseFactory);
            var udpRequestHandler = new GatewayRequestHandler(
                options, nodeManager, httpResponseFactory, udpNodeRouter
            );
            var udpProtocol = new UdpGatewayProtocol(
                options, udpRequestHandler, httpResponseFactory
            );

            var grpcInternalBinder = new GatewayServiceBinder(options, nodeManager);
            var grpcBbBinder = new BbGatewayServiceBinder("bb", nodeManager);
            var grpcMasterBinder = new MasterGatewayServiceBinder("master", nodeManager);
            var grpcProtocol =  new GrpcProtocol(
                options, List.of(grpcInternalBinder, grpcBbBinder, grpcMasterBinder)
            );

            var gateway = new Gateway(List.of(httpProtocol, udpProtocol, grpcProtocol));
            gateway.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static GatewayOptions getGatewayOptions() throws IOException {
        var properties = new Properties();

        try (var input = Files.newInputStream(Path.of("gateway.properties"))) {
            properties.load(input);
        }

        return new GatewayOptions.Builder()
            .httpPort(Integer.parseInt(properties.getProperty("http.port")))
            .udpPort(Integer.parseInt(properties.getProperty("udp.port")))
            .grpcPort(Integer.parseInt(properties.getProperty("grpc.port")))
            .password(properties.getProperty("password"))
            .build();
    }

    private static HttpMessageOptions getHttpMessageOptions() throws IOException {
        var properties = new Properties();

        try (var input = Files.newInputStream(Path.of("http.properties"))) {
            properties.load(input);
        }

        return new HttpMessageOptions.Builder()
            .includeDateHeader(Boolean.parseBoolean(properties.getProperty("date")))
            .includeServerHeader(Boolean.parseBoolean(properties.getProperty("server.include")))
            .server(properties.getProperty("server.url"))
            .version(HttpVersion.fromValue(properties.getProperty("version")))
            .build();
    }
}
