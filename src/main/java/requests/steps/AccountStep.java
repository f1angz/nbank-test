package requests.steps;

import models.CreateAccountRs;
import models.CreateUserRq;
import models.DepositMoneyRq;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Random;

public class AccountStep {

    public static DepositMoneyRq createAccountAndDeposit(String username, String password, Double balance) {
        CreateAccountRs account = new CrudRequesters(
                RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsCreated(),
                Endpoint.ACCOUNTS).post(null).extract().as(CreateAccountRs.class);

        return DepositMoneyRq.builder()
                .id(account.getId())
                .balance(balance)
                .build();
    }
}
