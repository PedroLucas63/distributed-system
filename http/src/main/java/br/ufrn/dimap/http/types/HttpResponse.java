package br.ufrn.dimap.http.types;

import java.util.HashMap;
import java.util.Map;

public record HttpResponse(
    HttpVersion version,
    HttpStatusCode statusCode,
    String reasonPhrase,
    Map<String, String> headers,
    byte[] body
) implements HttpMessage {
    public static class Builder {
        private HttpVersion version;
        private HttpStatusCode statusCode;
        private String reasonPhrase;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body;

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder statusCode(HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder reasonPhrase(String reasonPhrase) {
            this.reasonPhrase = reasonPhrase;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            if (version == null) {
                throw new IllegalStateException("Version was not set.");
            }

            if (statusCode == null) {
                throw new IllegalStateException("Status Code was not set.");
            }

            if (reasonPhrase == null || reasonPhrase.isBlank()) {
                reasonPhrase = statusCode.getReasonPhrase();
            }

            return new HttpResponse(
                version,
                statusCode,
                reasonPhrase,
                headers,
                body
            );
        }
    }
}
