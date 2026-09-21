package br.ufrn.dimap.wal.types;

public enum CommandType {
    Transaction(0);

    private final int value;

    private CommandType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CommandType fromValue(int value) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getValue() == value) {
                return commandType;
            }
        }

        throw new IllegalArgumentException("Invalid value " + value);
    }
}
