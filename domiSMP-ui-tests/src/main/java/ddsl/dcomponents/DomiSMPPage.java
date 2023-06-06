package ddsl.dcomponents;


import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.LoginPage;

public class DomiSMPPage extends DComponent {
    @FindBy(css = "page-header > h1")
    protected WebElement pageTitle;
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

    public SideNavigation getSidebar() {
        return new SideNavigation(driver);
    }

    public Breadcrump getBreadcrump() {
        return new Breadcrump(driver);
    }

    public LoginPage goToLoginPage() {
        loginBtnTop.click();
        return new LoginPage(driver);
    }

    public LoginPage logout() {
        rightMenuBtn.click();
        logoutMenuBtn.click();
        return new LoginPage(driver);
    }


    public AlertComponent getAlertArea() {
        return new AlertComponent(driver);
    }

    public DButton getExpiredDialoginbutton() {
        return weToDButton(dialogOKbutton);
    }
}
