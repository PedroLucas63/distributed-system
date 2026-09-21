package br.ufrn.dimap.wal.exceptions;

public class WalAlreadyLoadedException extends WalException {
    public WalAlreadyLoadedException() {
        super("The WAL has already been loaded.");
    }
}
