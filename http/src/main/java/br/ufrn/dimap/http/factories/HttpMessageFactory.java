package br.ufrn.dimap.http.factories;

import br.ufrn.dimap.http.options.HttpMessageOptions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMessageFactory {
    protected final HttpMessageOptions options;

    public HttpMessageFactory(HttpMessageOptions options) {
        this.options = options;
    }

    protected Map<String, String> getHeaders(
            String contentType,
            int contentLength
    ) {
        var headers = new HashMap<>(options.getDefaultHeaders());

        if (options.isIncludeDateHeader()) {
            var date = ZonedDateTime.now(java.time.ZoneOffset.UTC).format(
                    DateTimeFormatter.RFC_1123_DATE_TIME
            );
            headers.put("Date", date);
        }

        if (options.isIncludeServerHeader() && !options.getServer().isBlank()) {
            headers.put("Server", options.getServer());
        }

        if (contentLength <= 0) {
            return headers;
        }

        headers.put("Content-Type", contentType);
        headers.put("Content-Length", Integer.toString(contentLength));

        return headers;
    }
}
