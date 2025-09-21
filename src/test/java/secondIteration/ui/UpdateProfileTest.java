package secondIteration.ui;

import api.generators.EntityGenerator;
import api.models.CustomerRs;
import api.models.UpdateProfileRq;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import firstiteration.ui.BaseUiTest;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.UpdateProfilePage;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateProfileTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanEditProfile() {
        UpdateProfileRq updateRequest = EntityGenerator.generate(UpdateProfileRq.class);

        assertThat(new UpdateProfilePage().open()
                .editProfile(updateRequest.getName())
               .checkAlertMessageAndAccept(BankAlerts.UPDATE_NAME)
               .getActualUsername().getText()).contains(updateRequest.getName());

        CustomerRs updateProfileRs = SessionStorage.getSteps().getUserProfile();
        assertThat(updateProfileRs.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    @UserSession
    public void userCanEditProfileWithNotCorrectData() {
        UpdateProfileRq updateRequest = EntityGenerator.generate(UpdateProfileRq.class);
        updateRequest.setName("123");

        assertThat(new UpdateProfilePage().open()
                .editProfile(updateRequest.getName())
                .checkAlertMessageAndAccept(BankAlerts.UPDATE_NAME_ERROR));

        CustomerRs updateProfileRs = SessionStorage.getSteps().getUserProfile();
        assertThat(updateProfileRs.getName()).isNotEqualTo(updateRequest.getName());
    }
}
