package br.ufrn.dimap.api.options;

import java.net.InetAddress;

public class NodeOptions {
    private long nodeId;
    private final String pathPrefix;
    private final String password;


    private final InetAddress gatewayAddress;
    private final int gatewayUdpPort;
    private final int gatewayHttpPort;
    private final int gatewayGrpcPort;

    private final int udpPort;
    private final int httpPort;
    private final int grpcPort;

    private NodeOptions(Builder builder) {
        this.nodeId = builder.nodeId;
        this.pathPrefix = builder.pathPrefix;
        this.password = builder.password;
        this.gatewayAddress = builder.gatewayAddress;
        this.gatewayUdpPort = builder.gatewayUdpPort;
        this.gatewayHttpPort = builder.gatewayHttpPort;
        this.gatewayGrpcPort = builder.gatewayGrpcPort;
        this.udpPort = builder.udpPort;
        this.httpPort = builder.httpPort;
        this.grpcPort = builder.grpcPort;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public String getPassword() {
        return password;
    }

    public InetAddress getGatewayAddress() {
        return gatewayAddress;
    }

    public int getGatewayUdpPort() {
        return gatewayUdpPort;
    }

    public int getGatewayHttpPort() {
        return gatewayHttpPort;
    }

    public int getGatewayGrpcPort() {
        return gatewayGrpcPort;
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
        private long nodeId;
        private String pathPrefix;
        private String password;
        private InetAddress gatewayAddress;
        private int gatewayUdpPort;
        private int gatewayHttpPort;
        private int gatewayGrpcPort;
        private int udpPort;
        private int httpPort;
        private int grpcPort;

        public Builder nodeId(long nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder pathPrefix(String pathPrefix) {
            this.pathPrefix = pathPrefix;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder gatewayAddress(InetAddress gatewayAddress) {
            this.gatewayAddress = gatewayAddress;
            return this;
        }

        public Builder gatewayUdpPort(int gatewayUdpPort) {
            this.gatewayUdpPort = gatewayUdpPort;
            return this;
        }

        public Builder gatewayHttpPort(int gatewayHttpPort) {
            this.gatewayHttpPort = gatewayHttpPort;
            return this;
        }

        public Builder gatewayGrpcPort(int gatewayGrpcPort) {
            this.gatewayGrpcPort = gatewayGrpcPort;
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

        public NodeOptions build() {
            if (pathPrefix == null || pathPrefix.isEmpty()) {
                throw new IllegalStateException("Path prefix was not set.");
            }

            if (password == null || password.isEmpty()) {
                throw new IllegalStateException("Password was not set.");
            }

            if (gatewayAddress == null) {
                throw new IllegalStateException("Gateway address was not set.");
            }

            return new NodeOptions(this);
        }
    }
}
