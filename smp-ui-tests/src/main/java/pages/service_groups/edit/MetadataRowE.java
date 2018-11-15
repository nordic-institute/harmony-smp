package pages.service_groups.edit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import pages.service_groups.MetadataRow;

import java.util.List;

public class MetadataRowE extends MetadataRow {

	public MetadataRowE(WebDriver driver, WebElement container) {
		super(driver, container);
	}

	@FindBy(css = "datatable-body-cell:nth-child(5) > div > div > button:nth-child(1)")
	WebElement editMetadataButton;

	@FindBy(css = "datatable-body-cell:nth-child(5) > div > div > button:nth-child(2)")
	WebElement deleteMetadataButton;

	public void clickEdit(){
		waitForElementToBeClickable(editMetadataButton).click();
	}

	public void clickDelete(){
		waitForElementToBeClickable(deleteMetadataButton).click();
	}





}
