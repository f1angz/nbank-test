package firstiteration.ui;

import api.configs.Config;
import api.models.CreateUserRq;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import firstiteration.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("ui.remote");
        Configuration.baseUrl = Config.getProperty("ui.baseUrl");
        Configuration.browser = Config.getProperty("ui.browser");
        Configuration.browserSize = Config.getProperty("ui.browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true,
                        "enabledLog", true));
    }

    public void authAsUser(String username, String password) {
        Selenide.open("/login");
        String token = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", token);
    }

    public void authAsUser(CreateUserRq user) {
        authAsUser(user.getUsername(), user.getPassword());
    }
}
