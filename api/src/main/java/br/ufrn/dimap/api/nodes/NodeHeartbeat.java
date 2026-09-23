package br.ufrn.dimap.api.nodes;

import br.ufrn.dimap.http.factories.HttpRequestFactory;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;
import br.ufrn.dimap.api.types.HeartbeatMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Duration;

public class NodeHeartbeat {
    private final NodeOptions options;
    private final HttpRequestFactory requestFactory;
    private final Duration interval;

    public NodeHeartbeat(
        NodeOptions options,
        HttpRequestFactory requestFactory,
        Duration interval
    ) {
        this.options = options;
        this.requestFactory = requestFactory;
        this.interval = interval;
    }

    public void start() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(interval);
                sendPulse();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {

            }
        }
    }

    private void sendPulse() throws IOException {
            try (var connection = new DatagramSocket()) {
            connection.connect(options.getGatewayAddress(), options.getGatewayUdpPort());

            var message = new HeartbeatMessage(options.getNodeId(), options.getPassword());
            var request = requestFactory.post("/heartbeat", message);

            var buffer = HttpMessageSerializer.serialize(request);
            connection.send(new DatagramPacket(buffer, buffer.length));
        }
    }
}
