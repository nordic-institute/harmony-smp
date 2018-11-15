package pages.domain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.components.grid.BasicGrid;

import java.util.ArrayList;
import java.util.List;

public class DomainGrid extends BasicGrid {
	public DomainGrid(WebDriver driver, WebElement container) {
		super(driver, container);
	}
	
	private By cellSelector = By.tagName("datatable-body-cell");
	
	public List<DomainRow> getRowsInfo(){
		List<DomainRow> rowInfos = new ArrayList<>();
		
		for (WebElement gridRow : gridRows) {
			List<WebElement> cells = gridRow.findElements(cellSelector);
			
			DomainRow row = new DomainRow();
			row.setDomainCode(cells.get(0).getText().trim());
			row.setSmlDomain(cells.get(1).getText().trim());
			row.setSmlSmpID(cells.get(2).getText().trim());
			row.setClientCertHeader(cells.get(3).getText().trim());
			row.setClientCertAlias(cells.get(4).getText().trim());
			row.setClientCertAlias(cells.get(5).getText().trim());
			row.setSignatureCertAlias(cells.get(6).getText().trim());
			
			rowInfos.add(row);
		}
		
		return rowInfos;
	}
	
}
