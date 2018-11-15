package pages.components.baseComponents;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.GenericSelect;
import pages.components.SandwichMenu;
import pages.login.LoginPage;
import utils.PROPERTIES;

public class Header extends PageComponent{

	public Header(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@FindBy(css = "page-header > h1")
	private WebElement pageTitle;

	@FindBy(css = ".helpMenu")
	private WebElement helpLnk;

	@FindBy(css = "#sandwichMenu a")
	private WebElement loginLnk;

	public SandwichMenu sandwichMenu = new SandwichMenu(driver);

	public LoginPage goToLogin(){
		waitForElementToBeClickable(loginLnk).click();
		return new LoginPage(driver);
	}


}
