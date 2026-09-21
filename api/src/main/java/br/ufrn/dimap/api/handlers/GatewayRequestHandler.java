package br.ufrn.dimap.api.handlers;

import br.ufrn.dimap.api.managers.NodeManager;
import br.ufrn.dimap.api.nodes.NodeConnection;
import br.ufrn.dimap.api.options.GatewayOptions;
import br.ufrn.dimap.api.routing.INodeRouter;
import br.ufrn.dimap.api.types.*;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GatewayRequestHandler implements IRequestHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final GatewayOptions gatewayOptions;
    private final NodeManager nodeManager;
    private final HttpResponseFactory responseFactory;
    private final INodeRouter nodeRouter;

    public GatewayRequestHandler(
        GatewayOptions gatewayOptions,
        NodeManager nodeManager,
        HttpResponseFactory responseFactory,
        INodeRouter nodeRouter
    ) {
        this.gatewayOptions = gatewayOptions;
        this.nodeManager = nodeManager;
        this.responseFactory = responseFactory;
        this.nodeRouter = nodeRouter;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (isInternalCall(request)) {
            return processInternalCall(request);
        }

        return nodeRouter.routeRequest(request);
    }

    private boolean isInternalCall(HttpRequest request) {
        var value = request.headers().get("X-Internal-Call");
        return value != null && value.equals("true");
    }

    private HttpResponse processInternalCall(HttpRequest request) {
        if (request.method() == HttpMethod.Post) {
            switch (request.path()) {
                case "/subscribe" -> {
                    return processSubscribe(request);
                }
                case "/heartbeat" -> {
                    return processHeartbeat(request);
                }
                case "/live-nodes" -> {
                    return processLiveNodes(request);
                }
                case "/command" -> {
                    return processNodeCommand(request);
                }
            }

        }

        return responseFactory.badRequest("Bad request");
    }

    private HttpResponse processSubscribe(HttpRequest request) {
        try {
            var subscribeRequest = MAPPER.readValue(request.body(), SubscribeRequest.class);

            if (subscribeRequest == null) {
                return responseFactory.badRequest("Bad request");
            }

            if (!subscribeRequest.password().equals(gatewayOptions.getPassword())) {
                return responseFactory.unauthorized("Wrong password");
            }

            var nodeId = nodeManager.registerNode(subscribeRequest.nodeInfo());
            return responseFactory.accepted(
                new SubscribeResponse(true, nodeId)
            );
        } catch (Exception e) {
            return responseFactory.badRequest("Bad request");
        }
    }

    private HttpResponse processHeartbeat(HttpRequest request) {
        try {
            var heartbeatRequest = MAPPER.readValue(request.body(), HeartbeatMessage.class);

            if (heartbeatRequest == null) {
                return responseFactory.badRequest("Bad request");
            }

            if (!heartbeatRequest.password().equals(gatewayOptions.getPassword())) {
                return responseFactory.unauthorized("Wrong password");
            }

            nodeManager.registerHeartbeat(heartbeatRequest.nodeId());
            return responseFactory.noContent();
        } catch (Exception e) {
            return responseFactory.badRequest("Bad request");
        }
    }
    private HttpResponse processLiveNodes(HttpRequest request) {
        try {
            var liveNodesRequest = MAPPER.readValue(request.body(), LiveNodesRequest.class);

            if (liveNodesRequest == null) {
                return responseFactory.badRequest("Bad request");
            }

            if (!liveNodesRequest.password().equals(gatewayOptions.getPassword())) {
                return responseFactory.unauthorized("Wrong password");
            }

            nodeManager.registerHeartbeat(liveNodesRequest.nodeId());

            var infos = nodeManager.getLiveNodesBySamePrefix(liveNodesRequest.nodeId());
            return responseFactory.ok(infos);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad request");
        }
    }

    private HttpResponse processNodeCommand(HttpRequest request) {
        try {
            var nodeCommandRequest = MAPPER.readValue(request.body(), NodeCommandRequest.class);

            if (nodeCommandRequest == null) {
                return responseFactory.badRequest("Bad request");
            }

            if (!nodeCommandRequest.password().equals(gatewayOptions.getPassword())) {
                return responseFactory.unauthorized("Wrong password");
            }

            var connection = nodeManager.getByNodeId(nodeCommandRequest.nodeId());

            if (connection == null) {
                return responseFactory.badRequest("Bad request");
            }

            try {
                return connection.sendConfigHttp(request);
            } catch (Exception e) {
                return responseFactory.badRequest(e.getMessage());
            }
        } catch (Exception e) {
            return responseFactory.badRequest("Bad request");
        }
    }
}
