package firstiteration.ui;

import api.generators.EntityGenerator;
import api.models.CreateUserRq;
import api.models.CreateUserRs;
import api.models.comparison.JsonComparator;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.BankAlerts;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseUiTest {

    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);

        assertThat(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlerts.USER_CREATED_SUCCESSFULLY)
                .getAlLUsers()
                .stream().anyMatch(userBage -> userBage.getUsername().equals(newUser.getUsername()))).isTrue();

        CreateUserRs createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).findFirst().get();

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(newUser, createdUser);
    }

    @Test
    @AdminSession
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);
        newUser.setUsername("a");

        assertThat(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlerts.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS)
                .getAlLUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername()))).isTrue();


        long userWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).count();
        assertThat(userWithSameUsernameAsNewUser).isZero();
    }
}
