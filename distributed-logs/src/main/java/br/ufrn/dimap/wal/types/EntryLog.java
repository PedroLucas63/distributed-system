package br.ufrn.dimap.wal.types;

public record EntryLog(IWalEntry entry) implements ILog {
    @Override
    public LogType getType() {
        return LogType.Entry;
    }
}
