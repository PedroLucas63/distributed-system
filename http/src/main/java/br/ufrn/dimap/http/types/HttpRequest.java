package br.ufrn.dimap.http.types;

import java.util.HashMap;
import java.util.Map;

public record HttpRequest(
    HttpMethod method,
    String path,
    HttpVersion version,
    Map<String, String> queryParameters,
    Map<String, String> headers,
    byte[] body
) implements HttpMessage {
    public static class Builder {
        private HttpMethod method;
        private String path;
        private HttpVersion version;
        private Map<String, String> queryParameters = new HashMap<>();
        private Map<String, String> headers = new HashMap<>();
        private byte[] body;

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }

        public Builder queryParameter(String name, String value) {
            queryParameters.put(name, value);
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

        public HttpRequest build() {
            if (method == null) {
                throw new IllegalStateException("Method was not set.");
            }

            if (path == null || path.isBlank()) {
                throw new IllegalStateException("Path was not set.");
            }

            if (version == null) {
                throw new IllegalStateException("Version was not set.");
            }

            return new HttpRequest(
                    method,
                    path,
                    version,
                    queryParameters,
                    headers,
                    body
            );
        }
    }
}
