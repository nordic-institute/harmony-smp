package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

import java.util.ArrayList;
import java.util.List;

public class AcordionSection extends PageComponent {
	public AcordionSection(WebDriver driver, WebElement container) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(container, PROPERTIES.TIMEOUT) , this);
	}

	@FindBy(css = "span.mat-content > mat-panel-title")
	WebElement title;

	@FindBy(css = "span.mat-content > mat-panel-description > div")
	WebElement selectCount;


	@FindBy(css = ".mat-expansion-indicator")
	WebElement expandButton;

	@FindBy(tagName = "mat-list-option")
	List<WebElement> options;

	public boolean isExpanded(){
		return options.get(0).isDisplayed();
	}

	public String getTitle(){
		return title.getText().replaceAll("\\W", "").trim();
	}
	public String getSelectedCountFullText(){
		return selectCount.getText().trim();
	}
	public Integer getSelectedCount(){
		String fullText = getSelectedCountFullText();
		return Integer.valueOf(fullText.replaceAll("\\D", ""));
	}
	public void expandSection(){
		waitForElementToBeClickable(expandButton).click();
		waitForElementToBeVisible(options.get(0));
	}

	public void selectOptionWithText(String text){
		for (WebElement option : options) {

			if(option.getAttribute("aria-selected").contains("true")){continue;}

			if(option.getText().trim().equalsIgnoreCase(text)){
				option.click();
				return;
			}
		}
	}

	public void selectOptionWithIndex(Integer index){
		if(index>=options.size()){return;}

		WebElement option = options.get(index);
		if(option.getAttribute("aria-selected").contains("true")){return;}

		option.click();
		return;
	}

	public boolean optionsEnabled(){
		waitForElementToBeVisible(title);
		boolean isDisabled = options.get(0).getAttribute("aria-disabled").equalsIgnoreCase("true");
		return !isDisabled;
	}

	public List<String> getOptions(){
		List<String> optionStr = new ArrayList<>();
		for (WebElement option : options) {
			optionStr.add(option.getText().trim());
		}
		return optionStr;
	}





}
