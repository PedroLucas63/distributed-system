package br.ufrn.dimap.wal.options;

public record WalOptions(String logFilePath) {
    private WalOptions(Builder builder) {
        this(builder.logFilePath);
    }

    public static class Builder {
        private String logFilePath;

        public Builder logFilePath(String logFilePath) {
            this.logFilePath = logFilePath;
            return this;
        }

        public WalOptions build() {
            if (logFilePath == null || logFilePath.isEmpty()) {
                throw new IllegalArgumentException("The log file path is required.");
            }

            return new WalOptions(this);
        }
    }
}
