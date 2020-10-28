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
		log.info("getting all row info");
		List<DomainRow> rowInfos = new ArrayList<>();

		for (WebElement gridRow : gridRows) {
			List<WebElement> cells = gridRow.findElements(cellSelector);

			DomainRow row = new DomainRow();

			for (int i = 0; i < headerTxt.size(); i++) {
				switch (headerTxt.get(i)){
					case "Domain code":
						row.setDomainCode(cells.get(i).getText().trim());
						break;
					case "SML Domain":
						row.setSmlDomain(cells.get(i).getText().trim());
						break;
					case "Signature CertAlias":
						row.setSignatureCertAlias(cells.get(i).getText().trim());
						break;
					case "SML SMP Id":
						row.setSmlSmpID(cells.get(i).getText().trim());
						break;
				}

			}

			rowInfos.add(row);
		}

		return rowInfos;
	}


}
