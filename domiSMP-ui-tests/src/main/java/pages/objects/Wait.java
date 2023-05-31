package pages.objects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

import java.time.Duration;

public class Wait {
    public final WebDriverWait defaultWait;
    public final WebDriverWait longWait;
    public final WebDriverWait shortWait;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private TestRunData data = new TestRunData();
    private WebDriver driver;

    public Wait(WebDriver driver) {
        this.defaultWait = new WebDriverWait(driver, data.getTIMEOUTinDuration());
        this.longWait = new WebDriverWait(driver, data.getLongWaitInDuration());
        this.shortWait = new WebDriverWait(driver, Duration.ofMinutes(1));
        this.driver = driver;
    }

    public void forXMillis(Integer millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("EXCEPTION: ", e);
        }
    }
}
