package br.ufrn.dimap.http.factories;

import br.ufrn.dimap.http.options.HttpMessageOptions;
import br.ufrn.dimap.http.serializer.IHttpBodySerializer;
import br.ufrn.dimap.http.serializer.JsonBodySerializer;
import br.ufrn.dimap.http.serializer.TextBodySerializer;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.http.types.HttpStatusCode;


public final class HttpResponseFactory extends HttpMessageFactory {
    private static final TextBodySerializer textSerializer = new TextBodySerializer();
    private static final JsonBodySerializer jsonSerializer = new JsonBodySerializer();

    public HttpResponseFactory(HttpMessageOptions options) {
        super(options);
    }

    public HttpResponse ok(Object content) {
        return create(HttpStatusCode.Ok, content, "");
    }
    public HttpResponse ok() {
        return ok(null);
    }

    public HttpResponse created(Object content, String location) {
        return create(HttpStatusCode.Created, content, location);
    }
    public HttpResponse created(Object content) {
        return created(content, "");
    }
    public HttpResponse created() {
        return created(null);
    }

    public HttpResponse accepted(Object content, String location) {
        return create(HttpStatusCode.Accepted, content, location);
    }
    public HttpResponse accepted(Object content) {
        return accepted(content, "");
    }
    public HttpResponse accepted() {
        return accepted(null);
    }

    public HttpResponse noContent() {
        return create(HttpStatusCode.NoContent, null, "");
    }

    public HttpResponse multipleChoices(Object content, String location) {
        return create(HttpStatusCode.MultipleChoices, content, location);
    }
    public HttpResponse multipleChoices(Object content) {
        return multipleChoices(content, "");
    }
    public HttpResponse multipleChoices() {
        return multipleChoices(null);
    }

    public HttpResponse movedPermanently(Object content, String location) {
        return create(HttpStatusCode.MovedPermanently, content, location);
    }
    public HttpResponse movedPermanently(Object content) {
        return movedPermanently(content, "");
    }

    public HttpResponse found(Object content, String location) {
        return create(HttpStatusCode.Found, content, location);
    }
    public HttpResponse found(Object content) {
        return found(content, "");
    }

    public HttpResponse notModified(Object content) {
        return create(HttpStatusCode.NotModified, content, "");
    }
    public HttpResponse notModified() {
        return notModified(null);
    }

    public HttpResponse badRequest(Object content) {
        return create(HttpStatusCode.BadGateway, content, "");
    }

    public HttpResponse unauthorized(Object content) {
        return create(HttpStatusCode.Unauthorized, content, "");
    }
    public HttpResponse unauthorized() {
        return unauthorized(null);
    }

    public HttpResponse forbidden(Object content) {
        return create(HttpStatusCode.Forbidden, content, "");
    }

    public HttpResponse notFound(Object content) {
        return create(HttpStatusCode.NotFound, content, "");
    }

    public HttpResponse internalServerError(Object content) {
        return create(HttpStatusCode.InternalServerError, content, "");
    }

    public HttpResponse notImplemented(Object content) {
        return create(HttpStatusCode.NotImplemented, content, "");
    }
    public HttpResponse notImplemented() {
        return notImplemented(null);
    }

    public HttpResponse badGateway(Object content) {
        return create(HttpStatusCode.BadGateway, content, "");
    }

    public HttpResponse serviceUnavailable(Object content) {
        return create(HttpStatusCode.ServiceUnavailable, content, "");
    }

    public HttpResponse create(
        HttpStatusCode statusCode,
        Object content,
        String location
    ) {
        if (content instanceof String) {
            return create(statusCode, content, location, textSerializer);
        }

        return create(statusCode, content, location, jsonSerializer);
    }

    private HttpResponse create(
        HttpStatusCode statusCode,
        Object content,
        String location,
        IHttpBodySerializer serializer
    ) {
        var body = content == null ? new byte[0] : serializer.serialize(content);

        var headers = getHeaders(
            serializer.contentType(),
            body.length
        );

        if (location != null && !location.isBlank())
            headers.put("Location", location);

        return new HttpResponse.Builder()
            .version(options.getVersion())
            .statusCode(statusCode)
            .reasonPhrase(statusCode.getReasonPhrase())
            .version(options.getVersion())
            .headers(headers)
            .body(body)
            .build();
    }
}