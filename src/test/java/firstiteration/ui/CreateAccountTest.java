package firstiteration.ui;

import api.models.CreateAccountRs;
import api.models.CreateUserRq;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.UserPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        authAsUser(user);

        Selenide.open("/dashboard");

        new UserPage().open().createNewAccount();

        List<CreateAccountRs> createdAccounts = new UserSteps(user.getUsername(), user.getPassword())
                .getAllAccounts();
        assertThat(createdAccounts).hasSize(1);

        new UserPage().checkAlertMessageAndAccept(BankAlerts.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.getFirst().getAccountNumber());

        assertThat(createdAccounts.getFirst().getBalance()).isZero();

    }
}
