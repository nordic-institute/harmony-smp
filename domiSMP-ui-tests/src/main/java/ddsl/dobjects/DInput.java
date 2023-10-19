package ddsl.dobjects;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
/**
 * Generic wrapper for input element.
 */
public class DInput extends DObject {
    public DInput(WebDriver driver, WebElement element) {
        super(driver, element);
    }

    public void fill(String value){
        if (null == value) {
            return;
        }
        if (isEnabled()) {
            element.clear();
            element.sendKeys(value);
        } else {
            throw new ElementNotInteractableException("Cannot type disabled field");
        }
    }

    /**
     * Method to send values which are bigger which can't be handled by Angular issue
     */
    public void fill(String value, Boolean slowSendValues){
        if (null == value) {
            return;
        }
        if (isEnabled()) {
            element.clear();

            if (slowSendValues) {
                for (int i = 0; i < value.length(); i++) {
                    char c = value.charAt(i);
                    String s = String.valueOf(c);
                    element.sendKeys(s);
                }
            } else {
                element.sendKeys(value);
            }
        } else {
            throw new ElementNotInteractableException("Cannot type disabled field");
        }
    }

    public void clear() throws Exception {
        if (isEnabled()) {
            element.clear();
        } else {
            throw new Exception("Cannot type disabled field");
        }
    }

    @Override
    public String getText() {
        if (isPresent()) {
            return element.getAttribute("value").trim();
        }
        return null;
    }


}
