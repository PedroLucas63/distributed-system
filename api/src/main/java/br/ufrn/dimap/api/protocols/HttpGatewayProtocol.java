package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.GatewayOptions;
import br.ufrn.dimap.http.exceptions.HttpException;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.IOException;
public class HttpGatewayProtocol extends AbstractHttpProtocol {
    private final IRequestHandler requestHandler;
    private final HttpResponseFactory responseFactory;

    public HttpGatewayProtocol(
        GatewayOptions options,
        IRequestHandler requestHandler,
        HttpResponseFactory responseFactory
    ) {
        super(options);
        this.requestHandler = requestHandler;
        this.responseFactory = responseFactory;
    }

    @Override
    protected void processRequest(HttpConnection connection) {
        try (connection) {
            HttpResponse response;

            try {
                var request = connection.readRequest();
                response = requestHandler.handle(request);
            } catch (HttpException e) {
                response = responseFactory.badRequest("Invalid request: " + e.getMessage());
            } catch (Exception e) {
                response = responseFactory.internalServerError("Internal server error: " + e.getMessage());
            }

            connection.write(response);
        } catch (IOException e) {
            System.out.println("[HTTP] Connection error: " + e.getMessage());
        }
    }
}
