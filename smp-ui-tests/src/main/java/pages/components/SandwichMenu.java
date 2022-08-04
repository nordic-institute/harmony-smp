package pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import pages.password.PasswordChangepopup;
import pages.service_groups.search.SearchPage;
import utils.PROPERTIES;

public class SandwichMenu extends PageComponent {
	public SandwichMenu(WebDriver driver) {
		super(driver);
		log.info("sandwich menu init");
		
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}


	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "settingsmenu_id")
	WebElement expandoButton;

	@FindBy(css = "div.mat-menu-content")
	WebElement lnkContainer;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "currentuser_id")
	WebElement currentUserID;

	@FindBy(id = "logout_id")
	WebElement logoutLnk;

	@FindBy(id = "changePassword_id")
	WebElement passChangeLnk;


	public boolean isLoggedIn(){
		clickVoidSpace();

		waitForElementToBeClickable(expandoButton).click();

		boolean isLoggedIn = false;
		try {
			String text = waitForElementToBeVisible(lnkContainer).getText();
			isLoggedIn = !text.contains("Not logged in");
		} catch (Exception e) {		}

		log.info("User login status is: " + isLoggedIn);

		clickVoidSpace();
		return isLoggedIn;
	}
	public PasswordChangepopup clickChangePasswordOption()
	{
		waitForElementToBeClickable(expandoButton).click();
		waitForElementToBeClickable(passChangeLnk).click();
		return new PasswordChangepopup(driver);
	}

	public SearchPage logout(){
		clickVoidSpace();

		if(isLoggedIn()){
			waitForElementToBeClickable(expandoButton).click();
			waitForElementToBeClickable(logoutLnk).click();
			log.info("Logging out...");
		}
		return new SearchPage(driver);
	}

	public void waitForSandwichMenu(){
		log.info("waiting for sandwich menu");
		waitForXMillis(500);
		waitForElementToBeVisible(expandoButton);
	}

}
