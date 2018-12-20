package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.service_groups.ServiceGroupRow;

public class ServiceGroupRowE extends ServiceGroupRow {
	public ServiceGroupRowE(WebDriver driver, WebElement container) {
		super(driver, container);
	}

	@FindBy(css = "datatable-body-cell:nth-child(7) > div > div > button:nth-child(1)")
	WebElement addMetadataButton;

	@FindBy(css = "datatable-body-cell:nth-child(7) > div > div > button:nth-child(2)")
	WebElement editServiceGroup;

	@FindBy(css = "datatable-body-cell:nth-child(7) > div > div > button:nth-child(3)")
	WebElement deleteServiceGroup;

	@Override
	public Integer getMetadataSize() {
		return Integer.valueOf(cells.get(1).getText().trim());
	}

	public Integer getOwnerSize() {
		return Integer.valueOf(cells.get(2).getText().trim());
	}

	public ServiceMetadataPopup clickAddMetadata(){
		waitForElementToBeClickable(addMetadataButton).click();

		return new ServiceMetadataPopup(driver);
	}

	public ServiceGroupPopup clickEdit(){
		waitForElementToBeClickable(editServiceGroup).click();
		return new ServiceGroupPopup(driver);
	}

	public void clickDelete(){
		waitForElementToBeClickable(deleteServiceGroup).click();
	}



}
