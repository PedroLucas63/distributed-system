package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.GatewayOptions;
import br.ufrn.dimap.http.exceptions.HttpException;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.parser.HttpParser;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpGatewayProtocol extends AbstractUdpProtocol {
    private final IRequestHandler requestHandler;
    private final HttpResponseFactory responseFactory;

    public UdpGatewayProtocol(
        GatewayOptions options,
        IRequestHandler requestHandler,
        HttpResponseFactory responseFactory
    ) {
        super(options);
        this.requestHandler = requestHandler;
        this.responseFactory = responseFactory;
    }

    @Override
    protected void processRequest(DatagramSocket listener, DatagramPacket result)  {
        try {
            HttpResponse response;

            try {
                var memoryStream = new ByteArrayInputStream(result.getData());
                var request = HttpParser.parseRequest(memoryStream);
                response = requestHandler.handle(request);
            } catch (HttpException e) {
                response = responseFactory.badRequest("Invalid request: " + e.getMessage());
            } catch (Exception e) {
                response = responseFactory.internalServerError("Internal server error: " + e.getMessage());
            }

            var buffer = HttpMessageSerializer.serialize(response);
            var packet = new DatagramPacket(buffer, buffer.length, result.getAddress(), result.getPort());
            listener.send(packet);
        } catch (IOException e) {
            System.out.println("[UDP] Connection error: " + e.getMessage());
        }
    }
}
