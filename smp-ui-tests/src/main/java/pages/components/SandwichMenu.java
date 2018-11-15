package pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import pages.service_groups.search.SearchPage;
import utils.PROPERTIES;

public class SandwichMenu extends PageComponent {
	public SandwichMenu(WebDriver driver) {
		super(driver);
		log.info("sandwich menu init");
		
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}


	@FindBy(id = "settingsmenu_id")
	WebElement expandoButton;

	@FindBy(css = "button[role=\"menuitem\"] span")
	WebElement currentUserID;

	@FindBy(id = "logout_id")
	WebElement logoutLnk;

	public boolean isLoggedIn(){
		waitForElementToBeClickable(expandoButton).click();

		waitForElementToBeVisible(currentUserID);
		boolean toReturn = !currentUserID.getText().equalsIgnoreCase("Not logged in") ;
		log.info("User login status is: " + toReturn);
		currentUserID.click();
		clickVoidSpace();
		return toReturn;
	}

	public SearchPage logout(){
		waitForElementToBeClickable(expandoButton).click();
		waitForElementToBeClickable(logoutLnk).click();
		log.info("Logging out...");
		return new SearchPage(driver);
	}


}
