package pages.components.baseComponents;


import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PROPERTIES;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;


public class PageComponent {

	protected WebDriver driver;
	protected WebDriverWait webDriverWait;
	protected Logger log = Logger.getLogger(this.getClass());


	public PageComponent(WebDriver driver) {
		this.driver = driver;
		this.webDriverWait = new WebDriverWait(this.driver, PROPERTIES.TIMEOUT);
	}

	public void waitForXMillis(Integer millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}

	public WebElement waitForElementToBeClickable(WebElement element) {
		return webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public WebElement waitForElementToBeVisible(WebElement element) {
		return webDriverWait.until(ExpectedConditions.visibilityOf(element));
	}

	public WebElement waitForElementToBeVisible(By elementSelector) {
		return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(elementSelector));
	}

	public void waitForElementToBeEnabled(WebElement element) {
		int maxTimeout = PROPERTIES.TIMEOUT * 1000;
		int waitedSoFar = 0;
		while ((null != element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)){
			waitedSoFar += 300;
			waitForXMillis(300);
		}
	}

//	public void waitForElementToBeGone(WebElement element) {
//		try {
//			webDriverWait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)));
//		} catch (Exception e) {	}
//	}

	public void waitForElementToBeGone(WebElement element) {
		WebDriverWait myWait = new WebDriverWait(driver, PROPERTIES.SHORT_UI_TIMEOUT);

		try {
			myWait.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {	return;}

		int waitTime = PROPERTIES.SHORT_UI_TIMEOUT * 1000;
		while (waitTime >0){
			boolean displayed = true;

			try {
				displayed = element.isDisplayed();
			} catch (Exception e) {
				return;
			}

			if(!displayed){
				return;
			}
			waitForXMillis(500);
			waitTime = waitTime - 500;
		}
	}

	public void waitForNumberOfWindowsToBe(int noOfWindows) {
		try {
			webDriverWait.until(numberOfWindowsToBe(noOfWindows));
		} catch (Exception e) {	}
	}

	public void clearAndFillInput(WebElement element, String toFill) {

		waitForElementToBeVisible(element).clear();
		element.sendKeys(toFill);
	}

	public void clickVoidSpace(){
		try {
			waitForXMillis(500);
			((JavascriptExecutor)driver).executeScript("document.querySelector('[class*=\"overlay-backdrop\"]').click()");
			waitForXMillis(500);
		} catch (Exception e) {	}
		waitForXMillis(500);
	}


	private ExpectedCondition<Boolean> numberOfWindowsToBe(final int numberOfWindows) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				driver.getWindowHandles();
				return driver.getWindowHandles().size() == numberOfWindows;
			}
		};}


}

