module http {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports br.ufrn.dimap.http.exceptions;
    exports br.ufrn.dimap.http.factories;
    exports br.ufrn.dimap.http.options;
    exports br.ufrn.dimap.http.parser;
    exports br.ufrn.dimap.http.serializer;
    exports br.ufrn.dimap.http.sockets;
    exports br.ufrn.dimap.http.types;
}