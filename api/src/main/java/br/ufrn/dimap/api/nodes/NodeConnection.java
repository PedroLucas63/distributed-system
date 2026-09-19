package br.ufrn.dimap.api.nodes;

import br.ufrn.dimap.http.parser.HttpParser;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.api.types.NodeInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class NodeConnection {

    private long nodeId;
    private final NodeInfo nodeInfo;

    public NodeConnection(long nodeId, NodeInfo nodeInfo) {
        this.nodeId = nodeId;
        this.nodeInfo = nodeInfo;
    }

    public HttpResponse sendHttp(HttpRequest request) throws IOException {
        try (var node = new HttpConnection(nodeInfo.address(), nodeInfo.httpPort())) {
            node.write(request);
            return node.readResponse();
        }
    }

    public HttpResponse sendUdp(HttpRequest request) throws IOException, TimeoutException {
        var buffer = HttpMessageSerializer.serialize(request);

        try (var client = new DatagramSocket()) {
            var endPoint = new InetSocketAddress(nodeInfo.address(), nodeInfo.httpPort());
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
