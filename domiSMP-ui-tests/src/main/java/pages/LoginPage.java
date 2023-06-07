package pages;

import ddsl.dcomponents.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.HashMap;

public class LoginPage extends DomiSMPPage {

    /**
     * Page object for the Login page. This contains the locators of the page and the methods for the behaviour of the page
     */
    @FindBy(id = "username_id")
    private WebElement username;
    @FindBy(id = "password_id")
    private WebElement password;
    @FindBy(id = "loginbutton_id")
    private WebElement loginBtn;

    public LoginPage(WebDriver driver) {
        super(driver);
        log.debug(".... init");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public DomiSMPPage login(String user, String pass) throws Exception {
        HashMap<String, String> usr = new HashMap<>();
        usr.put("username", user);
        usr.put("pass", pass);
        log.debug("Login started " + usr.get("username") + " / " + usr.get("pass"));

        goToLoginPage();
        weToDInput(username).fill(usr.get("username"));
        weToDInput(password).fill(usr.get("pass"));
        weToDButton(loginBtn).click();

        if (getExpiredDialoginbutton().isPresent()) {
            log.info("Expired password dialog is present.");
            getExpiredDialoginbutton().click();
        }


        return new DomiSMPPage(driver);

    }

}
