package api.requests.steps;

import api.models.CreateAccountRs;
import api.models.DepositMoneyRq;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class AccountStep {

    public static CreateAccountRs createAccountForUser(String username, String password) {
        return new CrudRequesters(
                RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsCreated(),
                Endpoint.ACCOUNTS).post(null).extract().as(CreateAccountRs.class);
    }

    public static DepositMoneyRq createAccountAndDeposit(CreateAccountRs account, Double balance) {
        return DepositMoneyRq.builder()
                .id(account.getId())
                .balance(balance)
                .build();
    }
}
