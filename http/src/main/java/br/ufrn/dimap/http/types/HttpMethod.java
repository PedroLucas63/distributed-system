package br.ufrn.dimap.http.types;

public enum HttpMethod {
    Get("GET"),
    Post("POST"),
    Head("HEAD");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HttpMethod fromValue(String value) {
        for (var method : values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }

        throw new IllegalArgumentException(
            "Unknown HTTP method: " + value
        );
    }
}
