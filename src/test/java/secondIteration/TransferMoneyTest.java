package secondIteration;

import firstiteration.BaseTest;
import models.*;
import models.comparison.JsonComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.steps.AccountStep;
import requests.steps.AdminSteps;
import requests.steps.TransferStep;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.TestDataGenerator;

import java.util.stream.Stream;

public class TransferMoneyTest extends BaseTest {
    private final JsonComparator comparator = new JsonComparator();

    @Test
    public void successfulTransferBetweenAccounts() {
        CreateUserRq user = AdminSteps.createUser();

        TransferRq transferRq = TransferStep.createValidTransferRequest(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomSmallBalance());

        TransferRs transferRs = new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.TRANSFER)
                .post(transferRq).extract().as(TransferRs.class);

        comparator.assertMatches(transferRq, transferRs);
        softly.assertThat(transferRs.getMessage()).isEqualTo(SUCCESS_TRANSFER);
    }

    @Test
    public void transferWithInsufficientBalance() {
        CreateUserRq user = AdminSteps.createUser();
        CreateAccountRs account = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());
        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(account.getId())
                .balance(TestDataGenerator.randomSmallBalance())
                .build();


        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.DEPOSIT)
                .post(deposit).extract().as(DepositMoneyRs.class);

        CreateAccountRs receiverAccount = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());

        TransferRq transferRq = TransferRq.builder()
                .senderAccountId(deposit.getId())
                .receiverAccountId(receiverAccount.getId())
                .amount(TestDataGenerator.randomBalance())
                .build();

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(INVALID_TRANSFER),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    @Test
    public void transferToNonExistentAccount() {
        CreateUserRq user = AdminSteps.createUser();

        TransferRq transferRq = TransferStep.createValidTransferRequest(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomBalance());
        transferRq.setReceiverAccountId(Long.MAX_VALUE);

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(INVALID_TRANSFER),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    @Test
    public void transferFromNonExistentAccount() {
        CreateUserRq user = AdminSteps.createUser();

        CreateAccountRs receiverAccount = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());

        TransferRq transferRq = TransferRq.builder()
                .senderAccountId(Long.MAX_VALUE)
                .receiverAccountId(receiverAccount.getId())
                .amount(TestDataGenerator.randomSmallBalance())
                .build();

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsForbidden(UNAUTH_ACCESS),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    @Test
    public void transferWithoutAuthentication() {
        CreateUserRq user = AdminSteps.createUser();

        TransferRq transferRq = TransferStep.createValidTransferRequest(
                user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomSmallBalance());

        new CrudRequesters(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsUnauth(),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    @Test
    public void transferToSameAccount() {
        CreateUserRq user = AdminSteps.createUser();

        TransferRq transferRq = TransferStep.createValidTransferRequest(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomBalance());
        transferRq.setReceiverAccountId(transferRq.getSenderAccountId());

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(INVALID_TRANSFER),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmountsProvider")
    public void transferWithInvalidAmount(Double amount) {
        CreateUserRq user = AdminSteps.createUser();

        TransferRq transferRq = TransferStep.createValidTransferRequest(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomSmallBalance());
        transferRq.setAmount(amount);

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(INVALID_TRANSFER),
                Endpoint.TRANSFER)
                .post(transferRq);
    }

    public static Stream<Arguments> invalidTransferAmountsProvider() {
        return Stream.of(
                Arguments.of(-100.0),
                Arguments.of(0.0)
        );
    }
}
