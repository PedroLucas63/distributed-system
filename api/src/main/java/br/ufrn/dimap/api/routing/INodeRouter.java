package br.ufrn.dimap.api.routing;

import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;

public interface INodeRouter {
    HttpResponse routeRequest(HttpRequest request);
}
