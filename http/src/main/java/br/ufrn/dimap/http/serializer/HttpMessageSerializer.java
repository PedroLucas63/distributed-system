package br.ufrn.dimap.http.serializer;

import br.ufrn.dimap.http.types.HttpMessage;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpMessageSerializer {
    public static byte[] serialize(HttpMessage message) throws IOException {
        if (message instanceof HttpRequest request) {
            return serialize(request);
        } else if (message instanceof HttpResponse response) {
            return serialize(response);
        }

        throw new IllegalArgumentException("Invalid message type");
    }

    public static byte[] serialize(HttpResponse response) {
        var builder = new StringBuilder();

        builder.append(response.version().getValue())
            .append(' ')
            .append(response.statusCode().getValue())
            .append(' ')
            .append(response.reasonPhrase())
            .append("\r\n");

        return getBytes(builder, response.headers(), response.body(), response);
    }

    public static byte[] serialize(HttpRequest request) {
        var builder = new StringBuilder();

        builder.append(request.method().getValue())
            .append(' ')
            .append(request.path())
            .append(' ')
            .append(request.version().getValue())
            .append("\r\n");

        return getBytes(builder, request.headers(), request.body(), request);
    }

    private static byte[] getBytes(StringBuilder builder, Map<String, String> headers, byte[] body, HttpMessage request) {
        headers.forEach((header, value) ->
            builder.append(header)
                .append(": ")
                .append(value)
                .append("\r\n")
        );

        builder.append("\r\n");

        var headerBytes = builder.toString().getBytes(StandardCharsets.UTF_8);

        var result = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(body, 0, result, headerBytes.length, body.length);

        return result;
    }
}
