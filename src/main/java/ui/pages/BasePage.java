package ui.pages;

import api.models.CreateUserRq;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Attachment;
import lombok.Getter;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage<T extends BasePage> {
    protected SelenideElement usernameInput = $("input[placeholder='Username']");
    protected SelenideElement passwordInput = $("input[placeholder='Password']");

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public T checkAlertMessageAndAccept(BankAlerts bankAlert) {
        Alert alert = switchTo().alert();
        String actual = alert.getText();

        if (bankAlert.isRegex()) {
            assertThat(actual).matches(bankAlert.getMessage());
        } else {
            assertThat(actual).contains(bankAlert.getMessage());
        }

        alert.accept();
        return (T) this;
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/login");
        String token = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", token);
    }

    public static void authAsUser(CreateUserRq user) {
        authAsUser(user.getUsername(), user.getPassword());
    }

    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elements, Function<SelenideElement, T> constructor) {
        return elements.stream().map(constructor).toList();
    }
}
