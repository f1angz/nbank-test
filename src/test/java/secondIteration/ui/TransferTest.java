package secondIteration.ui;

import api.models.CreateAccountRs;
import api.models.TransactionsType;
import api.utils.TestDataGenerator;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import firstiteration.ui.BaseUiTest;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.TransferPage;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanTransferMoney() {
        CreateAccountRs sendAccount = SessionStorage.getSteps().createAccount();
        CreateAccountRs recAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositMoney(sendAccount.getId(), TestDataGenerator.randomBalance());

        Double amount = TestDataGenerator.randomSmallBalance();
        Boolean actualResult = new TransferPage().open()
                .transferMoney(sendAccount, recAccount, amount)
                .checkAlertMessageAndAccept(BankAlerts.TRANSFER_SUCCESSFULLY)
                .getMatchingTransactions().stream().anyMatch(transactionsBage ->
                        transactionsBage.getTransactionsText().contains(TransactionsType.TRANSFER_OUT + " - $" + String.format("%.2f", amount)));
        assertThat(actualResult).isTrue();

        String expectedBalance = String.format("%.2f", amount);
        actualResult = new TransferPage().open().getMatchingTransactions().stream().anyMatch(transactionsBage ->
                transactionsBage.getTransactionsText().contains(TransactionsType.TRANSFER_IN + " - $" + expectedBalance));
        assertThat(actualResult).isTrue();

        assertThat(SessionStorage.getSteps().getAllTransactions(sendAccount.getId()).size()).isEqualTo(2);
        assertThat(SessionStorage.getSteps().getAllTransactions(recAccount.getId()).size()).isEqualTo(1);
    }

    @Test
    @UserSession
    public void userCannotTransferMoneyWithNotCorrectData() {
        CreateAccountRs sendAccount = SessionStorage.getSteps().createAccount();
        CreateAccountRs recAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositMoney(sendAccount.getId(), TestDataGenerator.randomBalance());
        assertThat(new TransferPage().open()
                .transferMoney(sendAccount, recAccount, -100.0)
                .checkAlertMessageAndAccept(BankAlerts.TRANSFER_ERROR)
                .getMatchingTransactions().stream().noneMatch(
                        transactionsBage -> transactionsBage.getTransactionsText().contains(TransactionsType.TRANSFER_OUT.toString()))).isTrue();

        assertThat(SessionStorage.getSteps().getAllTransactions(sendAccount.getId()).size()).isEqualTo(1);
        assertThat(SessionStorage.getSteps().getAllTransactions(recAccount.getId()).size()).isEqualTo(0);
    }
}
