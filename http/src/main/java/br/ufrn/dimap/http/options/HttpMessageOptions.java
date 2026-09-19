package br.ufrn.dimap.http.options;

import br.ufrn.dimap.http.types.HttpVersion;

import java.util.HashMap;
import java.util.Map;

public final class HttpMessageOptions
{
    private final HttpVersion version;
    private final boolean includeDateHeader;
    private final boolean includeServerHeader;
    private final String server;
    private final Map<String, String> defaultHeaders;

    private HttpMessageOptions(Builder builder) {
        this.version = builder.version;
        this.includeDateHeader = builder.includeDateHeader;
        this.includeServerHeader = builder.includeServerHeader;
        this.server = builder.server;
        this.defaultHeaders = Map.copyOf(builder.defaultHeaders);
    }

    public HttpVersion getVersion() {
        return version;
    }

    public boolean isIncludeDateHeader() {
        return includeDateHeader;
    }

    public boolean isIncludeServerHeader() {
        return includeServerHeader;
    }

    public String getServer() {
        return server;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public static class Builder {
        private HttpVersion version = HttpVersion.HTTP1_0;
        private boolean includeDateHeader = false;
        private boolean includeServerHeader = false;
        private String server = "";
        private Map<String, String> defaultHeaders = new HashMap<>();

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder includeDateHeader(boolean includeDateHeader) {
            this.includeDateHeader = includeDateHeader;
            return this;
        }

        public Builder includeServerHeader(boolean includeServerHeader) {
            this.includeServerHeader = includeServerHeader;
            return this;
        }

        public Builder server(String server) {
            this.server = server;
            return this;
        }

        public Builder defaultHeaders(Map<String, String> defaultHeaders) {
            this.defaultHeaders = defaultHeaders;
            return this;
        }

        public Builder defaultHeader(String name, String value) {
            this.defaultHeaders.put(name, value);
            return this;
        }

        public HttpMessageOptions build() {
            return new HttpMessageOptions(this);
        }
    }
}