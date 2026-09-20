package br.ufrn.dimap.api.options;

public class GatewayOptions extends ServerOptions{
    protected GatewayOptions(Builder builder) {
        super(builder);
    }

    public static class Builder extends ServerOptions.Builder<Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GatewayOptions build() {
            if (password == null || password.isEmpty()) {
                throw new IllegalStateException("Password was not set.");
            }

            return new GatewayOptions(this);
        }
    }
}
