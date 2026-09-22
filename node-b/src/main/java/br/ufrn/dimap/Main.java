package br.ufrn.dimap;

import br.ufrn.dimap.api.Node;
import br.ufrn.dimap.api.managers.LifecycleManager;
import br.ufrn.dimap.api.nodes.NodeHeartbeat;
import br.ufrn.dimap.api.nodes.NodeRegister;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.api.protocols.GrpcProtocol;
import br.ufrn.dimap.api.protocols.HttpNodeProtocol;
import br.ufrn.dimap.api.protocols.UdpNodeProtocol;
import br.ufrn.dimap.grpc.FinanceServiceBinder;
import br.ufrn.dimap.handlers.FinanceRequestHandler;
import br.ufrn.dimap.http.factories.HttpRequestFactory;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.options.HttpMessageOptions;
import br.ufrn.dimap.http.types.HttpVersion;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Main {
    static void main() {
        try {
            var options = getNodeOptions();

            if (options.getConfigPort() != null) {
                var httpResponseOptions = getHttpResponseOptions();
                var httpResponseFactory = new HttpResponseFactory(httpResponseOptions);

                LifecycleManager lifecycleManager = new LifecycleManager(options, httpResponseFactory, Main::startNode);
                lifecycleManager.start();
            } else {
                startNode(options);
            }
        } catch (IOException e) {
            System.out.println("Error is started server");
        }
    }

    static void startNode(NodeOptions options) {
        try {
            System.out.println("Iniciando nó A...");

            var httpResponseOptions = getHttpResponseOptions();
            var httpResponseFactory = new HttpResponseFactory(httpResponseOptions);

            System.out.println("\nConfigurações do sistema:");
            System.out.println(" - Node Id: " + options.getNodeId());
            System.out.println(" - Gateway Address: " + options.getGatewayAddress());
            System.out.println(" - Gateway HTTP Port: " + options.getGatewayHttpPort());
            System.out.println(" - Gateway UDP Port: " + options.getGatewayUdpPort());
            System.out.println(" - Gateway gRPC Port: " + options.getGatewayGrpcPort());
            System.out.println(" - HTTP Port: " + options.getHttpPort());
            System.out.println(" - UDP Port: " + options.getUdpPort());
            System.out.println(" - gRPC Port: " + options.getGrpcPort());
            System.out.println(" - Config Port: " + options.getConfigPort());
            System.out.println(" - Prefix: " + options.getPrefix());
            System.out.println(" - Password: " + options.getPassword());

            System.out.println("\nConfigurações das mensagens de response:");
            System.out.println(" - HTTP Version: " + httpResponseOptions.getVersion());
            System.out.println(" - Include Date: " + httpResponseOptions.isIncludeDateHeader());
            System.out.println(" - Include Server: " + httpResponseOptions.isIncludeServerHeader());
            System.out.println(" - Server: " + httpResponseOptions.getServer());
            System.out.println(" - Default headers: ");
            for (var header : httpResponseOptions.getDefaultHeaders().entrySet()) {
                var  key = header.getKey();
                var value = header.getValue();

                System.out.println("  - " + key + ": " + value);
            }

            var requestHandler = new FinanceRequestHandler(options, httpResponseFactory);

            var httpRequestOptions = getHttpRequestOptions();
            var httpRequestFactory = new HttpRequestFactory(httpRequestOptions);

            System.out.println("\nConfigurações das mensagens de request:");
            System.out.println(" - HTTP Version: " + httpRequestOptions.getVersion());
            System.out.println(" - Include Date: " + httpRequestOptions.isIncludeDateHeader());
            System.out.println(" - Include Server: " + httpRequestOptions.isIncludeServerHeader());
            System.out.println(" - Server: " + httpRequestOptions.getServer());
            System.out.println(" - Default headers: ");
            for (var header : httpRequestOptions.getDefaultHeaders().entrySet()) {
                var  key = header.getKey();
                var value = header.getValue();

                System.out.println("  - " + key + ": " + value);
            }

            var register = new NodeRegister(options, httpRequestFactory);

            var heartbeatTimer = Duration.ofSeconds(3);
            var heartbeat = new NodeHeartbeat(
                options, httpRequestFactory, heartbeatTimer
            );

            var httpProtocol = new HttpNodeProtocol(options, requestHandler);
            var udpProtocol = new UdpNodeProtocol(options, requestHandler);

            var grpcBinder = new FinanceServiceBinder();
            var grpcProtocol = new GrpcProtocol(options, List.of(grpcBinder));

            var node = new Node(
              options, register, heartbeat,
              List.of(httpProtocol, udpProtocol, grpcProtocol)
            );

            System.out.println("Funcionando...");
            
            node.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static NodeOptions getNodeOptions() throws IOException {
        var properties = new Properties();

        try (var input = Files.newInputStream(Path.of("node.properties"))) {
            properties.load(input);
        }

        var configPortStr = properties.getProperty("config.port");
        Integer configPort = null;
        if (configPortStr != null) {
            configPort = Integer.parseInt(configPortStr);
        }

        var nodeIdStr = properties.getProperty("node.id");
        Long nodeId = null;
        if (nodeIdStr != null) {
            nodeId = Long.parseLong(nodeIdStr);
        }

        return new NodeOptions.Builder()
            .nodeId(nodeId)
            .gatewayAddress(
                InetAddress.getByName(properties.getProperty("gateway.address"))
            )
            .gatewayUdpPort(Integer.parseInt(properties.getProperty("gateway.udp.port")))
            .gatewayHttpPort(Integer.parseInt(properties.getProperty("gateway.http.port")))
            .gatewayGrpcPort(Integer.parseInt(properties.getProperty("gateway.grpc.port")))
            .httpPort(Integer.parseInt(properties.getProperty("http.port")))
            .udpPort(Integer.parseInt(properties.getProperty("udp.port")))
            .grpcPort(Integer.parseInt(properties.getProperty("grpc.port")))
            .configPort(configPort)
            .prefix(properties.getProperty("prefix"))
            .password(properties.getProperty("password"))
            .build();
    }

    private static HttpMessageOptions getHttpResponseOptions() throws IOException {
        var properties = new Properties();

        try (var input = Files.newInputStream(Path.of("http-response.properties"))) {
            properties.load(input);
        }

        return new HttpMessageOptions.Builder()
            .includeDateHeader(Boolean.parseBoolean(properties.getProperty("date")))
            .includeServerHeader(Boolean.parseBoolean(properties.getProperty("server.include")))
            .server(properties.getProperty("server.url"))
            .version(HttpVersion.fromValue(properties.getProperty("version")))
            .build();
    }

    private static HttpMessageOptions getHttpRequestOptions() throws IOException {
        var properties = new Properties();

        try (var input = Files.newInputStream(Path.of("http-request.properties"))) {
            properties.load(input);
        }

        var headers = properties.stringPropertyNames()
            .stream()
            .filter(name -> name.startsWith("header."))
            .collect(Collectors.toMap(
                name -> name.substring("header.".length()), properties::getProperty
            ));

        return new HttpMessageOptions.Builder()
            .includeDateHeader(Boolean.parseBoolean(properties.getProperty("date")))
            .version(HttpVersion.fromValue(properties.getProperty("version")))
            .defaultHeaders(headers)
            .build();
    }
}
