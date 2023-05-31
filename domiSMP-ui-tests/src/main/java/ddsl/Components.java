package ddsl;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.objects.Wait;
import utils.TestRunData;

public class Components {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public Wait wait;
    protected WebDriver driver;
    protected TestRunData data = new TestRunData();

    public Components(WebDriver driver) {
        this.driver = driver;
        this.wait = new Wait(driver);
    }

    public void waitForRowsToLoad() {

        try {
            wait.forXMillis(100);
            int bars = 1;
            int waits = 0;
            while (bars > 0 && waits < 30) {
                Object tmp = ((JavascriptExecutor) driver).executeScript("return document.querySelectorAll('datatable-progress').length;");
                bars = Integer.parseInt(tmp.toString());
                waits++;
                wait.forXMillis(100);
            }
            log.debug("waited for rows to load for ms = " + waits * 100);
            wait.forXMillis(100);
        } catch (Exception e) {
        }

    }
}
