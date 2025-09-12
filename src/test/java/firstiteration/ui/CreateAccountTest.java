package firstiteration.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import models.CreateAccountRs;
import models.CreateUserRq;
import models.CreateUserRs;
import models.LoginUserRq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest {

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
    public void userCanCreateAccountTest() {
        CreateUserRq user = AdminSteps.createUser();
        String userAuthHeader = new CrudRequesters(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.LOGIN)
                .post(LoginUserRq.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/login");
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userAuthHeader);
        Selenide.open("/dashboard");

        $(By.xpath("//*[text()='➕ Create New Account']")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains("✅ New Account Created! Account Number:");
        alert.accept();

        Pattern pattern = Pattern.compile("Account Number: (\\w+)");
        Matcher matcher = pattern.matcher(alertText);
        matcher.find();
        String createdAccNumber = matcher.group(1);

        CreateAccountRs[] existingUserAccount = given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .extract().as(CreateAccountRs[].class);

        CreateAccountRs createdAccount = Arrays.stream(existingUserAccount).filter(account -> account.getAccountNumber().equals(createdAccNumber)).findFirst().get();
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
        assertThat(existingUserAccount).hasSize(1);

    }
}
