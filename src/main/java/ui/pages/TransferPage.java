package ui.pages;

import api.models.CreateAccountRs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.elements.TransactionsBage;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.*;

public class TransferPage extends BasePage<TransferPage>{
    private SelenideElement transferAgain = $x("//button[text()='\uD83D\uDD01 Transfer Again']");
    private SelenideElement newTransfer = $x("//button[text()='\uD83C\uDD95 New Transfer']");
    private SelenideElement selectYourAccount = $("select.account-selector");
    private SelenideElement recipientAccountNumber = $("input[placeholder='Enter recipient account number']");
    private SelenideElement amount = $("input[placeholder='Enter amount']");
    private SelenideElement checkbox = $("input[type='checkbox']");
    private SelenideElement sendTransfer = $x("//button[text()='\uD83D\uDE80 Send Transfer']");

    @Override
    public String url() {
        return "/transfer";
    }

    public List<TransactionsBage> getMatchingTransactions() {
        refresh();
        transferAgain.click();
        ElementsCollection collection = $$("ul.list-group > li").shouldHave(sizeGreaterThan(0));
        return generatePageElements(collection, TransactionsBage::new);
    }

    public TransferPage transferMoney(CreateAccountRs senderAccNumber, CreateAccountRs recipientAccNumber, Double amount) {
        newTransfer.click();
        selectYourAccount.selectOptionByValue(senderAccNumber.getId().toString());
        recipientAccountNumber.sendKeys(recipientAccNumber.getAccountNumber());
        this.amount.sendKeys(amount.toString());
        checkbox.click();
        sendTransfer.click();
        return this;
    }
}
