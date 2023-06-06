package ddsl.dobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


public class DSelect extends DObject {

    Select select = new Select(element);

    public DSelect(WebDriver driver, WebElement element) {
        super(driver, element);
    }

    public void selectValue(String value) {
        select.selectByVisibleText(value);
    }

    public String getCurrentValue() {
        return element.getText();
    }


}
