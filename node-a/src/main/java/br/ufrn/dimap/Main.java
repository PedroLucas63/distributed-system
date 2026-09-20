package br.ufrn.dimap;

import br.ufrn.dimap.api.Node;
import br.ufrn.dimap.api.nodes.NodeHeartbeat;
import br.ufrn.dimap.api.nodes.NodeRegister;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.api.protocols.GrpcProtocol;
import br.ufrn.dimap.api.protocols.HttpNodeProtocol;
import br.ufrn.dimap.api.protocols.UdpNodeProtocol;
import br.ufrn.dimap.application.service.Bank;
import br.ufrn.dimap.grpc.BbServiceBinder;
import br.ufrn.dimap.handlers.BbRequestHandler;
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

            var httpResponseOptions = getHttpResponseOptions();
            var httpResponseFactory = new HttpResponseFactory(httpResponseOptions);

            var bank = new Bank();
            var requestHandler = new BbRequestHandler(bank, options, httpResponseFactory);

            var httpRequestOptions = getHttpRequestOptions();
            var httpRequestFactory = new HttpRequestFactory(httpRequestOptions);

            var register = new NodeRegister(options, httpRequestFactory);

            var heartbeatTimer = Duration.ofSeconds(3);
            var heartbeat = new NodeHeartbeat(
                options, httpRequestFactory, heartbeatTimer
            );

            var httpProtocol = new HttpNodeProtocol(options, requestHandler);
            var udpProtocol = new UdpNodeProtocol(options, requestHandler);

            var grpcBinder = new BbServiceBinder(bank);
            var grpcProtocol = new GrpcProtocol(options, List.of(grpcBinder));

            var node = new Node(
              options, register, heartbeat,
              List.of(httpProtocol, udpProtocol, grpcProtocol)
            );

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

        return new NodeOptions.Builder()
            .gatewayAddress(
                InetAddress.getByName(properties.getProperty("gateway.address"))
            )
            .gatewayUdpPort(Integer.parseInt(properties.getProperty("gateway.udp.port")))
            .gatewayHttpPort(Integer.parseInt(properties.getProperty("gateway.http.port")))
            .gatewayGrpcPort(Integer.parseInt(properties.getProperty("gateway.grpd.port")))
            .httpPort(Integer.parseInt(properties.getProperty("http.port")))
            .udpPort(Integer.parseInt(properties.getProperty("udp.port")))
            .grpcPort(Integer.parseInt(properties.getProperty("grpc.port")))
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
