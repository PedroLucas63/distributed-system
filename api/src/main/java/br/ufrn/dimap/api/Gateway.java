package br.ufrn.dimap.api;

import br.ufrn.dimap.api.protocols.IProtocol;

import java.util.List;
import java.util.concurrent.Executors;

public class Gateway {
    private final List<IProtocol> protocols;

    public Gateway(List<IProtocol> protocols) {
        this.protocols = protocols;
    }

    public void start() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var protocol : protocols) {
                executor.submit(protocol::start);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
