package pages;

import ddsl.dcomponents.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class ProfilePage extends DomiSMPPage {

    @FindBy(id = "smpTheme_id")
    private WebElement themeSel;
    @FindBy(id = "moment-locale")
    private WebElement localeSel;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(id = "emailAddress_id")
    private WebElement emailAddressInput;
    @FindBy(id = "fullName_id")
    private WebElement fullNameInput;

    public ProfilePage(WebDriver driver) {
        super(driver);
        log.debug(".... init");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public void changeUserProfileData(String emailValue, String fullNameValue, String selectThemeValue, String localeValue) throws Exception {
        try {
            if (!emailValue.isEmpty()) {
                weToDInput(emailAddressInput).fill(emailValue);
            }
            if (!emailValue.isEmpty()) {
                weToDInput(fullNameInput).fill(fullNameValue);
            }
            if (!(selectThemeValue == null)) {
                weToDSelect(themeSel).selectValue(selectThemeValue.toString());
            }
            if (!localeValue.isEmpty()) {
                weToDSelect(localeSel).selectValue(localeValue);
            }

        } catch (Exception e) {
            log.error("Cannot change User Profile Data ", e);
        }

        if (saveBtn.isEnabled()) {
            saveBtn.click();
        } else {
            log.debug("Save button enable is " + saveBtn.isEnabled());
        }

        try {
            getAlertArea().getAlertMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public String getSelectedTheme() {
        return weToDSelect(themeSel).getCurrentValue();
    }

    public String getSelectedLocale() {
        return weToDSelect(localeSel).getCurrentValue();
    }

    public String getEmailAddress() {
        return weToDInput(emailAddressInput).getText();
    }

    public String getFullName() {
        return weToDInput(fullNameInput).getText();
    }


}