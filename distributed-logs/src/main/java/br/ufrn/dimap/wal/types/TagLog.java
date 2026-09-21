package br.ufrn.dimap.wal.types;

public record TagLog(String tag) implements ILog {
    @Override
    public LogType getType() {
        return LogType.Tag;
    }
}
