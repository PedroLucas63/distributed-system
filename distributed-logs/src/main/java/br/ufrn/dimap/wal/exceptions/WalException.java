package br.ufrn.dimap.wal.exceptions;

public class WalException extends RuntimeException {
    public WalException(String message) {
        super(message);
    }

    public WalException(String message, Exception cause) {
        super(message, cause);
    }
}
