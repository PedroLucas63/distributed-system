package br.ufrn.dimap.handlers;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.types.OperationResponse;
import br.ufrn.dimap.types.OperationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.BiFunction;

public class CalcRequestHandler implements IRequestHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpResponseFactory responseFactory;
    private final String addPrefix;
    private final String subPrefix;
    private final String multPrefix;
    private final String divPrefix;


    public CalcRequestHandler(NodeOptions nodeOptions, HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;

        addPrefix = "/" + nodeOptions.getPrefix() + "/add";
        subPrefix = "/" + nodeOptions.getPrefix() + "/sub";
        multPrefix = "/" + nodeOptions.getPrefix() + "/mult";
        divPrefix = "/" + nodeOptions.getPrefix() + "/div";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.method() == HttpMethod.Get) {
            if (request.path().equals(addPrefix)) {
                return add(request);
            }
            if (request.path().equals(subPrefix)) {
                return sub(request);
            }
            if (request.path().equals(multPrefix)) {
                return mult(request);

            }
            if (request.path().equals(divPrefix)) {
                return div(request);
            }
        }

        return responseFactory.badRequest("Bad Request");
    }

    private HttpResponse add(HttpRequest request)
    {
        return applyOperation(request, (a, b) -> a + b);
    }

    private HttpResponse sub(HttpRequest request)
    {
        return applyOperation(request, (a, b) -> a - b);
    }

    private HttpResponse mult(HttpRequest request)
    {
        return applyOperation(request, (a, b) -> a * b);
    }

    private HttpResponse div(HttpRequest request)
    {
        return applyOperation(request, (a, b) -> a / b);
    }


    private HttpResponse applyOperation(
            HttpRequest request,
            BiFunction<Double, Double, Double> function
    ) {
        OperationRequest body;

        try {
            body = MAPPER.readValue(request.body(), OperationRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            var response = new OperationResponse(function.apply(body.first(), body.second()));
            return responseFactory.ok(response);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }
}
