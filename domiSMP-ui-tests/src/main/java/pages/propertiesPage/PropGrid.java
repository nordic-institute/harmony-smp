package pages.propertiesPage;

import ddsl.dcomponents.Grid.BasicGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PropGrid extends BasicGrid {

    /**
     * This class is used to map Property grid component.
     */

    public PropGrid(WebDriver driver, WebElement container) {
        super(driver, container);
    }

    public PropertyPopup selectValue(String propertyValue) {
        this.doubleClickRow(propertyValue);
        return new PropertyPopup(driver);
    }

    public String getPropertyValue(String propertyName) {
        return getValue(propertyName);
    }
}
