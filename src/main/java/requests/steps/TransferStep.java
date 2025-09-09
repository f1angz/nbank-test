package requests.steps;

import models.*;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.TestDataGenerator;

public class TransferStep {

    public static TransferRq createValidTransferRequest(String username, String password, Double amount) {
        CreateUserRq user = AdminSteps.createUser();
        CreateAccountRs account = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());
        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(account.getId())
                .balance(TestDataGenerator.randomBalance())
                .build();

        new CrudRequesters(
                RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.DEPOSIT)
                .post(deposit).extract().as(DepositMoneyRs.class);

        CreateAccountRs receiverAccount = AccountStep.createAccountForUser(username, password);

        return TransferRq.builder()
                .senderAccountId(deposit.getId())
                .receiverAccountId(receiverAccount.getId())
                .amount(amount)
                .build();
    }
}
