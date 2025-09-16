package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utils.TestDataGenerator;

public class TransferStep {

    public static TransferRq createValidTransferRequest(String username, String password, Double amount) {
        CreateAccountRs account = AccountStep.createAccountForUser(username, password);
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
