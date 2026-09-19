package br.ufrn.dimap.http.factories;

import br.ufrn.dimap.http.options.HttpMessageOptions;
import br.ufrn.dimap.http.serializer.IHttpBodySerializer;
import br.ufrn.dimap.http.serializer.JsonBodySerializer;
import br.ufrn.dimap.http.serializer.TextBodySerializer;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;

public final class HttpRequestFactory extends HttpMessageFactory {
    private static final TextBodySerializer textSerializer = new TextBodySerializer();
    private static final JsonBodySerializer jsonSerializer = new JsonBodySerializer();

    public HttpRequestFactory(HttpMessageOptions options) {
        super(options);
    }

    public HttpRequest get(String path) {
        return create(path, HttpMethod.Get, null);
    }

    public HttpRequest post(String path, Object content) {
        return create(path, HttpMethod.Post, content);
    }

    public HttpRequest head(String path) {
        return create(path, HttpMethod.Head, null);
    }

    public HttpRequest create(
        String path,
        HttpMethod method,
        Object content
    ) {
        if (content instanceof String) {
            return create(path, method, content, textSerializer);
        }

        return create(path, method, content, jsonSerializer);
    }

    private HttpRequest create(
        String path,
        HttpMethod method,
        Object content,
        IHttpBodySerializer serializer
    ) {
        var body = content == null ? new byte[0] : serializer.serialize(content);

        var headers = getHeaders(
            serializer.contentType(),
            body.length
        );

        return new HttpRequest.Builder()
            .method(method)
            .path(path)
            .version(options.getVersion())
            .headers(headers)
            .body(body)
            .build();
    }
}