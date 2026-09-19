module api {
    requires http;
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.protobuf;
    requires com.google.protobuf;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
}