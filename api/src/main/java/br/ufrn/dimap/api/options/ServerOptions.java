package br.ufrn.dimap.api.options;

public abstract class ServerOptions {
    protected final String password;
    protected final int udpPort;
    protected final int httpPort;
    protected final int grpcPort;

    protected ServerOptions(Builder<?> builder) {
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

    public abstract static class Builder<T extends Builder<T>> {
        protected String password;
        protected int udpPort;
        protected int httpPort;
        protected int grpcPort;

        protected abstract T self();

        public T password(String password) {
            this.password = password;
            return self();
        }

        public T udpPort(int udpPort) {
            this.udpPort = udpPort;
            return self();
        }

        public T httpPort(int httpPort) {
            this.httpPort = httpPort;
            return self();
        }

        public T grpcPort(int grpcPort) {
            this.grpcPort = grpcPort;
            return self();
        }

        public abstract ServerOptions build();
    }
}
