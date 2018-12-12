package pages.service_groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.baseComponents.PageComponent;

import java.util.ArrayList;
import java.util.List;

public class MetadataGrid  extends PageComponent{
	public MetadataGrid(WebDriver driver) {
		super(driver);
		PageFactory.initElements( driver, this);
	}


	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(css = "ngx-datatable.inner-table.virtualized datatable-row-wrapper")
	List<WebElement> rowContainers;

	public List<MetadataRow> getMetadataRows(){
		List<MetadataRow> rowList = new ArrayList<>();
		for (WebElement rowContainer : rowContainers) {
			rowList.add(new MetadataRow(driver, rowContainer));
		}
		return rowList;
	}

}
