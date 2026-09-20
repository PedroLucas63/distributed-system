package br.ufrn.dimap.http.sockets;

import java.io.IOException;
import java.net.*;

public class HttpListener implements AutoCloseable {
    private final ServerSocket server;

    public HttpListener(InetSocketAddress endPoint) throws IOException {
        this.server = new ServerSocket();
        this.server.bind(endPoint);
    }

    public HttpListener(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public HttpListener(InetAddress address, int port) throws IOException {
        var endPoint = new InetSocketAddress(address, port);
        this(endPoint);
    }

    public HttpListener(ServerSocket socket) {
        this.server = socket;
    }

    public SocketAddress getLocalEndpoint() {
        return server.getLocalSocketAddress();
    }

    public HttpConnection acceptConnection() throws IOException {
        var client = server.accept();
        return new HttpConnection(client);
    }

    @Override
    public void close() throws Exception {
        server.close();
    }
}
