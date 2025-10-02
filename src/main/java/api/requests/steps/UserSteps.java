package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.List;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<CreateAccountRs> getAllAccounts() {
        return StepLogger.log("Пользователь " + username + " получает список всех аккаунтов", () -> {
            return new ValidatedCrudRequesters<CreateAccountRs>(
                    RequestSpecs.authAsUser(username, password),
                    ResponseSpecs.requestReturnsOK(),
                    Endpoint.CUSTOMER_ACCOUNTS
            ).getAll(CreateAccountRs[].class);
        });
    }

    public CreateAccountRs createAccount() {
        return new ValidatedCrudRequesters<CreateAccountRs>(
                RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsCreated(),
                Endpoint.ACCOUNTS).post(null);
    }

    public List<TransactionsRs> getAllTransactions(long id) {
        return new CrudRequesters(RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.GET_TRANSACTIONS)
                .get(id).extract().jsonPath().getList("");
    }

    public void depositMoney(long id, double amount) {
        new CrudRequesters(RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.DEPOSIT)
                .post(DepositMoneyRq.builder().id(id).balance(amount).build());
    }

    public CustomerRs getUserProfile() {
        return new ValidatedCrudRequesters<CustomerRs>(RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.GET_PROFILE)
                .get(null);
    }
}
