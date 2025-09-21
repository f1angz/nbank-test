package firstiteration.ui;

import api.models.CreateUserRq;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.Browsers;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.BankAlerts;
import ui.pages.LoginPage;
import ui.pages.UserPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUserTest extends BaseUiTest {

    @Test
    @Browsers(values = {"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRq admin = CreateUserRq.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class)
                        .getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRq user = AdminSteps.createUser();
        new LoginPage().open().login(user.getUsername(), user.getPassword()).getPage(UserPage.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
