package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.parser.HttpParser;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpNodeProtocol extends AbstractUdpProtocol {
    private final IRequestHandler requestHandler;

    public UdpNodeProtocol(NodeOptions options, IRequestHandler requestHandler) {
        super(options);
        this.requestHandler = requestHandler;
    }

    @Override
    protected void processRequest(DatagramSocket listener, DatagramPacket result)  {
        try {
            var memoryStream = new ByteArrayInputStream(result.getData());
            var request = HttpParser.parseRequest(memoryStream);

            var response = requestHandler.handle(request);
            var buffer = HttpMessageSerializer.serialize(response);
            var packet = new DatagramPacket(buffer, buffer.length, result.getAddress(), result.getPort());

            listener.send(packet);
        } catch (IOException e) {
            System.out.println("[UDP] Connection error: " + e.getMessage());
        }
    }
}
