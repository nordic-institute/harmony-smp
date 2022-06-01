package pages.domain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

	public void mouseHoverOnDomainCode(int rowNumber) {
		WebElement element = driver.findElement(By.cssSelector(".datatable-row-wrapper:nth-child(" + rowNumber + ") .datatable-body-cell:nth-child(1) .datatable-body-cell-label span"));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}

	public boolean isDomainStillPresent(String domainCode) {
		boolean end = false;
		List<DomainRow> rows = new ArrayList<>();
		DomainPage page = new DomainPage(driver);
		page.pagination.skipToFirstPage();

		while (!end) {
			rows.addAll(page.grid().getRowsInfo());
			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		boolean found = false;
		for (DomainRow row : rows) {
			if (row.getDomainCode().equalsIgnoreCase(domainCode)) {
				found = true;
			}
		}
		return found;
	}

	public int scrollToDomain(String domainCode) {
		DomainPage page = new DomainPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			List<DomainRow> rows = page.grid().getRowsInfo();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getDomainCode().equalsIgnoreCase(domainCode)) {
					return i;
				}
			}

			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		return -1;
	}

	public int scrollToSmlDomain(String smlDomain) {
		try {
			DomainPage page = new DomainPage(driver);
			List<DomainRow> rows;
			int count = 0;
			do {
				if (count != 0) {
					page.pagination.goToNextPage();
				}
				rows = page.grid().getRowsInfo();
				for (int i = 0; i < rows.size(); i++) {
					if (rows.get(i).getSmlDomain().equalsIgnoreCase(smlDomain)) {
						return i;
					}
				}
				count++;
			}
			while (page.pagination.hasNextPage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
