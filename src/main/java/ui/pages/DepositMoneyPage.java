package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class DepositMoneyPage extends BasePage<DepositMoneyPage> {
    private SelenideElement accountSelect = $("select.account-selector");
    private SelenideElement amountInput = $("input[placeholder='Enter amount']");
    private SelenideElement depositButton = $x("//button[text()='\uD83D\uDCB5 Deposit']");


    @Override
    public String url() {
        return "/deposit";
    }

    public DepositMoneyPage depositMoney(int accountNumber, Double amount)  {
        accountSelect.selectOption(accountNumber);
        amountInput.sendKeys(amount.toString());
        depositButton.click();
        return this;
    }
}
