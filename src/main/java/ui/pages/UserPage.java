package ui.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserPage extends BasePage<UserPage> {
    private SelenideElement welcomeText = $(".welcome-text");
    private SelenideElement createNewAccount = $(By.xpath("//*[text()='➕ Create New Account']"));
    private SelenideElement depositMoney = $(By.xpath("//*[text()='\uD83D\uDCB0 Deposit Money']"));
    private SelenideElement makeTransfer = $(By.xpath("//*[text()='\uD83D\uDD04 Make a Transfer']"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserPage createNewAccount() {
        createNewAccount.click();
        return this;
    }
}
