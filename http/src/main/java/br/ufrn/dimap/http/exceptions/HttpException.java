package br.ufrn.dimap.http.exceptions;

public class HttpException extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }
}
