package pages.systemSettings.propertiesPage;

import ddsl.dcomponents.Grid.BasicGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
/**
 * This class is used to map Property grid component.
 */
public class PropGrid extends BasicGrid {
    public PropGrid(WebDriver driver, WebElement container) {
        super(driver, container);
    }

    public PropertyPopup doubleClickValue(String propertyValue) {
        this.doubleClickRow(propertyValue);
        return new PropertyPopup(driver);
    }

    public String getPropertyValue(String propertyName) {
        return getValue(propertyName);
    }
}
