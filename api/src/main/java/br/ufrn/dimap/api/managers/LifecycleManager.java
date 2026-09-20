package br.ufrn.dimap.api.managers;

import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.api.types.NodeCommandRequest;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.sockets.HttpListener;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Consumer;

public class LifecycleManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final NodeOptions options;
    private final HttpResponseFactory responseFactory;
    private final Consumer<NodeOptions> nodeStarter;

    private volatile Thread nodeThread;

    public LifecycleManager(
        NodeOptions options,
        HttpResponseFactory responseFactory,
        Consumer<NodeOptions> nodeStarter
    ) {
        this.options = options;
        this.nodeStarter = nodeStarter;
        this.responseFactory = responseFactory;
    }

    public void start() {
        startNode();

        try (var listener = new HttpListener(
            InetAddress.getByName("0.0.0.0"),
            options.getConfigPort()
        )) {
            System.out.println(
                "[LIFECYCLE] Configuration server listening on port " + options.getConfigPort()
            );

            while (!Thread.currentThread().isInterrupted()) {
                var connection = listener.acceptConnection();
                processRequest(connection);
            }
        } catch (Exception e) {
            System.out.println(
                "[LIFECYCLE] Error: " + e.getMessage()
            );
        }
    }

    private void processRequest(HttpConnection connection) {
        try (connection) {
            var request = connection.readRequest();
            var response = handle(request);
            connection.write(response);
        } catch (IOException e) {
            System.out.println("[LIFECYCLE] Connection error: " + e.getMessage());
        }
    }

    private HttpResponse handle(HttpRequest request) {
        if (request.method() != HttpMethod.Post) {
            return responseFactory.badRequest("Bad request");
        }

        if (!"/command".equals(request.path())) {
            return responseFactory.badRequest("Bad request");
        }

        return processCommand(request);
    }

    private HttpResponse processCommand(HttpRequest request) {
        try {
            var command = MAPPER.readValue(request.body(), NodeCommandRequest.class);

            if (command == null) {
                return responseFactory.badRequest("Bad request");
            }

            if (!options.getPassword().equals(command.password())) {
                return responseFactory.unauthorized("Wrong password");
            }

            if (!options.getPrefix().equals(command.nodePrefix())) {
                return responseFactory.badRequest("Wrong node prefix");
            }

            switch (command.command().toUpperCase()) {
                case "START" -> startNode();
                case "STOP" -> stopNode();
                default -> {
                    return responseFactory.badRequest(
                        "Unknown command"
                    );
                }
            }

            return responseFactory.accepted(command);

        } catch (Exception e) {
            return responseFactory.badRequest("Bad request");
        }
    }

    private synchronized void startNode() {
        if (isNodeRunning()) {
            return;
        }

        nodeThread = Thread.startVirtualThread(() -> {
            try {
                nodeStarter.accept(options);
            } catch (Exception e) {
                System.out.println(
                    "[LIFECYCLE] Node stopped: " + e.getMessage()
                );
            }
        });
    }

    private synchronized void stopNode() {
        if (!isNodeRunning()) {
            return;
        }

        System.out.println("[LIFECYCLE] Stopping node " + options.getPrefix() + "...");

        nodeThread.interrupt();
        nodeThread = null;

        System.out.println("[LIFECYCLE] Node " + options.getPrefix() + " stopped.");
    }

    private boolean isNodeRunning() {
        return nodeThread != null && nodeThread.isAlive();
    }
}
