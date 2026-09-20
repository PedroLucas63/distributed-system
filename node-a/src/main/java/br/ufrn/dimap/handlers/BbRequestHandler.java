package br.ufrn.dimap.handlers;

import br.ufrn.dimap.api.handlers.IRequestHandler;
import br.ufrn.dimap.api.options.NodeOptions;
import br.ufrn.dimap.application.domain.Account;
import br.ufrn.dimap.application.service.Bank;
import br.ufrn.dimap.http.factories.HttpResponseFactory;
import br.ufrn.dimap.http.types.HttpMethod;
import br.ufrn.dimap.http.types.HttpRequest;
import br.ufrn.dimap.http.types.HttpResponse;
import br.ufrn.dimap.types.AccountResponse;
import br.ufrn.dimap.types.CreateAccountRequest;
import br.ufrn.dimap.types.TransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

public class BbRequestHandler implements IRequestHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Bank bank;
    private final NodeOptions nodeOptions;
    private final HttpResponseFactory responseFactory;

    public BbRequestHandler(Bank bank, NodeOptions nodeOptions, HttpResponseFactory responseFactory) {
        this.bank = bank;
        this.nodeOptions = nodeOptions;
        this.responseFactory = responseFactory;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.method() == HttpMethod.Post
                && request.path().equals("/" + nodeOptions.getPrefix())) {
            return createAccount(request);
        }

        if (request.method() == HttpMethod.Post
                && request.path().equals("/" + nodeOptions.getPrefix() + "/withdraw")) {
            return withdraw(request);
        }

        if (request.method() == HttpMethod.Post
                && request.path().equals("/" + nodeOptions.getPrefix() + "/deposit")) {
            return deposit(request);
        }

        if (request.method() == HttpMethod.Get) {
            var path = request.path();

            if (path.startsWith("/" + nodeOptions.getPrefix())) {
                var parts = Arrays.stream(path.split("/"))
                        .filter(part -> !part.isEmpty())
                        .toList();

                if (parts.size() == 2) {
                    if (parts.get(1).equals("all")) {
                        return processGetAll();
                    }

                    try {
                        var accountNumber = Long.parseLong(parts.get(1));
                        return processGetAccount(accountNumber);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return responseFactory.badRequest("Bad Request");
    }

    private HttpResponse createAccount(HttpRequest request) {
        CreateAccountRequest body;

        try {
            body = MAPPER.readValue(
                    request.body(),
                    CreateAccountRequest.class
            );
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            var account = toResponse(
                bank.createAccount(body.name(), body.accountNumber())
            );

            var location = "/" + nodeOptions.getPrefix()
                    + "/" + account.accountNumber();

            return responseFactory.created(account, location);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }

    private HttpResponse withdraw(HttpRequest request) {
        TransactionRequest body;

        try {
            body = MAPPER.readValue(request.body(), TransactionRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            var account = toResponse(
                bank.withdraw(body.accountNumber(), body.amount())
            );

            return responseFactory.ok(account);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }

    private HttpResponse deposit(HttpRequest request) {
        TransactionRequest body;

        try {
            body = MAPPER.readValue(request.body(), TransactionRequest.class);
        } catch (Exception e) {
            return responseFactory.badRequest("Bad Request");
        }

        if (body == null) {
            return responseFactory.badRequest("Bad Request");
        }

        try {
            var account = toResponse(
                bank.deposit(body.accountNumber(), body.amount())
            );

            return responseFactory.ok(account);
        } catch (Exception e) {
            return responseFactory.badRequest(e.getMessage());
        }
    }

    private HttpResponse processGetAll() {
        var accounts = bank.getAccounts()
            .stream()
            .map(BbRequestHandler::toResponse)
            .toList();

        return responseFactory.ok(accounts);
    }

    private HttpResponse processGetAccount(long accountNumber) {
        var account = toResponse(bank.getAccount(accountNumber));

        if (account == null) {
            return responseFactory.notFound("Account not found");
        }

        return responseFactory.ok(account);
    }

    private static AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        return new AccountResponse(
            account.getName(),
            account.getAccountNumber(),
            account.getBalance()
        );
    }
}
