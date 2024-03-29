package pages.service_groups.search;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.ServiceGroupGrid;
import pages.service_groups.FilterArea;
import utils.PROPERTIES;

public class SearchPage extends SMPPage {

	public SearchPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		this.pageHeader.waitForTitleToBe("Search");

	}

	public FilterArea filters = new FilterArea(driver);

	@FindBy(id = "searchTable")
	WebElement searchGridContainer;


	public boolean isLoaded() {
		log.info("checking if search page is loaded");
		if(!filters.isLoaded()){ return false;}
		return getServiceGroupGrid().isLoaded();
	}

	public ServiceGroupGrid
		getServiceGroupGrid() {
		return new ServiceGroupGrid(driver, searchGridContainer);
	}
}
