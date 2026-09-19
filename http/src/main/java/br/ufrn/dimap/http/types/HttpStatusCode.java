package br.ufrn.dimap.http.types;

public enum HttpStatusCode {
    Ok(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    NoContent(204, "No Content"),
    MultipleChoices(300, "Multiple Choices"),
    MovedPermanently(301, "Moved Permanently"),
    Found(302, "Found"),
    NotModified(304, "Not Modified"),
    BadRequest(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"),
    Forbidden(403, "Forbidden"),
    NotFound(404, "Not Found"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented"),
    BadGateway(502, "Bad Gateway"),
    ServiceUnavailable(503, "Service Unavailable");

    private final int value;
    private final String reasonPhrase;

    HttpStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static HttpStatusCode fromValue(int value) {
        for (var status : values()) {
            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException(
            "Unknown HTTP status code: " + value
        );
    }
}