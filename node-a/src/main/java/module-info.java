module node.a {
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.protobuf;
    requires com.google.protobuf;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires java.xml; requires application; requires api;
    requires http;
    requires org.jspecify;
}