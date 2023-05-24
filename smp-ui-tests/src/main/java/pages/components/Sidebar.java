package pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.Header;
import pages.components.baseComponents.PageComponent;
import pages.components.baseComponents.SMPPage;
import utils.PROPERTIES;

public class Sidebar extends PageComponent {

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(tagName = "mat-sidenav")
	private WebElement sideBar;
	private WebElement topLogo;
	private WebElement topLogoText;
	@FindBy(id = "sidebar_search_id")
	private WebElement searchLnk;
	@FindBy(id = "sidebar_edit_id")
	private WebElement editLnk;
	@FindBy(id = "sidebar_domain_id")
	private WebElement domainLnk;
	@FindBy(id = "sidebar_user_id")
	private WebElement userLnk;
	@FindBy(id = "sidebar_property_id")
	private WebElement propertyLnk;
	@FindBy(css = "mat-icon[role=img][mattooltip=Collapse]")
	private WebElement collapseButton;
	@FindBy(xpath = "//button[@id='sidebar_search_id']//span[text()='Search']")
	private WebElement sidebarSearchText;

	public Sidebar(WebDriver driver) {
		super(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	/* Receives the Page object class as parameter and based on the class name it navigates to the appropriate page
	 and returns an instance of that class */
	public <T extends SMPPage> T goToPage(Class<T> expect) {
		log.info("Navigating to " + expect.getSimpleName());

		switch (expect.getSimpleName()) {
			case "SearchPage":
				waitForElementToBeClickable(searchLnk).click();
				break;
			case "EditPage":
				waitForElementToBeClickable(editLnk).click();
				break;
			case "DomainPage":
				waitForElementToBeClickable(domainLnk).click();
				break;
			case "UsersPage":
				waitForElementToBeClickable(userLnk).click();
				break;
			case "PropertiesPage":
				waitForElementToBeClickable(propertyLnk).click();
				break;
		}

		waitForXMillis(500);

		new Header(driver).waitForTitleToBe();

		waitForRowsToLoad();

		return PageFactory.initElements(driver, expect);
	}

	public boolean isSearchLnkEnabled() {
		return isVisible(searchLnk) && isEnabled(searchLnk);
	}

	public boolean isEditLnkEnabled() {
		return isVisible(editLnk) && isEnabled(editLnk);
	}

	public boolean isDomainLnkEnabled() {
		return isVisible(domainLnk) && isEnabled(domainLnk);
	}

	public boolean isUsersLnkEnabled() {
		return isVisible(userLnk) && isEnabled(userLnk);
	}

	public boolean isSidebarSearchTextEnable() {
		return isVisible(sidebarSearchText) && isEnabled(sidebarSearchText);
	}

	public void collapsingSideBar() {
		collapseButton.click();
	}

	public void expandingSideBar() {
		driver.findElement(By.cssSelector("mat-icon[role=img][mattooltip=Expand]")).click();
	}
}
