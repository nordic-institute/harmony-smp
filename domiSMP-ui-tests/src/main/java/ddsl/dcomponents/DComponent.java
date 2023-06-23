package ddsl.dcomponents;

import ddsl.dobjects.DButton;
import ddsl.dobjects.DInput;
import ddsl.dobjects.DSelect;
import ddsl.dobjects.DWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

public class DComponent {

    private final static Logger LOG = LoggerFactory.getLogger(DComponent.class);
    public DWait wait;
    protected WebDriver driver;
    protected TestRunData data = new TestRunData();

    public DComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new DWait(driver);
    }

    protected DButton weToDButton(WebElement element) {
        return new DButton(driver, element);
    }

    protected DInput weToDInput(WebElement element) {
        return new DInput(driver, element);
    }

    protected DSelect weToDSelect(WebElement element) {
        return new DSelect(driver, element);
    }
}
