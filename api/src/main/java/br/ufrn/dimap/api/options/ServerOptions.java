package br.ufrn.dimap.api.options;

public class ServerOptions {
    protected final String password;
    protected final int udpPort;
    protected final int httpPort;
    protected final int grpcPort;

    protected ServerOptions(Builder builder) {
        this.password = builder.password;
        this.udpPort = builder.udpPort;
        this.httpPort = builder.httpPort;
        this.grpcPort = builder.grpcPort;
    }

    public String getPassword() {
        return password;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public static class Builder {
        protected String password;
        protected int udpPort;
        protected int httpPort;
        protected int grpcPort;

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder udpPort(int udpPort) {
            this.udpPort = udpPort;
            return this;
        }

        public Builder httpPort(int httpPort) {
            this.httpPort = httpPort;
            return this;
        }

        public Builder grpcPort(int grpcPort) {
            this.grpcPort = grpcPort;
            return this;
        }

        public ServerOptions build() {
            if (password == null || password.isEmpty()) {
                throw new IllegalStateException("Password was not set.");
            }

            return new ServerOptions(this);
        }
    }
}
