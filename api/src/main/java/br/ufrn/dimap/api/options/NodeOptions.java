package br.ufrn.dimap.api.options;

import java.net.InetAddress;

public class NodeOptions extends ServerOptions {
    private Long nodeId;
    private final String prefix;

    private final InetAddress gatewayAddress;
    private final int gatewayUdpPort;
    private final int gatewayHttpPort;
    private final int gatewayGrpcPort;
    private final Integer configPort;

    private NodeOptions(Builder builder) {
        super(builder);
        this.nodeId = builder.nodeId;
        this.prefix = builder.prefix;
        this.gatewayAddress = builder.gatewayAddress;
        this.gatewayUdpPort = builder.gatewayUdpPort;
        this.gatewayHttpPort = builder.gatewayHttpPort;
        this.gatewayGrpcPort = builder.gatewayGrpcPort;
        this.configPort = builder.configPort;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getPrefix() {
        return prefix;
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

    public Integer getConfigPort() {
        return configPort;
    }

    public static class Builder extends ServerOptions.Builder<Builder> {
        private Long nodeId;
        private String prefix;
        private InetAddress gatewayAddress;
        private int gatewayUdpPort;
        private int gatewayHttpPort;
        private int gatewayGrpcPort;
        private Integer configPort = null;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder nodeId(Long nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
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

        public Builder configPort(Integer configPort) {
            this.configPort = configPort;
            return this;
        }

        @Override
        public NodeOptions build() {
            if (prefix == null || prefix.isEmpty()) {
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
