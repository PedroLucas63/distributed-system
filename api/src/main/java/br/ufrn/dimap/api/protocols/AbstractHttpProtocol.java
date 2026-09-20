package br.ufrn.dimap.api.protocols;

import br.ufrn.dimap.api.options.ServerOptions;
import br.ufrn.dimap.http.sockets.HttpConnection;
import br.ufrn.dimap.http.sockets.HttpListener;

import java.net.InetAddress;

public abstract class AbstractHttpProtocol implements IProtocol{

    protected final ServerOptions options;

    public AbstractHttpProtocol(ServerOptions options) {
        this.options = options;
    }

    @Override
    public void start() {
        try(var listener = new HttpListener(
            InetAddress.getByName("0.0.0.0"),
            options.getHttpPort()
        )) {
            while (!Thread.currentThread().isInterrupted()) {
                var connection = listener.acceptConnection();
                Thread.startVirtualThread(() -> processRequest(connection));
            }
        } catch (Exception e) {
            System.out.println("[HTTP] Error: " + e.getMessage());
        }
    }

    protected abstract void processRequest(HttpConnection connection);
}
