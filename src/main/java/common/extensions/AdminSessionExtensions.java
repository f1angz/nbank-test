package common.extensions;

import api.models.CreateUserRq;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtensions implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AdminSession annotation = context.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null) {
            BasePage.authAsUser(CreateUserRq.getAdmin());
        }
    }
}
