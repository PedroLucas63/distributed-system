package br.ufrn.dimap.api.handlers;

import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

public interface IRequestHandler {
    HttpResponse handle(HttpRequest request);
}
