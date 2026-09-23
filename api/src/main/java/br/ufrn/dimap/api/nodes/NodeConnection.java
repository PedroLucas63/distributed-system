package br.ufrn.dimap.api.nodes;

import br.ufrn.dimap.http.parser.HttpParser;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.api.types.NodeInfo;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class NodeConnection {

    private long nodeId;
    private final NodeInfo nodeInfo;
    private final ManagedChannel channel;

    public NodeConnection(long nodeId, NodeInfo nodeInfo) {
        this.nodeId = nodeId;
        this.nodeInfo = nodeInfo;

        this.channel = ManagedChannelBuilder
            .forAddress(nodeInfo.address(), nodeInfo.grpcPort())
            .usePlaintext()
            .build();
    }

    public HttpResponse sendHttp(HttpRequest request) throws IOException {
        try (var node = new HttpConnection(nodeInfo.address(), nodeInfo.httpPort())) {
            node.write(request);
            return node.readResponse();
        }
    }

    public HttpResponse sendConfigHttp(HttpRequest request) throws IOException {
        if (nodeInfo.configPort() == null) {
            throw new IllegalStateException("Port not configured");
        }

        try (var node = new HttpConnection(nodeInfo.address(), nodeInfo.configPort())) {
            node.write(request);
            return node.readResponse();
        }
    }

    public HttpResponse sendUdp(HttpRequest request) throws IOException, TimeoutException {
        var buffer = HttpMessageSerializer.serialize(request);

        try (var client = new DatagramSocket()) {
            var endPoint = new InetSocketAddress(nodeInfo.address(), nodeInfo.udpPort());
            client.connect(endPoint);

            client.send(new DatagramPacket(buffer, buffer.length, endPoint));

            client.setSoTimeout(5000);

            var responseBuffer = new byte[65535];
            var packet = new DatagramPacket(responseBuffer, responseBuffer.length);

            try {
                client.receive(packet);
            } catch (SocketTimeoutException e) {
                throw new TimeoutException(
                    "The node did not respond in time."
                );
            }

            try (
                var input = new ByteArrayInputStream(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength()
                )
            ) {
                return HttpParser.parseResponse(input);
            }
        }
    }

    public void stop() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
        }
    }

    public Channel getGrpcChannel() {
        return channel;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }
}
