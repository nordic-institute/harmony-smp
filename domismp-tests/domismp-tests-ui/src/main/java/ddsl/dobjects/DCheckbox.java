package ddsl.dobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

public class DCheckbox extends DObject {
    WebElement labelElement;
    WebElement input;

    public DCheckbox(WebDriver driver, WebElement element) {
        this(driver, element, null);
        input = element.findElement(By.cssSelector("input[type='checkbox']"));
    }

    public DCheckbox(WebDriver driver, WebElement element, WebElement labelElement) {
        super(driver, element);

        this.labelElement = labelElement;
    }

    public boolean isChecked() throws Exception {
        if (isPresent()) {
            if (Objects.requireNonNull(input.getDomAttribute("class")).contains("-checked")) {
                return true;
            }
            List<WebElement> input = element.findElements(By.cssSelector("input[type='checkbox']"));
            return Objects.requireNonNull(input.get(0).getDomAttribute("class")).contains("-selected");
        }
        throw new DObjectNotPresentException();
    }

    public void check() throws Exception {
        if (isChecked()) return;
        if (isEnabled()) {
            clickCheckbox();
            wait.forAttributeToContain(element, "class", "checkbox-checked");
            return;
        }
        throw new Exception("Checkbox is not enabled");
    }

    public void uncheck() throws Exception {
        if (!isChecked()) return;
        if (isEnabled()) {
            clickCheckbox();
            wait.forAttributeToNOTContain(this.element, "class", "checkbox-checked");
            return;
        }
        throw new Exception("Checkbox is not enabled");
    }

    private void clickCheckbox() {
        try {
            input.click();
        } catch (ElementNotInteractableException ex) {
            // in mat-checkbox the input is actually hidden, and the user has to click on the label to interact with it
            if (this.labelElement != null)
                this.labelElement.click();
        }
    }

    public boolean isEnabled() throws ElementNotInteractableException {
        try {
            if (isPresent()) {
                wait.forElementToBeEnabled(element);
                return !Objects.requireNonNull(element.getDomAttribute("class")).endsWith("disabled");
            }
        } catch (ElementNotInteractableException e) {
            throw new ElementNotInteractableException("Element not enabled: " + e);
        }

        return false;
    }

    public boolean isDisabled() throws Exception {
        if (isPresent()) {
            wait.forElementToBeDisabled(element);
            return Objects.requireNonNull(element.getDomAttribute("class")).endsWith("disabled");
        }
        throw new Exception();
    }

}
