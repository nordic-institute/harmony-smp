package pages.service_groups;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import pages.components.baseComponents.PageComponent;

import java.util.List;

public class ServiceGroupRow extends PageComponent {

	public ServiceGroupRow(WebDriver driver, WebElement container) {
		super(driver);
		PageFactory.initElements( new DefaultElementLocatorFactory(container) , this);
	}




	@FindBy(tagName = "datatable-body-cell")
	protected List<WebElement> cells;
    @FindBy(css = "div.datatable-row-detail.ng-star-inserted > div.ng-star-inserted")
	private WebElement emptyMetadataContent;

	@FindBy(css = ".ng-star-inserted.datatable-icon-right")
	private WebElement expandMetadata;

	public MetadataGrid expandMetadata() {
			expandMetadata.click();
//		todo: find something better to wait for
			waitForXMillis(1000);
		return new MetadataGrid(driver);
	}
	public void collapseMetadata(){
			driver.findElement(By.cssSelector(".ng-star-inserted.datatable-icon-down")).click();
	}

	public Integer getMetadataSize() {
		return Integer.valueOf(cells.get(2).getText().trim());
	}

	public String getParticipantScheme() {
		return cells.get(3).getText().trim();
	}

	public String getParticipantIdentifier() {
		return cells.get(4).getText().trim();
	}

	public String getServiceGroupURL() {
		return cells.get(5).findElement(By.tagName("a")).getAttribute("href").trim();
	}

	public boolean verifyMetadataExpandButton() {
		return expandMetadata.isEnabled();
	}

	public boolean verifyMetadataCollapseButton() {
		WebElement collapseButton = driver.findElement(By.cssSelector(".ng-star-inserted.datatable-icon-down"));
		return collapseButton.isDisplayed() && collapseButton.isEnabled();
	}

	public boolean isMetadataExpanded() {
		try{
			if(	new MetadataGrid(driver).getMetadataRows().size() >0 ){
				return true;
			}
		}catch (Exception e){ e.printStackTrace();}
		return false;
	}

	public String emptyMetadataContentText()
	{
		return emptyMetadataContent.getText();
	}


}
