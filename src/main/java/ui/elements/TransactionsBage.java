package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class TransactionsBage extends BaseElement {
    private String transactionsText;

    public TransactionsBage(SelenideElement element) {
        super(element);
        transactionsText = element.$x(".//span").getText();
    }
}
