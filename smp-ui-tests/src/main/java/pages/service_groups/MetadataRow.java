package pages.service_groups;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import pages.components.baseComponents.PageComponent;

import java.util.List;

public class MetadataRow extends PageComponent {
	public MetadataRow(WebDriver driver, WebElement container) {
			super(driver);
			PageFactory.initElements( new DefaultElementLocatorFactory(container) , this);
	}

	@FindBy(tagName = "datatable-body-cell")
	protected List<WebElement> cells;

	public String getDomain(){
		return cells.get(0).getText().trim();
	}

	public String getDocumentIdentifierScheme(){
		return cells.get(1).getText().trim();
	}

	public String getDocumentIdentifier(){
		return cells.get(2).getText().trim();
	}

	public String getURL(){
		return cells.get(3).findElement(By.tagName("a")).getAttribute("href").trim();
	}

	public void clickURL(){
		cells.get(3).findElement(By.tagName("a")).click();
	}

}
