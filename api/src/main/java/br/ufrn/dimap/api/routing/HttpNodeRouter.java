package br.ufrn.dimap.api.routing;

import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.nodes.NodeConnection;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.IOException;

public class HttpNodeRouter extends AbstractNodeRouter {
    public HttpNodeRouter(NodeManager nodeManager, HttpResponseFactory responseFactory) {
        super(nodeManager, responseFactory);
    }

    @Override
    protected HttpResponse send(
        NodeConnection connection, HttpRequest request
    ) throws IOException {
        return connection.sendHttp(request);
    }
}
