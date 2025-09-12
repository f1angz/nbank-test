package firstiteration.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import models.CreateUserRq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import requests.steps.AdminSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.24.80.1:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true,
                        "enabledLog", true));
    }

    @Test
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRq admin = CreateUserRq.builder().username("admin").password("admin").build();

        Selenide.open("/login");
        $("input[placeholder='Username']").sendKeys(admin.getUsername());
        $("input[placeholder='Password']").sendKeys(admin.getPassword());
        $("button").click();

        $(By.xpath("//*[text()='Admin Panel']")).shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRq user = AdminSteps.createUser();

        Selenide.open("/login");
        $("input[placeholder='Username']").sendKeys(user.getUsername());
        $("input[placeholder='Password']").sendKeys(user.getPassword());
        $("button").click();

        $(".welcome-text").shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
