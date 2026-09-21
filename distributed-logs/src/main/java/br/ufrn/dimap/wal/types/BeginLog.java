package br.ufrn.dimap.wal.types;

import java.util.UUID;

public record BeginLog (CommandType command, UUID id) implements ILog {
    @Override
    public LogType getType() {
        return LogType.Begin;
    }
}