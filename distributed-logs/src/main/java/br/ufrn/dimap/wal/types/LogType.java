package br.ufrn.dimap.wal.types;

public enum LogType {
    Begin(0),
    Entry(1),
    Commit(2),
    Tag(3);

    private final int value;

    private LogType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LogType fromValue(int value) {
        for (LogType logType : LogType.values()) {
            if (logType.getValue() == value) {
                return logType;
            }
        }

        throw new IllegalArgumentException("Invalid value " + value);
    }
}
