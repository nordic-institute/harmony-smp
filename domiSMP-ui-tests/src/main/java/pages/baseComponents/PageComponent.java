package pages.baseComponents;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.objects.Wait;
import utils.TestRunData;

public class PageComponent {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public Wait wait;
    protected WebDriver driver;
    protected TestRunData data = new TestRunData();

    public PageComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new Wait(driver);
    }
}
