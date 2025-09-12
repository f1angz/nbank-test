package firstiteration.ui;

import api.generators.EntityGenerator;
import api.models.CreateUserRq;
import api.models.CreateUserRs;
import api.models.comparison.JsonComparator;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.BankAlerts;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseUiTest {

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRq admin = CreateUserRq.getAdmin();

        authAsUser(admin);

        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);

        new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlerts.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAlLUsers()
                .findBy(Condition.exactText(newUser.getUsername() + "\nUSER"))
                .shouldBe(Condition.visible);

        CreateUserRs createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).findFirst().get();

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(newUser, createdUser);
    }

    @Test
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRq admin = CreateUserRq.getAdmin();

        authAsUser(admin);

        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);
        newUser.setUsername("a");

        new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAlLUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);


        long userWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).count();
        assertThat(userWithSameUsernameAsNewUser).isZero();
    }
}
