package pages.components.baseComponents;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.SandwichMenu;
import pages.login.LoginPage;
import pages.password.PasswordChangepopup;
import utils.PROPERTIES;

public class Header extends PageComponent{

	public Header(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@FindBy(id = "_header_id")
	private WebElement pageTitle;

	@FindBy(css = ".helpMenu")
	private WebElement helpLnk;

	@FindBy(css = "#sandwichMenu a")
	private WebElement loginLnk;

	@FindBy(css = "#sandwichMenu .ng-star-inserted")
	private WebElement role;

	@FindBy(css = "#changePassword_id")
	private WebElement changePasswordOption;

	public SandwichMenu sandwichMenu = new SandwichMenu(driver);

	public PasswordChangepopup clickChangePasswordOption()
	{
		log.info("Clicking on changepassword option");
		waitForElementToBeClickable(changePasswordOption).click();
		return new PasswordChangepopup(driver);
	}

	public LoginPage goToLogin(){
		log.info("Going to login page");
		waitForElementToBeClickable(loginLnk).click();
		return new LoginPage(driver);
	}


	public void waitForTitleToBe(String title){
		log.info("waiting for page title to be " + title);
		waitForXMillis(500);
		waitForElementToHaveText(pageTitle, title);
	}

	public void waitForTitleToBe(){
		log.info("waiting for page title to be present");
		waitForXMillis(500);
		waitForElementToBeVisible(pageTitle);
	}

	public String getRoleName()
	{
		String getUserRole = role.getText();
		String roleName= getUserRole.split(":")[0].trim();
		return roleName;
	}
}
