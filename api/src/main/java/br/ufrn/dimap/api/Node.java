package br.ufrn.dimap.api;

import br.ufrn.dimap.api.nodes.NodeHeartbeat;
import br.ufrn.dimap.api.nodes.NodeRegister;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.api.protocols.IProtocol;

import java.util.List;
import java.util.concurrent.Executors;

public class Node {
    private final NodeOptions options;
    private final NodeRegister register;
    private final NodeHeartbeat heartbeat;
    private final List<IProtocol> protocols;

    public Node(
        NodeOptions options,
        NodeRegister register,
        NodeHeartbeat heartbeat,
        List<IProtocol> protocols
    ) {
        this.options = options;
        this.register = register;
        this.heartbeat = heartbeat;
        this.protocols = protocols;
    }

    public void start() {
        try {
            var id = register.register();
            options.setNodeId(id);

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                executor.submit(heartbeat::start);

                for (var protocol : protocols) {
                    executor.submit(protocol::start);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
