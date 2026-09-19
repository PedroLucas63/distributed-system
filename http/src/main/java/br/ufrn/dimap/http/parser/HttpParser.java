package br.ufrn.dimap.http.parser;

import br.ufrn.dimap.http.types.*;
import br.ufrn.dimap.http.exceptions.HttpException;
import br.ufrn.dimap.http.types.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {
    private enum MessageType {
        Response, Request, Invalid
    }

    public static HttpMessage parse(InputStream stream) throws IOException {
        var startLine = readLine(stream);
        if (startLine == null)
            throw new HttpException("Invalid message");

        var messageType = identifyMessageType(startLine);

        if (messageType == MessageType.Response) {
            return parseResponse(stream, startLine);
        } else if  (messageType == MessageType.Request) {
            return parseRequest(stream, startLine);
        } else {
            throw new HttpException("HTTP message type not identified.");
        }
    }

    private static MessageType identifyMessageType(String startLine)
    {
        if  (startLine == null || startLine.isEmpty())
            return MessageType.Invalid;

        var tokens = startLine.trim().split("\\s+");
        try
        {
            parseVersion(tokens[0]);
            return MessageType.Response;
        } catch (Exception ex) {
            return MessageType.Request;
        }
    }

    public static HttpResponse parseResponse(InputStream stream) throws IOException {
        var startLine = readLine(stream);
        if (startLine == null)
            throw new HttpException("Invalid message");
        return parseResponse(stream, startLine);
    }

    private static HttpResponse parseResponse(InputStream stream, String startLine) throws IOException {
        var builder = new HttpResponse.Builder();
        setResponseStartLine(startLine, builder);

        var headers = readHeaders(stream);
        builder.headers(headers);

        var contentLengthStr = headers.getOrDefault("Content-Length", "0");
        var body = readBody(stream, contentLengthStr);
        builder.body(body);

        return builder.build();
    }

    private static void setResponseStartLine(String startLine, HttpResponse.Builder builder) {
        var tokens = startLine.trim().split("\\s+", 3);

        if (tokens.length != 3)
            throw new HttpException("Invalid response: start line does not have 3 parts");

        var version = parseVersion(tokens[0]);
        var statusCode = parseStatusCode(tokens[1]);
        var reasonPhrase = parseReasonPhrase(tokens[2]);

        builder.version(version)
            .statusCode(statusCode)
            .reasonPhrase(reasonPhrase);
    }

    private static HttpStatusCode parseStatusCode(String statusCode)
    {
        try {
            var code = Integer.parseInt(statusCode);
            return HttpStatusCode.fromValue(code);
        } catch (NumberFormatException ex) {
            throw  new HttpException("Invalid status code: " + statusCode);
        }
    }

    private static String parseReasonPhrase(String reasonPhrase)
    {
        if (reasonPhrase == null ||
            reasonPhrase.isEmpty() ||
            reasonPhrase.contains("\r") ||
            reasonPhrase.contains("\n")
        )
            throw new HttpException("Invalid reason phrase: " + reasonPhrase);

        return reasonPhrase.trim();
    }

    public static HttpRequest parseRequest(InputStream stream) throws IOException {
        var startLine = readLine(stream);
        if (startLine == null)
            throw new HttpException("Invalid message");
        return parseRequest(stream, startLine);
    }

    private static HttpRequest parseRequest(InputStream stream, String startLine) throws IOException {
        var builder = new HttpRequest.Builder();
        setRequestStartLine(startLine, builder);

        var headers = readHeaders(stream);
        builder.headers(headers);

        var contentLengthStr = headers.getOrDefault("Content-Length", "0");
        var body = readBody(stream, contentLengthStr);
        builder.body(body);

        return builder.build();
    }

    private static void setRequestStartLine(String startLine, HttpRequest.Builder builder)
    {
        var tokens = startLine.trim().split("\\s+", 3);;

        if (tokens.length != 3)
            throw new HttpException("Invalid request: start line does not have 3 parts");

        var method = HttpMethod.fromValue(tokens[0]);
        var path = parsePath(tokens[1]);
        var queryParameters = parseQueryParameters(tokens[1]);
        var version = parseVersion(tokens[2]);

        builder.method(method)
            .path(path)
            .queryParameters(queryParameters)
            .version(version);
    }

    private static String parsePath(String path)
    {
        if (!path.startsWith("/"))
            throw new HttpException("Invalid path: " + path);

        var pathParts = path.split("\\?");
        if  (pathParts.length > 2) {
            throw new HttpException("Invalid path: " + path);
        }

        return pathParts[0];
    }

    private static Map<String, String> parseQueryParameters(String path)
    {
        Map<String, String>  queryParameters = new HashMap<>();
        var pathParts = path.split("\\?");

        if (pathParts.length != 2) return queryParameters;

        var query =  pathParts[1];
        var fields = query.split("&");

        for (var field : fields)
        {
            var fieldParts = field.split("=");

            if (fieldParts.length == 1)
            {
                var key = fieldParts[0];
                if (key == null || key.isEmpty())
                    throw new HttpException("Invalid query parameter: key is missing");

                queryParameters.put(key, "true");
            }
            else if (fieldParts.length != 2)
            {
                throw new HttpException("Invalid query parameter: invalid format");
            }
            else
            {
                var key = fieldParts[0];
                var value = fieldParts[1];

                if (key == null || key.isEmpty() || value == null || value.isEmpty())
                    throw new HttpException("Invalid query parameter: key or value is missing");

                if (queryParameters.containsKey(key)) {
                    var newValue = queryParameters.get(key) + "," + value;
                    queryParameters.put(key, newValue);
                } else {
                    queryParameters.put(key, value);
                }
            }
        }

        return queryParameters;
    }

    private static HttpVersion parseVersion(String version)
    {
        version = version.toUpperCase();
        return HttpVersion.fromValue(version);
    }

    private static Map<String, String> readHeaders(InputStream stream) throws IOException {
        Map<String, String> headers = new HashMap<>();

        var line = readLine(stream);
        while (line != null && !line.isEmpty())
        {
            var headerParts = line.trim().split(":", 2);
            if (headerParts.length != 2)
                throw new HttpException("Invalid header: header does not have two parts");

            var key = parseHeaderKey(headerParts[0]);
            var value = parseHeaderValue(headerParts[1]);

            if (headers.containsKey(key))
            {
                var newValue = headers.get(key) + "," + value;
                headers.put(key, newValue);
            } else {
                headers.put(key, value);
            }

            line = readLine(stream);
        }

        return headers;
    }

    private static String parseHeaderKey(String key)
    {
        if (key == null || key.isEmpty())
            throw new HttpException("Invalid header key");
        return key.trim();
    }

    private static String parseHeaderValue(String value)
    {
        if (value == null || value.isEmpty())
            throw new HttpException("Invalid header value");
        return value.trim();
    }

    private static byte[] readBody(InputStream stream, String value) throws IOException {
        var contentLength = parseContentLength(value);

        if (contentLength < 0) {
            throw new HttpException("Invalid content length: content length is less than 0");
        }

        if (contentLength == 0) {
            return new byte[0];
        }

        var body = new byte[contentLength];
        var totalRead = 0;

        while (totalRead < contentLength)
        {
            var read = stream.read(
                body,
                totalRead,
                contentLength - totalRead
            );

            if (read == 0)
                throw new HttpException("Unexpected end of body");

            totalRead += read;
        }

        return body;
    }

    private static int parseContentLength(String value)
    {
        try {
            var length = Integer.parseInt(value);
            if (length >= 0)
                return length;
        } catch (NumberFormatException _) {
        }

        throw new HttpException("Invalid content length: content length is less than 0 or not is an integer");
    }

    private static String readLine(InputStream stream) throws IOException {
        var bytes = new ByteArrayOutputStream();

        while (true)
        {
            var value = stream.read();

            switch (value)
            {
                case -1:
                    if (bytes.size() == 0) {
                        return null;
                    }
                    throw new HttpException("Unexpected end of request");

                case '\r':
                {
                    var next = stream.read();
                    if (next != '\n') {
                        throw new HttpException("Invalid line ending");
                    }

                    return bytes.toString(StandardCharsets.US_ASCII);
                }
                default:
                    bytes.write(value);
                    break;
            }
        }
    }
}
