package pages.service_groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ServiceGroupGrid extends PageComponent {

	public ServiceGroupGrid(WebDriver driver, WebElement container) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(container, PROPERTIES.TIMEOUT) , this);
	}

	@FindBy(className = "datatable-header-cell-label")
	List<WebElement> headers;

	@FindBy(className = "datatable-row-wrapper")
	List<WebElement> rowWrappers;

	public List<ServiceGroupRow> getRows() {
		List<ServiceGroupRow> rows = new ArrayList<>();

		for (WebElement rowWrapper : rowWrappers) {
			rows.add(new ServiceGroupRow(driver, rowWrapper));
		}

		return rows;
	}

	public boolean isLoaded(){
		if(headers.size()>0){
			return true;
		}
		return false;
	}

	public void doubleClickRow(int rowNumber) {
		log.info("double clicking row ... " + rowNumber);
		waitForXMillis(500);
		if(rowNumber>=rowWrappers.size()){return ;}
		waitForElementToBeClickable(rowWrappers.get(rowNumber));
		Actions action = new Actions(driver);
		action.doubleClick(rowWrappers.get(rowNumber)).perform();
	}

	public void selectRow(int rowNumber) {
		log.info("clicking row ... " + rowNumber);
		waitForXMillis(500);
		if(rowNumber>=rowWrappers.size()){return ;}
		rowWrappers.get(rowNumber).click();
	}

	public List<String> getHeaders(){
		List<String> stHeaders = new ArrayList<>();
		for (WebElement header : headers) {
			stHeaders.add(header.getText().trim());
		}
		return stHeaders;
	}

	public int getRowsNo(){
		return rowWrappers.size();
	}


	public <T extends ServiceGroupRow> ArrayList<T> getRowsAs(Class<T> expectedType){
		log.info("getting rows!!!");
		ArrayList<T> toReturn = new ArrayList<T>();

		for (int i = 0; i < rowWrappers.size(); i++) {
			Constructor<T> constructor = null;
			T obj = null;
			try {
				constructor = expectedType.getDeclaredConstructor(WebDriver.class, WebElement.class);
				obj = constructor.newInstance(driver, rowWrappers.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			toReturn.add(obj);
		}
		return toReturn;
	}



}
