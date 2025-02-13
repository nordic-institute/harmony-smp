package pages;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
/**
 * Page object for the Login page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class LoginPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(LoginPage.class);
    @FindBy(id = "username_id")
    private WebElement usernameInput;
    @FindBy(id = "password_id")
    private WebElement passwordInput;
    @FindBy(id = "loginbutton_id")
    private WebElement loginBtn;
    @FindBy(css = ".mat-mdc-tab-labels > div:nth-child(2)")
    private WebElement goToResetPasswordTab;
    @FindBy(id = "reset_username_id")
    private WebElement resetUsernameInput;
    @FindBy(id = "resetbutton_id")
    private WebElement requestResetPasswordBtn;

    public LoginPage(WebDriver driver) {
        super(driver);
        LOG.debug(".... init");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public void login(String user, String pass) throws Exception {
        HashMap<String, String> usr = new HashMap<>();
        usr.put("username", user);
        usr.put("pass", pass);
        LOG.debug("Login started " + usr.get("username") + " / " + usr.get("pass"));

        goToLoginPage();
        weToDInput(usernameInput).fill(usr.get("username"));
        weToDInput(passwordInput).fill(usr.get("pass"));
        weToDButton(loginBtn).click();

        try {
            if (getBreadcrump().getCurrentPage().equals("Login")) {
                getExpiredDialoginbutton().click();
            }
        } catch (Exception e) {
            LOG.debug("Password expiration popup is not present");
        }
    }

    public String resetPassword(String user) {
        LOG.debug("Resetting password for : " + user);
        goToLoginPage();

        weToDButton(goToResetPasswordTab).click();
        weToDInput(resetUsernameInput).fill(user);
        weToDButton(requestResetPasswordBtn).click();
        return getAlertArea().getAlertMessage();
    }
}

