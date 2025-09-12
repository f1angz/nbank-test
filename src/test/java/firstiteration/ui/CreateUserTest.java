package firstiteration.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import generators.EntityGenerator;
import models.CreateUserRq;
import models.CreateUserRs;
import models.comparison.JsonComparator;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import specs.RequestSpecs;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest {

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
    public void adminCanCreateUserTest() {
        CreateUserRq admin = CreateUserRq.builder().username("admin").password("admin").build();

        Selenide.open("/login");
        $("input[placeholder='Username']").sendKeys(admin.getUsername());
        $("input[placeholder='Password']").sendKeys(admin.getPassword());
        $("button").click();

        $(By.xpath("//*[text()='Admin Panel']")).shouldBe(Condition.visible);

        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);
        $("input[placeholder='Username']").sendKeys(newUser.getUsername());
        $("input[placeholder='Password']").sendKeys(newUser.getPassword());
        $(By.xpath("//button[text()='Add User']")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("✅ User created successfully!");
        alert.accept();

        ElementsCollection allUsersFromDashboard = $(By.xpath(("//*[text()='All Users']"))).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        CreateUserRs[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(CreateUserRs[].class);
        CreateUserRs createdUser = Arrays.stream(users).filter(user -> user.getUsername().equals(newUser.getUsername())).findFirst().get();

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(newUser, createdUser);
    }

    @Test
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRq admin = CreateUserRq.builder().username("admin").password("admin").build();

        Selenide.open("/login");
        $("input[placeholder='Username']").sendKeys(admin.getUsername());
        $("input[placeholder='Password']").sendKeys(admin.getPassword());
        $("button").click();

        $(By.xpath("//*[text()='Admin Panel']")).shouldBe(Condition.visible);

        CreateUserRq newUser = EntityGenerator.generate(CreateUserRq.class);
        newUser.setUsername("a");

        $("input[placeholder='Username']").sendKeys(newUser.getUsername());
        $("input[placeholder='Password']").sendKeys(newUser.getPassword());
        $(By.xpath("//button[text()='Add User']")).click();

        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("❌ Failed to create user:\n" +
                "\n" +
                "• username: Username must be between 3 and 15 characters");
        alert.accept();

        ElementsCollection allUsersFromDashboard = $(By.xpath(("//*[text()='All Users']"))).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

        CreateUserRs[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(CreateUserRs[].class);
        long userWithSameUsernameAsNewUser = Arrays.stream(users).filter(user -> user.getUsername().equals(newUser.getUsername())).count();

        assertThat(userWithSameUsernameAsNewUser).isZero();
    }
}
