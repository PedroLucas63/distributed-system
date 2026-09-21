package br.ufrn.dimap.wal.exceptions;

public class WalUnknownTypeException extends WalException {
    public WalUnknownTypeException(int typeId) {
        super("The WAL contains an unknown entry type with ID " + typeId + ".");
    }
}
