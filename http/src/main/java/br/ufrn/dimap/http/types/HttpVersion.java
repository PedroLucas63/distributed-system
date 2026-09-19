package br.ufrn.dimap.http.types;

public enum HttpVersion {
    HTTP0_9("HTTP/0.9"),
    HTTP1_0("HTTP/1.0"),
    HTTP1_1("HTTP/1.1"),
    HTTP2("HTTP/2"),
    HTTP3("HTTP/3");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HttpVersion fromValue(String value) {
        for (var version : values()) {
            if (version.value.equals(value)) {
                return version;
            }
        }

        throw new IllegalArgumentException(
            "Unknown HTTP version: " + value
        );
    }
}
