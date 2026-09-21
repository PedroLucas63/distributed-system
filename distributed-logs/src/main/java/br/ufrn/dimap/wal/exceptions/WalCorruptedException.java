package br.ufrn.dimap.wal.exceptions;

public class WalCorruptedException extends WalException {
    public WalCorruptedException(String message) {
        super(message);
    }
    public WalCorruptedException(String message, Exception cause) {
        super(message, cause);
    }
}
