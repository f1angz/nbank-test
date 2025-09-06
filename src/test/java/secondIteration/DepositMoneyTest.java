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
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.TestDataGenerator;

import java.util.stream.Stream;

public class DepositMoneyTest extends BaseTest {

    @Test
    public void authUserCanDepositMoneyIntoAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        DepositMoneyRq deposit = AccountStep.createAccountAndDeposit(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomBalance());

        DepositMoneyRs depositMoneyRs = new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.DEPOSIT)
                .post(deposit).extract().as(DepositMoneyRs.class);

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(deposit, depositMoneyRs);
        softly.assertThat(depositMoneyRs.getTransactions().getFirst().getType()).isEqualTo(TransactionsType.DEPOSIT.toString());
    }

    @Test
    public void unauthUserCannotDepositMoneyIntoAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        DepositMoneyRq deposit = AccountStep.createAccountAndDeposit(user.getUsername(),
                user.getPassword(),
                TestDataGenerator.randomBalance());

        new CrudRequesters(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsUnauth(),
                Endpoint.DEPOSIT)
                .post(deposit);
    }

    @ParameterizedTest
    @MethodSource("invalidBalanceDataProvider")
    public void authUserCannotDepositMoneyIfBalanceDataIsNotCorrectTest(Double balance, String error) {
        CreateUserRq user = AdminSteps.createUser();

        DepositMoneyRq deposit = AccountStep.createAccountAndDeposit(user.getUsername(),
                user.getPassword(),
                balance);

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(error),
                Endpoint.DEPOSIT).post(deposit);
    }

    @ParameterizedTest
    @MethodSource("invalidAccountIdProvider")
    public void authUserCannotDepositMoneyIfAccountNonExistentTest(Long accountId) {
        CreateUserRq user = AdminSteps.createUser();

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsCreated(),
                Endpoint.ACCOUNTS).post(null).extract().as(CreateAccountRs.class);

        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(accountId)
                .balance(TestDataGenerator.randomBalance())
                .build();

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsForbidden("Unauthorized access to account"),
                Endpoint.DEPOSIT).post(deposit);
    }

    public static Stream<Arguments> invalidBalanceDataProvider() {
        return Stream.of(
                Arguments.of(-1.0, "Invalid account or amount"),
                Arguments.of(0.0, "Invalid account or amount")
        );
    }

    public static Stream<Long> invalidAccountIdProvider() {
        return Stream.of(-1L, 0L, Long.MAX_VALUE);
    }
}
