package ddsl.dcomponents;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataCommonComponent extends DomiSMPPage {

    /**
     * Common component for user data used in Profile and Users page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(UserDataCommonComponent.class);
    @FindBy(id = "changePassword_id")
    public WebElement setChangePasswordBtn;
    @FindBy(id = "smpTheme_id")
    private WebElement themeSel;
    @FindBy(id = "smpLocale_id")
    private WebElement localeSel;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(id = "emailAddress_id")
    private WebElement emailAddressInput;
    @FindBy(id = "fullName_id")
    private WebElement fullNameInput;
    @FindBy(id = "passwordUpdatedOn_id")
    private WebElement lastSetLbl;
    @FindBy(id = "passwordExpireOnMessage_id")
    private WebElement passwordExpiresOnLbl;

    public UserDataCommonComponent(WebDriver driver) {
        super(driver);
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

    public String getLastSetValue() {
        return lastSetLbl.getText();
    }

    public String getPasswordExpiresOnValue() {
        return passwordExpiresOnLbl.getText();
    }

    public SetChangePasswordDialog getChangePasswordDialog() {
        return new SetChangePasswordDialog(driver);
    }


    public String fillUserProfileData(String emailValue, String fullNameValue, String selectThemeValue, String localeValue) {
        try {
            if (!emailValue.isEmpty()) {
                weToDInput(emailAddressInput).fill(emailValue);
            }
            if (!emailValue.isEmpty()) {
                weToDInput(fullNameInput).fill(fullNameValue);
            }
            weToDSelect(themeSel).selectByVisibleText(selectThemeValue);
            weToDSelect(localeSel).selectByVisibleText(localeValue);


        } catch (Exception e) {
            LOG.error("Cannot change User Profile Data ", e);
        }

        if (saveBtn.isEnabled()) {
            saveBtn.click();
        } else {
            LOG.debug("Save button is " + saveBtn.isEnabled());
        }

        try {
            return getAlertArea().getAlertMessage();
        } catch (Exception e) {
            return null;
        }
    }

    public void ChangePassword(String currentPasssword, String newPassword) throws Exception {
        SetChangePasswordDialog dialog = new SetChangePasswordDialog(driver);
        dialog.setNewPassword(currentPasssword, newPassword);
    }

}
