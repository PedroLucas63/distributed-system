package br.ufrn.dimap.http.types;

import java.util.Map;

public interface HttpMessage
{
    Map<String, String> headers();
    byte[] body();
}
