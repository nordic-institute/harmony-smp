package pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.baseComponents.PageComponent;

import java.util.ArrayList;
import java.util.List;

public class GenericSelect extends PageComponent {

	public GenericSelect(WebDriver driver, WebElement container) {
		super(driver);

		log.info("select init");
		this.container = container;
		
		PageFactory.initElements(new DefaultElementLocatorFactory(container), this);
	}

	private WebElement container;

	@FindBy(className = "mat-select-arrow")
	WebElement expandoButton;

	@FindBy(css = "div.mat-select-value span")
	WebElement currentValueElement;

	private By optionSelector = By.tagName("mat-option");


	private void expandSelect(){
		log.info("expand select");
		waitForElementToBeClickable(expandoButton).click();
	}

	private List<WebElement> getOptions(){
		expandSelect();
		log.info("getting options");
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionSelector));
	}

	public List<String> getOptionTexts(){
		log.info("get displayed option texts");
		List<WebElement> options = getOptions();
		List<String> optionTexts = new ArrayList<>();

		for (WebElement option : options) {
			optionTexts.add(option.getText().trim());
		}
		return optionTexts;
	}

	public boolean selectOptionWithText(String text){
		log.info("selecting option with text" + text);
		List<WebElement> options = getOptions();


		for (WebElement option : options) {
			if(option.getText().trim().equalsIgnoreCase(text)){
				waitForElementToBeClickable(option).click();
				waitForElementToBeGone(option);
				log.info("return type is True");
				return true;
			}
		}
		log.info(text + " option not found, could not select it");
		return false;
	}

	public boolean selectFirstOption(){
		log.info("selecting first option");
		List<WebElement> options = getOptions();

		WebElement option = options.get(1);
		waitForElementToBeClickable(option).click();
		waitForElementToBeGone(option);
		return true;
	}
	public boolean selectWithIndex(int index){
		log.info("selecting the required option");
		List<WebElement> options = getOptions();

		WebElement option = options.get(index);
		waitForElementToBeClickable(option).click();
		waitForElementToBeGone(option);
		return true;
	}



	public String getSelectedValue() {
		log.info("getting current selected value");
		return currentValueElement.getText().trim();
	}

	public boolean selectOptionByText(String text) {
		log.info("(2) selecting option with text" + text);

		expandoButton.click();

		List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionSelector));
		for (WebElement option : options) {
			String optionDomain = option.getText().trim();
			if(optionDomain.equalsIgnoreCase(text)){
				option.click();
				waitForElementToBeGone(option);
				return true;
			}
		}
		log.info(text + " option not found, could not select it (2)" );
		return false;
	}

	public boolean isLoaded() {
		log.info("assert loaded state");
		waitForElementToBeVisible(expandoButton);
		if(!expandoButton.isDisplayed()){ return false;}
		return currentValueElement.isDisplayed();
	}
}
