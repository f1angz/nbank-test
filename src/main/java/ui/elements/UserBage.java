package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class UserBage extends BaseElement {
    private String username;
    private String role;

    public UserBage(SelenideElement element) {
        super(element);
        username = element.getText().split("\n")[0];
        username = element.getText().split("\n")[1];
    }
}
