package pages;

import ddsl.PageWithGrid;
import ddsl.dcomponents.UserDataCommonComponent;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.UserModel;

public class UsersPage extends PageWithGrid {
    /**
     * Page object for the Users page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(UsersPage.class);

    public UserDataCommonComponent userData;
    @FindBy(id = "username_id")
    private WebElement usernameInput;
    @FindBy(id = "role_id")
    private WebElement applicationRoleDdl;
    @FindBy(id = "active_id")
    private WebElement isActive;


    public UsersPage(WebDriver driver) {
        super(driver);
        userData = new UserDataCommonComponent(driver);
        LOG.debug("Loading Users page.");
    }

    public DButton getCreateUserBtn() {
        return new DButton(driver, AddBtn);
    }

    public String fillNewUserDataAndSave(UserModel newUserData) {
        LOG.debug("Filling user data...");
        try {
            weToDInput(usernameInput).fill(newUserData.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        weToDSelect(applicationRoleDdl).selectValue(newUserData.getRole());

        String alertMessage = userData.fillUserProfileData(newUserData.getEmailAddress(), newUserData.getFullName(), newUserData.getSmpTheme(), newUserData.getSmpLocale());
        LOG.debug("User {} was created", newUserData.getUsername());
        return alertMessage;
    }

    public String getApplicationRoleValue() {
        return weToDSelect(applicationRoleDdl).getCurrentValue();
    }

    public String getFullNameValue() {
        return userData.getFullName();
    }

    public Boolean isSelectedUserActive() {
        try {
            return weToDInput(isActive).getAttribute("class").contains("checked");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailValue() {
        return userData.getEmailAddress();
    }

    public String getSelectedThemeValue() {
        return userData.getSelectedTheme();

    }

    public String getSelectedLocaleValue() {
        return userData.getSelectedLocale();

    }


}
