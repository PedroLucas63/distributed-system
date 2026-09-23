package br.ufrn.dimap.api.managers;

import br.ufrn.dimap.api.nodes.NodeConnection;
import br.ufrn.dimap.api.types.NodeInfo;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class NodeManager {
    private final long heartbeatTimeoutNanos;

    private long currentNode = 1;
    private final Map<String, Long> cyclicNodeIds = new HashMap<>();

    private final ReentrantLock nodesLock = new ReentrantLock();
    private final Map<Long, NodeConnection> knownNodes = new HashMap<>();
    private final Map<String, Map<Long, Long>> liveNodes = new HashMap<>();

    public NodeManager(Duration timeout) {
        this.heartbeatTimeoutNanos = timeout.toNanos();
    }

    public long registerNode(NodeInfo info) {
        nodesLock.lock();

        try {
            long nodeId;

            if (info.id() == null) {
                nodeId = currentNode++;
            } else {
                nodeId = info.id();

                if (nodeId > currentNode) {
                    currentNode = nodeId + 1;
                }
            }

            if (knownNodes.containsKey(nodeId)) {
                var currentNode = knownNodes.get(nodeId);
                var prefix =  currentNode.getNodeInfo().prefix();

                var dictionary = liveNodes.getOrDefault(prefix, new HashMap<>());

                if (dictionary.containsKey(nodeId)) {
                    var heartbeat = dictionary.get(nodeId);
                    var nodeTime = System.nanoTime() - heartbeat;

                    if (nodeTime <= heartbeatTimeoutNanos) {
                        throw new IllegalArgumentException("Node already exists");
                    }
                }

                currentNode.stop();
            }

            System.out.println("Registrando nó: " + nodeId);

            var connection = new NodeConnection(nodeId, info);
            knownNodes.put(nodeId, connection);

            addLiveNode(info.prefix(), nodeId);

            return nodeId;
        } finally {
            nodesLock.unlock();
        }
    }

    public NodeConnection getByNodeId(long nodeId)
    {
        nodesLock.lock();

        try {
            if (knownNodes.containsKey(nodeId)) {
                return knownNodes.get(nodeId);
            }

            return null;
        } finally {
            nodesLock.unlock();
        }
    }

    public NodeConnection getNextLiveNode(String prefix)
    {
        nodesLock.lock();

        try {
            var availableNodes = liveNodes.get(prefix);

            if (availableNodes == null)
                return null;

            while (!availableNodes.isEmpty()) {
                var nodeIds = new ArrayList<>(availableNodes.keySet());
                var index = (int) (cyclicNodeIds.get(prefix) % nodeIds.size());

                cyclicNodeIds.put(prefix, cyclicNodeIds.get(prefix) + 1);

                var heartbeat = availableNodes.get(nodeIds.get(index));
                var nodeTime = System.nanoTime() - heartbeat;

                if (nodeTime > heartbeatTimeoutNanos) {
                    availableNodes.remove(nodeIds.get(index));
                } else {
                    return knownNodes.get(nodeIds.get(index));
                }
            }

            return null;
        } finally {
            nodesLock.unlock();
        }
    }

    public List<NodeInfo> getLiveNodesBySamePrefix(long nodeId)
    {
        var connection = knownNodes.get(nodeId);
        if (connection == null)
            return List.of();

        var prefix = connection.getNodeInfo().prefix();

        var dictionary = liveNodes.get(prefix);
        if (dictionary == null)
            return List.of();

        return dictionary.keySet()
            .stream()
            .map(knownNodes::get)
            .filter(Objects::nonNull)
            .map(NodeConnection::getNodeInfo)
            .toList();
    }

    public void registerHeartbeat(long nodeId)
    {
        nodesLock.lock();
        try {
            var connection = knownNodes.get(nodeId);
            if (connection == null) return;

            var prefix = connection.getNodeInfo().prefix();
            addLiveNode(prefix, nodeId);
        } finally {
            nodesLock.unlock();
        }
    }

    public void registerConnectionRefused(long nodeId) {
        nodesLock.lock();

        System.out.println("Conexão recusada do nó: " + nodeId);

        try {
            var connection = knownNodes.get(nodeId);
            if (connection == null) return;

            var prefix = connection.getNodeInfo().prefix();
            var dictionary = liveNodes.get(prefix);
            if  (dictionary == null) return;

            dictionary.remove(nodeId);
        } finally {
            nodesLock.unlock();
        }
    }

    private void addLiveNode(String prefix, long nodeId)
    {
        var dictionary = liveNodes.computeIfAbsent(
          prefix,
          key -> {
              cyclicNodeIds.put(prefix, 0L);
              return new HashMap<>();
          }
        );

        dictionary.put(nodeId, System.nanoTime());
    }
}
