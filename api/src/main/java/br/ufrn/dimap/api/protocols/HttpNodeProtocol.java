package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.sockets.HttpConnection;

import java.io.IOException;

public class HttpNodeProtocol extends AbstractHttpProtocol{
    private final IRequestHandler requestHandler;

    public HttpNodeProtocol(NodeOptions options, IRequestHandler requestHandler) {
        super(options);
        this.requestHandler = requestHandler;
    }

    @Override
    protected void processRequest(HttpConnection connection) {
        try (connection) {
            var request = connection.readRequest();
            var response = requestHandler.handle(request);
            connection.write(response);
        } catch (IOException e) {
            System.out.println("[HTTP] Connection error: " + e.getMessage());
        }
    }
}
