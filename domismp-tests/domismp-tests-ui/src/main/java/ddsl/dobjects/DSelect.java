package ddsl.dobjects;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic wrapper for select element.
 */
public class DSelect extends DObject {
    private final static Logger LOG = LoggerFactory.getLogger(DSelect.class);
    Select select = new Select(element);
    public DSelect(WebDriver driver, WebElement element) {
        super(driver, element);
    }
    public void selectByVisibleText(String value) {
        select.selectByVisibleText(value);
    }

    public void selectByVisibleText(String value, boolean forceSelection) {
        if (forceSelection) {
            select.getWrappedElement().sendKeys(Keys.ENTER);
        }
        wait.forXMillis(10);
        LOG.debug("Selecting by visible text: [{}]", value);
        select.getAllSelectedOptions().stream().forEach(e ->  LOG.debug("value to select", e.getText()));
        select.selectByVisibleText(value);
    }

    public void selectValue(String value) {
        select.selectByValue(value);
    }
    public String getCurrentValue() {
        try {
            return select.getAllSelectedOptions().get(0).getText();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
