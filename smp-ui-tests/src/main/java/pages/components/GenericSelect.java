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
		this.container = container;
		
		PageFactory.initElements(new DefaultElementLocatorFactory(container), this);
	}

	private WebElement container;

	@FindBy(className = "mat-select-arrow")
	WebElement expandoButton;

	@FindBy(css = "div.mat-select-value span")
	WebElement curentValueElement;

	private By optionSelector = By.tagName("mat-option");


	private void expandSelect(){
		waitForElementToBeClickable(expandoButton).click();
	}

	private List<WebElement> getOptions(){
		expandSelect();
		return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionSelector));
	}

	public List<String> getOptionTexts(){
		List<WebElement> options = getOptions();
		List<String> optionTexts = new ArrayList<>();

		for (WebElement option : options) {
			optionTexts.add(option.getText().trim());
		}
		return optionTexts;
	}

	public boolean selectOptionWithText(String text){
		List<WebElement> options = getOptions();


		for (WebElement option : options) {
			if(option.getText().trim().equalsIgnoreCase(text)){
				waitForElementToBeClickable(option).click();
				waitForElementToBeGone(option);
				return true;
			}
		}
		return false;
	}


	public String getSelectedValue() {
		return curentValueElement.getText().trim();
	}

	public boolean selectOptionByText(String text) {
		expandoButton.click();

		List<WebElement> options = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionSelector));
		for (WebElement option : options) {
			String optionDomain = option.getText().trim();
			if(optionDomain.equalsIgnoreCase(text)){
				option.click();
				waitForElementToBeGone(option);
				return true;
			}
		}

		return false;
	}

	public boolean isLoaded() {
		if(!expandoButton.isDisplayed()){ return false;}
		if(!curentValueElement.isDisplayed()){ return false;}
		return true;
	}
}
