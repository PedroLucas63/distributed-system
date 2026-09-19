package br.ufrn.dimap.http.serializer;

import java.nio.charset.StandardCharsets;

public class TextBodySerializer implements IHttpBodySerializer {
    @Override
    public byte[] serialize(Object body) {
        return ((String) body).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String contentType() {
        return "text/plain; charset=utf-8";
    }
}

