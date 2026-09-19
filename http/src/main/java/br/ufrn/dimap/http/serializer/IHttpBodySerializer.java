package br.ufrn.dimap.http.serializer;

public interface IHttpBodySerializer {
    byte[] serialize(Object body);
    String contentType();
}
