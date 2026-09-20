module api {
    requires http;
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.protobuf;
    requires com.google.protobuf;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires java.xml;

    exports br.ufrn.dimap.api;
    exports br.ufrn.dimap.api.grpc;
    exports br.ufrn.dimap.api.handlers;
    exports br.ufrn.dimap.api.managers;
    exports br.ufrn.dimap.api.nodes;
    exports br.ufrn.dimap.api.options;
    exports br.ufrn.dimap.api.protocols;
    exports br.ufrn.dimap.api.routing;
    exports br.ufrn.dimap.api.types;
}