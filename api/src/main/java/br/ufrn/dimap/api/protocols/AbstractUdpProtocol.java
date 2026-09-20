package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.options.ServerOptions;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class AbstractUdpProtocol implements IProtocol {
    private final ServerOptions options;

    public AbstractUdpProtocol(ServerOptions options) {
        this.options = options;
    }

    @Override
    public void start() {
        try (var listener = new DatagramSocket(options.getUdpPort())) {
            while (!Thread.currentThread().isInterrupted()) {
                var buffer = new byte[65535];
                var packet = new DatagramPacket(buffer, buffer.length);

                listener.receive(packet);

                Thread.startVirtualThread(() -> processRequest(listener, packet));
            }
        } catch (Exception e) {
            System.out.println("[UDP] Error: " + e.getMessage());
        }
    }

    protected abstract void processRequest(DatagramSocket listener, DatagramPacket result);
}
