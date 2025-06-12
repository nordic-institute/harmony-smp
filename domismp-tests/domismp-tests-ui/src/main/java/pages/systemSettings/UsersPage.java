package pages.systemSettings;

import ddsl.commonPages.CommonPageWithTabsAndGrid;
import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.Grid.SmallGrid;
import ddsl.dcomponents.commonComponents.UserDataCommonComponent;
import ddsl.dobjects.DButton;
import ddsl.dobjects.DInput;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.UserModel;
/**
 * Page object for the Users page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class UsersPage extends CommonPageWithTabsAndGrid {
    private final static Logger LOG = LoggerFactory.getLogger(UsersPage.class);
    public UserDataCommonComponent userData;
    @FindBy(id = "username_id")
    private WebElement usernameInput;
    @FindBy(id = "role_id")
    private WebElement applicationRoleDdl;
    @FindBy(id = "active_id")
    private WebElement isActiveCheckBox;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;


    public UsersPage(WebDriver driver) {
        super(driver);
        userData = new UserDataCommonComponent(driver);
        LOG.debug("Loading Users page.");
    }

    @Override
    public SmallGrid getLeftSideGrid() {
        return new SmallGrid(driver, rightPanel);
    }

    public DButton getCreateUserBtn() {
        return new DButton(driver, addBtn);
    }

    public String deleteAndConfirm() {
        weToDButton(deleteBtn).click();
        new ConfirmationDialog(driver).confirm();
        return getAlertMessageAndClose();
    }

    public String fillNewUserDataAndSave(UserModel newUserData) {
        LOG.debug("Filling user data...");
        try {
            weToDInput(usernameInput).fill(newUserData.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        weToDSelect(applicationRoleDdl).selectValue(newUserData.getRole());

        String alertMessage = userData.fillUserProfileDataAndSave(newUserData.getEmailAddress(), newUserData.getFullName(), newUserData.getSmpTheme(), newUserData.getSmpLocale());
        LOG.debug("User {} was created", newUserData.getUsername());
        return alertMessage;
    }

    public void fillNewUserData(UserModel newUserData) {
        LOG.debug("Filling user data...");
        try {
            weToDInput(usernameInput).fill(newUserData.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        weToDSelect(applicationRoleDdl).selectValue(newUserData.getRole());

        userData.fillUserProfileData(newUserData.getEmailAddress(), newUserData.getFullName(), newUserData.getSmpTheme(), newUserData.getSmpLocale());

    }

    public DInput getUsernameInput() {
        return weToDInput(usernameInput);
    }

    public String getApplicationRoleValue() {
        return weToDSelect(applicationRoleDdl).getCurrentValue();
    }

    public String getFullNameValue() {
        return userData.getFullName();
    }

    public Boolean isSelectedUserActive() {
        try {
            return weToDInput(isActiveCheckBox).getAttribute("class").contains("checked");

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

    public void changeApplicationRole(String role) {
        try {
            weToDSelect(applicationRoleDdl).selectByVisibleText(role);
            weToDButton(saveBtn).click();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DButton getSaveBtn() {
        return weToDButton(saveBtn);
    }

    public String modifyIsActiveForUser(Boolean isActive) throws Exception {
        if (isActive) {
            weToDChecked(isActiveCheckBox).check();

        } else {
            weToDChecked(isActiveCheckBox).uncheck();

        }
        if (weToDButton(saveBtn).isEnabled()) ;
        {
            LOG.debug("Changing active value of access token to: [{}]", isActive);

            weToDButton(saveBtn).click();
            return getAlertArea().getAlertMessage();
        }
    }

    public void filterAndSelectUsername(String username) {
        // bug in filter -does not handle char _ O so search only by the last random part of username
        String searchKey = username.contains("_") ? username.substring(username.lastIndexOf("_") + 1) : username;
        weToDInput(filterInput).fill(searchKey);
        getLeftSideGrid().searchAndClickElementInColumn("Username", username);
    }

    public boolean IsUsernamePresentInGrid(String username) {
        weToDInput(filterInput).fill(username);
        return getLeftSideGrid().isValuePresentInColumn("Username", username);
    }

}
