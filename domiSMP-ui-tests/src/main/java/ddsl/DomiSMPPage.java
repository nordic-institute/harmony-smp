package ddsl;


import ddsl.dcomponents.AlertComponent;
import ddsl.dcomponents.BreadcrumpComponent;
import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.SideNavigationComponent;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.LoginPage;

public class DomiSMPPage extends DComponent {

    /**
     * Page object for the common components from Domismp like navigation, right menu. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(DomiSMPPage.class);
    @FindBy(css = "cdk-overlay-backdrop cdk-overlay-dark-backdrop cdk-overlay-backdrop-showing")
    protected WebElement overlay;
    @FindBy(id = "login_id")
    private WebElement loginBtnTop;
    @FindBy(id = "settingsmenu_id")
    private WebElement rightMenuBtn;
    @FindBy(id = "logout_id")
    private WebElement logoutMenuBtn;
    @FindBy(className = "smp-expired-password-dialog")
    private WebElement expiredPasswordDialog;

    @FindBy(css = "#okbuttondialog_id ")
    private WebElement dialogOKbutton;


    public DomiSMPPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public SideNavigationComponent getSidebar() {
        return new SideNavigationComponent(driver);
    }

    public BreadcrumpComponent getBreadcrump() {
        return new BreadcrumpComponent(driver);
    }

    public LoginPage goToLoginPage() {
        loginBtnTop.click();
        return new LoginPage(driver);
    }

    public void logout() {
        rightMenuBtn.click();
        logoutMenuBtn.click();

    }

    public void refreshPage() {
        driver.navigate().refresh();
        waitForPageToLoaded();
    }

    public AlertComponent getAlertArea() {
        return new AlertComponent(driver);
    }

    public DButton getExpiredDialoginbutton() {
        return weToDButton(dialogOKbutton);
    }

    public boolean isExpiredDialoginbuttonEnabled() {
        return dialogOKbutton.isEnabled();
    }

    public boolean isExpiredPopupEnabled() {
        try {
            return dialogOKbutton.isDisplayed();
        } catch (Exception e) {
            LOG.info("Expiration poup not found", e);
            return false;
        }

    }

    public void closeExpirationPopupIfEnabled() {
        try {
            if (isExpiredPopupEnabled()) {
                LOG.info("Expired password dialog is present.");
                getSidebar().getExpiredDialoginbutton().click();
            }
        } catch (Exception e) {
            LOG.error("Could not close Expiration popup", e);
        }
    }

    public void waitForPageToLoaded() {
        wait.defaultWait.until(ExpectedConditions.visibilityOf(getBreadcrump().BreadcrumpItems.get(0)));
    }

}
