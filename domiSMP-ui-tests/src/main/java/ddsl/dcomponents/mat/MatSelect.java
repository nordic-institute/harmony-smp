package ddsl.dcomponents.mat;

import ddsl.dobjects.DObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;


public class MatSelect extends DObject {

    public MatSelect(WebDriver driver, WebElement element) {
        super(driver, element);
        String tagName = element.getTagName();
        if (null != tagName && StringUtils.equalsIgnoreCase("mat-select", tagName)) {
            this.element = element;
        } else {
            throw new UnexpectedTagNameException("mat-select", tagName);
        }
    }

    public void selectByVisibleText(String value) {
        element.click();
        WebElement option = element.findElement(By.xpath("//mat-option/span[contains(text(),'" + value + "')]"));
        wait.forElementToBeVisible(option);
        option.click();
    }

    public void selectByValue(String value) {
        element.click();
        WebElement option = element.findElement(By.xpath("//mat-option/span[contains(text(),'" + value + "')]"));
        wait.forElementToBeVisible(option);
        option.click();
    }

    /**
     * Method returns the current selected text!
     *
     * @return the text displayed in the select
     */
    public String getCurrentText() {
        return element.getText();

    }
}
