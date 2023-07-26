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
    }

    public DButton getCreateUserBtn() {
        return new DButton(driver, AddBtn);
    }

    public void fillNewUserData(UserModel newUserData) {
        try {
            weToDInput(usernameInput).fill(newUserData.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        weToDSelect(applicationRoleDdl).selectValue(newUserData.getRole());

        userData.fillUserProfileData(newUserData.getEmailAddress(), newUserData.getFullName(), newUserData.getSmpTheme(), newUserData.getSmpLocale());
    }


}
