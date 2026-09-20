package br.ufrn.dimap.api.nodes;

import br.ufrn.dimap.http.factories.HttpRequestFactory;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.api.types.NodeInfo;
import br.ufrn.dimap.api.types.SubscribeRequest;
import br.ufrn.dimap.api.types.SubscribeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NodeRegister {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final NodeOptions options;
    private final HttpRequestFactory requestFactory;

    public NodeRegister(NodeOptions options, HttpRequestFactory requestFactory) {
        this.options = options;
        this.requestFactory = requestFactory;
    }

    public long register() throws IOException {
        try (var connection = new HttpConnection()) {
            connection.connect(options.getGatewayAddress(), options.getGatewayHttpPort());

            var message = createSubscribeRequest();
            var request = requestFactory.post("/subscribe", message);
            connection.write(request);

            var response = connection.readResponse();
            var subscribeResponse = MAPPER.readValue(
                    response.body(),
                    SubscribeResponse.class
            );

            if (!subscribeResponse.success()) {
                throw new IllegalStateException(
                        "Failed to subscribe in Gateway. Check password or Gateway status."
                );
            }

            return subscribeResponse.nodeId();
        }
    }

    private SubscribeRequest createSubscribeRequest() throws UnknownHostException {
        return new SubscribeRequest(
                getNodeInfo(),
                options.getPassword()
        );
    }

    private NodeInfo getNodeInfo() throws UnknownHostException {
        {
            var hostName = InetAddress.getLocalHost().getHostName();
            var addresses = InetAddress.getAllByName(hostName);
            var address = Arrays.stream(addresses)
                    .filter(ip -> ip.getAddress().length == 4)
                    .findFirst()
                    .orElseThrow();

            return new NodeInfo(
                    address.getHostAddress(),
                    options.getPrefix(),
                    options.getUdpPort(),
                    options.getHttpPort(),
                    options.getGrpcPort(),
                    options.getConfigPort()
            );
        }
    }
}