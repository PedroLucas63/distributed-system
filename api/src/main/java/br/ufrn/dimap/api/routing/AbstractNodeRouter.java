package br.ufrn.dimap.api.routing;

import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.nodes.NodeConnection;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public abstract class AbstractNodeRouter implements INodeRouter {
    protected final NodeManager nodeManager;
    protected final HttpResponseFactory responseFactory;

    protected AbstractNodeRouter(NodeManager nodeManager, HttpResponseFactory responseFactory) {
        this.nodeManager = nodeManager;
        this.responseFactory = responseFactory;
    }

    @Override
    public HttpResponse routeRequest(HttpRequest request) {
        var parts = Arrays.stream(request.path().split("/"))
                .filter(part -> !part.isEmpty())
                .toList();

        if (parts.isEmpty()) {
            return responseFactory.serviceUnavailable("Path not found");
        }

        var prefix = parts.getFirst();
        var connection = nodeManager.getNextLiveNode(prefix);

        if (connection == null) {
            return responseFactory.serviceUnavailable("No nodes available");
        }

        try {
            return send(connection, request);
        } catch (IOException | TimeoutException e) {
            nodeManager.registerConnectionRefused(connection.getNodeId());
            return responseFactory.serviceUnavailable("Connection refused");
        } catch (Exception e) {
            return responseFactory.internalServerError("An internal server error occurred");
        }
    }

    protected abstract HttpResponse send(
        NodeConnection connection, HttpRequest request
    ) throws IOException, TimeoutException;
}
