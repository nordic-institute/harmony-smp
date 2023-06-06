package ddsl.dobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

import java.time.Duration;

public class DWait {
    public final WebDriverWait defaultWait;
    public final WebDriverWait longWait;
    public final WebDriverWait shortWait;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final TestRunData data = new TestRunData();
    private final WebDriver driver;

    public DWait(WebDriver driver) {
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

    public WebElement forElementToBeClickable(WebElement element) {
        return defaultWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement forElementToBeVisible(WebElement element) {
        return defaultWait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement forElementToBeVisible(WebElement element, boolean waitLonger) {
        if (waitLonger) {
            return longWait.until(ExpectedConditions.visibilityOf(element));
        }
        log.error(String.valueOf(element.isDisplayed()));
        return defaultWait.until(ExpectedConditions.visibilityOf(element));
    }

    public void forElementToBeEnabled(WebElement element) {
        int maxTimeout = data.getTIMEOUT() * 1000;
        int waitedSoFar = 0;

        while ((null != element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)) {
            waitedSoFar += 300;
            forXMillis(300);
        }
    }

    public void forElementToBeDisabled(WebElement element) {
        int maxTimeout = data.getTIMEOUT() * 1000;
        int waitedSoFar = 0;

        while ((null == element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)) {
            waitedSoFar += 300;
            forXMillis(300);
        }
    }

    public void forAttributeNotEmpty(WebElement element, String attributeName) {
        defaultWait.until(ExpectedConditions.attributeToBeNotEmpty(element, attributeName));
    }

    public void forElementToBeGone(WebElement element) {
        defaultWait.until((ExpectedConditions.invisibilityOf(element)));

        forXMillis(500);
    }

    public void forInvisibilityofLocator(By locator) {
        defaultWait.until((ExpectedConditions.invisibilityOfElementLocated(locator)));
    }

    public void forElementToBeGone(By selector) {

        log.debug("Waiting for element to be gone: " + selector.toString());
        int c = 0;

        while (c < 50) {
            try {
                driver.findElement(selector);
            } catch (Exception e) {
                break;
            }
            forXMillis(100);
            c++;
        }

    }

    public void forElementToBe(WebElement element) {

        defaultWait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return element.getLocation() != null;
            }
        });

    }

    public void forAttributeToContain(WebElement element, String attributeName, String value) {
        defaultWait.until(ExpectedConditions.attributeContains(element, attributeName, value));
    }

    public void forAttributeToNOTContain(WebElement element, String attributeName, String value) {
        defaultWait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return !element.getAttribute(attributeName).contains(value);
            }
        });
    }

    public void forAttributeToContain(WebElement element, String attributeName, String value, boolean waitLonger) {

        WebDriverWait myWait = defaultWait;
        if (waitLonger) {
            myWait = longWait;
        }
        myWait.until(ExpectedConditions.attributeContains(element, attributeName, value));
    }

    public void forElementToHaveText(WebElement element) {
        defaultWait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return !element.getText().trim().isEmpty();
            }
        });
    }

    public void forElementToContainText(WebElement element, String text) {
        defaultWait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.textToBePresentInElement(element, text));

    }
}
