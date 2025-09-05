package requests.steps;

import models.CreateAccountRs;
import models.DepositMoneyRq;
import models.DepositMoneyRs;
import models.TransferRq;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.TestDataGenerator;

public class TransferStep {
    public static final String errorText = "Invalid transfer: insufficient funds or invalid accounts";

    public static TransferRq createValidTransferRequest(String username, String password, Double amount) {
        DepositMoneyRq deposit = AccountStep.createAccountAndDeposit(
                username,
                password,
                TestDataGenerator.randomBalance());

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
