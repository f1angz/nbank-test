package firstiteration.ui;

import api.models.CreateAccountRs;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.UserPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        new UserPage().open().createNewAccount();

        List<CreateAccountRs> createdAccounts = SessionStorage.getSteps()
                .getAllAccounts();
        assertThat(createdAccounts).hasSize(1);

        new UserPage().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.getFirst().getAccountNumber());

        assertThat(createdAccounts.getFirst().getBalance()).isZero();

    }
}
