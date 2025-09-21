package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;
import lombok.Getter;
import org.openqa.selenium.By;
import ui.elements.UserBage;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {
    private SelenideElement adminPanelText = $(By.xpath("//*[text()='Admin Panel']"));
    private SelenideElement addUserButton = $(By.xpath("//button[text()='Add User']"));

    @Override
    public String url() {
        return "/admin";
    }

    public AdminPanel createUser(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        addUserButton.click();
        return this;
    }

    public List<UserBage> getAlLUsers() {
        ElementsCollection collection = $(By.xpath(("//*[text()='All Users']"))).parent().findAll("li");
        return generatePageElements(collection, UserBage::new);
    }

    public UserBage findUserByUsername(String username) {
        return RetryUtils.retry(
                () -> getAlLUsers().stream().filter(it -> it.getUsername().equals(username)).findFirst().orElse(null),
                result -> result != null,
                3,
                300
        );
    }
}
