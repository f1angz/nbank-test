package firstiteration.ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.extensions.AdminSessionExtensions;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import firstiteration.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith({AdminSessionExtensions.class,
        UserSessionExtension.class,
        BrowserMatchExtension.class
})
public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("ui.remote");
        Configuration.baseUrl = Config.getProperty("ui.baseUrl");
        Configuration.browser = Config.getProperty("ui.browser");
        Configuration.browserSize = Config.getProperty("ui.browserSize");
        Configuration.headless = true;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", false,
                        "enabledLog", true));
    }
}
