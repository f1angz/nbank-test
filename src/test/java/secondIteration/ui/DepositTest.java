package secondIteration.ui;

import api.models.CreateAccountRs;
import api.models.TransactionsType;
import api.utils.TestDataGenerator;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import firstiteration.ui.BaseUiTest;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.DepositMoneyPage;
import ui.pages.TransferPage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DepositTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanDepositMoneyOnAccount() {
        Double balance = TestDataGenerator.randomBalance();
        CreateAccountRs account = SessionStorage.getSteps().createAccount();

        assertThat(new DepositMoneyPage().open()
                .depositMoney(1, balance)
                .checkAlertMessageAndAccept(BankAlerts.DEPOSIT_SUCCESSFULLY)
                .getPage(TransferPage.class).open()
                .getMatchingTransactions().stream().anyMatch(transactionsBage ->
                        transactionsBage.getTransactionsText().contains(TransactionsType.DEPOSIT + " - $" + String.format("%.2f", balance)))).isTrue();

        assertThat(SessionStorage.getSteps().getAllTransactions(account.getId()).size()).isEqualTo(1);
    }

    @Test
    @UserSession
    public void userCannotDepositMoneyWithNotCorrectData() {
        CreateAccountRs account = SessionStorage.getSteps().createAccount();

        new DepositMoneyPage().open()
                .depositMoney(1, -100.0)
                .checkAlertMessageAndAccept(BankAlerts.ENTER_VALID_AMOUNT);
    }
}
