package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class UpdateProfilePage extends BasePage<UpdateProfilePage> {
    private SelenideElement newNameInput = $("input[placeholder]");
    private SelenideElement saveChanges = $x("//button[text()='\uD83D\uDCBE Save Changes']");
    protected SelenideElement userInfo = $("span[class='user-name']");

    @Override
    public String url() {
        return "/edit-profile";
    }

    public UpdateProfilePage editProfile(String newName) {
        newNameInput.sendKeys(newName);
        saveChanges.click();
        return this;
    }

    public SelenideElement getActualUsername() {
        refresh();
        return userInfo;
    }
}
