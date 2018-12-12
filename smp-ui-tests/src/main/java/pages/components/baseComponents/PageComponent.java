package pages.components.baseComponents;


import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
	
	public void waitForElementToBeEnabled(WebElement element) {
		int maxTimeout = PROPERTIES.TIMEOUT * 1000;
		int waitedSoFar = 0;
		while ((null != element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)){
			waitedSoFar += 300;
			waitForXMillis(300);
		}
	}

	public void waitForElementToBeGone(WebElement element) {
		try {
			webDriverWait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)));
		} catch (Exception e) {	}
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
		WebElement topLogoText = driver.findElement(By.id("topLogo"));
		By overlayLocator = By.cssSelector("[class*=\"overlay-backdrop\"]");
		Point logoPoint = topLogoText.getLocation();
		int x = logoPoint.x;
		int y = logoPoint.y;

		try {
			webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(overlayLocator));
			new Actions(driver).moveByOffset(x, y).click().build().perform();
		} catch (Exception e) {

		}
		try {
			int c=0;
			while(c<30){
				if(null != driver.findElement(overlayLocator)){
					new Actions(driver).moveByOffset(x, y).click().build().perform();
					waitForXMillis(500);
				}
				c++;
			}

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

