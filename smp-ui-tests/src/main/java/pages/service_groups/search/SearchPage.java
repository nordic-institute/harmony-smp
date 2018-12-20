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

		serviceGroupGrid = new ServiceGroupGrid(driver, searchGridContainer);

	}

	public FilterArea filters = new FilterArea(driver);

	@FindBy(id = "searchTable")
	WebElement searchGridContainer;
	public ServiceGroupGrid serviceGroupGrid;


	public boolean isLoaded() {
		if(!filters.isLoaded()){ return false;}
		return serviceGroupGrid.isLoaded();
	}
}
