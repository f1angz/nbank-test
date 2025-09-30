package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Attachment;

import static com.codeborne.selenide.Selenide.*;

public class UpdateProfilePage extends BasePage<UpdateProfilePage> {
    private SelenideElement newNameInput = $("input[placeholder]");
    private SelenideElement saveChanges = $x("//button[text()='\uD83D\uDCBE Save Changes']");
    protected SelenideElement userInfo = $("span[class='user-name']");

    @Override
    public String url() {
        return "/edit-profile";
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public UpdateProfilePage editProfile(String newName) {
        newNameInput.sendKeys(newName);
        saveChanges.click();
        return this;
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public SelenideElement getActualUsername() {
        refresh();
        return userInfo;
    }
}
