package br.ufrn.dimap.handlers;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.types.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FinanceRequestHandler implements IRequestHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpResponseFactory responseFactory;
    private final String simpleInterestPrefix;
    private final String compoundInterestPrefix;
    private final String discountPrefix;
    private final String installmentsPrefix;

    public FinanceRequestHandler(NodeOptions nodeOptions, HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;

        simpleInterestPrefix = "/" + nodeOptions.getPrefix() + "/simple-interest";
        compoundInterestPrefix = "/" + nodeOptions.getPrefix() + "/compound-interest";
        discountPrefix = "/" + nodeOptions.getPrefix() + "/discount";
        installmentsPrefix = "/" + nodeOptions.getPrefix() + "/installments";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.method() == HttpMethod.Get) {
            if (request.path().equals(simpleInterestPrefix)) {
                return simpleInterest(request);
            }
            if (request.path().equals(compoundInterestPrefix)) {
                return compoundInterest(request);
            }
            if (request.path().equals(discountPrefix)) {
                return discount(request);

            }
            if (request.path().equals(installmentsPrefix)) {
                return installments(request);
            }
        }

        return responseFactory.badRequest("Bad Request");
    }

    private HttpResponse simpleInterest(HttpRequest request)
    {
        return applyInterest(request, (r) -> {
            var i = r.rate() / 100;
            return r.principal() * (1 + i * r.periods());
        });
    }

    private HttpResponse compoundInterest(HttpRequest request)
    {
        return applyInterest(request, (r) -> {
            var i = r.rate() / 100;
            var rate = Math.pow(1 + i, r.periods());
            var j = r.principal() * rate;
            return r.principal() + j;
        });
    }

    private HttpResponse discount(HttpRequest request)
    {
        DiscountRequest body;

        try {
            body = MAPPER.readValue(request.body(), DiscountRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            double i = body.percentage() / 100;

            if (i < 0.0 || i > 1.0) {
                return responseFactory.badRequest("Bad Request");
            }

            var discount = body.value() * i;
            var result = body.value() - discount;
            var response = new ValueResponse(result);
            return responseFactory.ok(response);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }

    private HttpResponse installments(HttpRequest request)
    {
        InstallmentsRequest body;

        try {
            body = MAPPER.readValue(request.body(), InstallmentsRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            if (body.principal() < 0 ||
                    body.rate() < 0 ||
                    body.numberOfInstallments() <= 0) {
                return responseFactory.badRequest("Bad Request");
            }

            var installments = new ArrayList<InstallmentResponse>();

            var amortization = body.principal() / body.numberOfInstallments();
            var balance = body.principal();

            for (int i = 0; i < body.numberOfInstallments(); i++) {
                var interest = balance * (body.rate() / 100.0);
                var payment = amortization + interest;

                balance -= amortization;

                if (Math.abs(balance) < 0.000001) {
                    balance = 0.0;
                }

                var installment = new InstallmentResponse(
                        i + 1,
                        interest,
                        amortization,
                        payment,
                        balance
                );

                installments.add(installment);
            }

            return responseFactory.ok(installments);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }

    private HttpResponse applyInterest(HttpRequest request, Function<InterestRequest, Double> function)
    {
        InterestRequest body;

        try {
            body = MAPPER.readValue(request.body(), InterestRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            if (body.rate() < 0) {
                return responseFactory.badRequest("Bad Request");
            }

            var response = new ValueResponse(function.apply(body));
            return responseFactory.ok(response);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }}
