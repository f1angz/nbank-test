package requests.steps;

import models.CreateAccountRs;
import models.DepositMoneyRq;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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
