package pages.components.baseComponents;


import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PROPERTIES;


public class PageComponent {

	protected WebDriver driver;
	protected WebDriverWait wait;
	protected Logger log = Logger.getLogger(this.getClass());


	public PageComponent(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(this.driver, PROPERTIES.TIMEOUT);
	}


	public WebElement waitForElementToBeClickable(WebElement element) {
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public WebElement waitForElementToBeVisible(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	public WebElement waitForElementToBeVisible(By elementSelector) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(elementSelector));
	}

	public void waitForElementToBeEnabled(WebElement element) {
		int maxTimeout = PROPERTIES.SHORT_UI_TIMEOUT * 1000;
		int waitedSoFar = 0;
		while ((null != element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)) {
			waitedSoFar += 300;
			waitForXMillis(300);
		}
	}

	public void waitForElementToBeDisabled(WebElement element) {
		int maxTimeout = PROPERTIES.SHORT_UI_TIMEOUT * 1000;
		int waitedSoFar = 0;
		while ((null == element.getAttribute("disabled")) && (waitedSoFar < maxTimeout)) {
			waitedSoFar += 300;
			waitForXMillis(300);
		}
	}


	public void waitForElementToBeGone(WebElement element) {
		WebDriverWait myWait = new WebDriverWait(driver, PROPERTIES.SHORT_UI_TIMEOUT);

		try {
			myWait.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			return;
		}

		int waitTime = PROPERTIES.SHORT_UI_TIMEOUT * 1000;
		while (waitTime > 0) {
			boolean displayed = true;

			try {
				displayed = element.isDisplayed();
			} catch (Exception e) {
				return;
			}

			if (!displayed) {
				return;
			}
			waitForXMillis(500);
			waitTime = waitTime - 500;
		}
	}

	public void waitForNumberOfWindowsToBe(int noOfWindows) {
		try {
			wait.until(numberOfWindowsToBe(noOfWindows));
		} catch (Exception e) {
		}
	}

	public void clearAndFillInput(WebElement element, String toFill) {
		waitForElementToBeVisible(element).clear();
		log.info("cleared input");
		waitForElementToBeEnabled(element);
		element.sendKeys(toFill);
		log.info("filled in text " + toFill);
	}

	public void clickVoidSpace() {
		log.info("clicking void");
		try {
			waitForXMillis(500);
			((JavascriptExecutor) driver).executeScript("document.querySelector('[class*=\"overlay-backdrop\"]').click()");
			waitForXMillis(500);
		} catch (Exception e) {
		}
		waitForXMillis(500);
	}

	private ExpectedCondition<Boolean> numberOfWindowsToBe(final int numberOfWindows) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				driver.getWindowHandles();
				return driver.getWindowHandles().size() == numberOfWindows;
			}
		};
	}

	public void waitForXMillis(Integer millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("EXCEPTION: ", e);
		}
	}

	public void waitForAttributeNotEmpty(WebElement element, String attributeName) {
		wait.until(ExpectedConditions.attributeToBeNotEmpty(element, attributeName));
	}

	public void waitForElementToHaveText(WebElement element, String title) {
		wait.until(ExpectedConditions.textToBePresentInElement(element, title));
	}

	public void waitForElementToBe(WebElement element) {

		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return element.getLocation() != null;
			}
		});

	}

	public void waitForAttributeToContain(WebElement element, String attributeName, String value) {
		wait.until(ExpectedConditions.attributeContains(element, attributeName, value));
	}

	public void waitForElementToHaveText(WebElement element) {
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return !element.getText().trim().isEmpty();
			}
		});
	}

	public void waitForElementToContainText(WebElement element, String text) {
		wait.until(ExpectedConditions.textToBePresentInElement(element, text));
	}

	public boolean isVisible(WebElement element){
		log.info("checking if element is visible");

		try {
			waitForElementToBe(element);
			return element.isDisplayed();
		} catch (Exception e) {		}
		return false;
	}

	public boolean isEnabled(WebElement element){
		log.info("checking if element is enabled");
		try {
			waitForElementToBeEnabled(element);
		} catch (Exception e) {
			return false;
		}
		return element.isEnabled();
	}

	public boolean isDisabled(WebElement element){
		log.info("checking if element is disabled");
		try {
			waitForElementToBeDisabled(element);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}

