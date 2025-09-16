package secondIteration.api;

import api.models.*;
import firstiteration.api.BaseTest;
import api.models.comparison.JsonComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.requests.steps.AccountStep;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utils.TestDataGenerator;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositMoneyTest extends BaseTest {

    @Test
    public void authUserCanDepositMoneyIntoAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        CreateAccountRs account = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());
        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(account.getId())
                .balance(TestDataGenerator.randomBalance())
                .build();

        DepositMoneyRs depositMoneyRs = new ValidatedCrudRequesters<DepositMoneyRs>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.DEPOSIT)
                .post(deposit);

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(deposit, depositMoneyRs);
        softly.assertThat(depositMoneyRs.getTransactions().getFirst().getType()).isEqualTo(TransactionsType.DEPOSIT.toString());
    }

    @Test
    public void unauthUserCannotDepositMoneyIntoAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        CreateAccountRs account = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());
        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(account.getId())
                .balance(TestDataGenerator.randomBalance())
                .build();

        new CrudRequesters(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsUnauth(),
                Endpoint.DEPOSIT)
                .post(deposit);

        assertThat(new CrudRequesters(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.GET_TRANSACTIONS)
                .get(account.getId()).extract().jsonPath().getList("")).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidBalanceDataProvider")
    public void authUserCannotDepositMoneyIfBalanceDataIsNotCorrectTest(Double balance, String error) {
        CreateUserRq user = AdminSteps.createUser();
        CreateAccountRs account = AccountStep.createAccountForUser(user.getUsername(), user.getPassword());
        DepositMoneyRq deposit = DepositMoneyRq.builder()
                .id(account.getId())
                .balance(balance)
                .build();

        new CrudRequesters(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(error),
                Endpoint.DEPOSIT).post(deposit);

        assertThat(new CrudRequesters(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.GET_TRANSACTIONS)
                .get(account.getId()).extract().jsonPath().getList("")).isEmpty();
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
                ResponseSpecs.requestReturnsForbidden(UNAUTH_ACCESS),
                Endpoint.DEPOSIT).post(deposit);
    }

    public static Stream<Arguments> invalidBalanceDataProvider() {
        return Stream.of(
                Arguments.of(-1.0, INVALID_ACCOUNT),
                Arguments.of(0.0, INVALID_ACCOUNT)
        );
    }

    public static Stream<Long> invalidAccountIdProvider() {
        return Stream.of(-1L, 0L, Long.MAX_VALUE);
    }
}
