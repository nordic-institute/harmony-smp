package pages.components.grid;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.baseComponents.PageComponent;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BasicGrid extends PageComponent {

	public BasicGrid(WebDriver driver, WebElement container) {
		super(driver);
		
		log.info("Loading basic grid");
		waitToLoad();
		waitForRowsToLoad();
		PageFactory.initElements( new DefaultElementLocatorFactory(container) , this);

		for (int i = 0; i < gridHeaders.size(); i++) {
			headerTxt.add(gridHeaders.get(i).getText().trim());
		}

	}

	private By loadingBar = By.className("mat-ripple-element");

	private void waitToLoad(){
		try {
			waitForXMillis(500);
			waitForElementToBeGone(driver.findElement(loadingBar));
		} catch (Exception e) {

		}
	}

	public void waitForRowsToLoad() {
		try {
			waitForElementToBeVisible(loadingBar);

			int bars = 1;
			int waits = 0;
			while (bars > 0 && waits < 30) {
				Object tmp = ((JavascriptExecutor) driver).executeScript("return document.querySelectorAll('.mat-ripple-element').length;");
				bars = Integer.valueOf(tmp.toString());
				waits++;
				waitForXMillis(500);
			}
			log.debug("waited for rows to load for ms = 500*" + waits);
		} catch (Exception e) {	}
		waitForXMillis(500);
	}

	@FindBy(css = "datatable-header div.datatable-row-center datatable-header-cell")
	protected List<WebElement> gridHeaders;

	@FindBy(css = "datatable-body-row > div.datatable-row-center.datatable-row-group")
	protected List<WebElement> gridRows;
	
	
	protected ArrayList<String> headerTxt = new ArrayList<String>();
	
	public void selectRow(int rowNumber){
		log.info("selecting row with number ... " + rowNumber);
		if(rowNumber>=gridRows.size()){return;}
		gridRows.get(rowNumber).click();
	}
	
	public void doubleClickRow(int rowNumber){
		
		log.info("double clicking row ... " + rowNumber);
		waitForXMillis(500);
		if(rowNumber>=gridRows.size()){return ;}
		Actions action = new Actions(driver);
		action.doubleClick(gridRows.get(rowNumber)).perform();
	}

	public int getColumnsNo(){
		log.info("getting number of columns");
		return gridHeaders.size();
	}

	public int getRowsNo(){
		return gridRows.size();
	}
	

	
	
	
}
