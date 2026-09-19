package br.ufrn.dimap.api.types;

import java.util.List;

public record LiveNodesResponse(
    List<NodeInfo> nodes
) {
}
