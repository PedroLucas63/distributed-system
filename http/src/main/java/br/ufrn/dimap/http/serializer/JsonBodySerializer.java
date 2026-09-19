package br.ufrn.dimap.http.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBodySerializer implements IHttpBodySerializer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(Object body) {
        try {
            return MAPPER.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize body.", e);
        }
    }

    @Override
    public String contentType() {
        return "application/json; charset=utf-8";
    }
}
