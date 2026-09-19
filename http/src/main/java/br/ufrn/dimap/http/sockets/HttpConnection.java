package br.ufrn.dimap.http.sockets;

import br.ufrn.dimap.http.parser.HttpParser;
import br.ufrn.dimap.http.serializer.HttpMessageSerializer;
import br.ufrn.dimap.http.types.HttpMessage;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HttpConnection implements AutoCloseable  {
    private final Socket client;

    public HttpConnection() {
        this.client = new Socket();
    }

    public HttpConnection(Socket client) {
        this.client = client;
    }

    public HttpConnection(InetAddress address, int port) throws IOException {
        this.client = new Socket(address, port);
    }

    public HttpConnection(InetSocketAddress endPoint) throws IOException {
        this.client = new Socket();
        this.client.connect(endPoint);
    }

    public HttpConnection(String hostname, int port) throws IOException {
        this.client = new Socket(hostname, port);
    }

    public void connect(String hostname, int port) throws IOException {
        client.connect(new InetSocketAddress(hostname, port));
    }

    public void connect(InetAddress address, int port) throws IOException {
        client.connect(new InetSocketAddress(address, port));
    }

    public void connect(InetSocketAddress endpoint) throws IOException {
        client.connect(endpoint);
    }

    public HttpMessage read() throws IOException {
        return HttpParser.parse(client.getInputStream());
    }

    public HttpRequest readRequest() throws IOException {
        return HttpParser.parseRequest(client.getInputStream());
    }

    public HttpResponse readResponse() throws IOException {
        return HttpParser.parseResponse(client.getInputStream());
    }

    public void write(HttpMessage message) throws IOException {
        OutputStream stream = client.getOutputStream();
        byte[] buffer = HttpMessageSerializer.serialize(message);
        stream.write(buffer);
    }

    public InputStream getInputStream() throws IOException {
        return client.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return client.getOutputStream();
    }

    public void flush() throws IOException {
        client.getOutputStream().flush();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
