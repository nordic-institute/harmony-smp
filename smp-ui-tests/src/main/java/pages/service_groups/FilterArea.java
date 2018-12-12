package pages.service_groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class FilterArea extends PageComponent {
	public FilterArea(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		domainSelect = new GenericSelect(driver, domainSelectContainer);

	}


	@FindBy(id = "participantIdentifier")
	private WebElement participantIdentifierInput;

	@FindBy(id = "participantScheme")
	private WebElement participantSchemeInput;

	@FindBy(id = "domain_id")
	private WebElement domainSelectContainer;
	public GenericSelect domainSelect;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "searchbutton_id")
	private WebElement searchButton;



	public String getParticipantIdentifierInputValue() {
		return participantIdentifierInput.getText().trim();
	}

	public String getParticipantSchemeInputValue() {
		return participantSchemeInput.getText().trim();
	}

	public boolean isLoaded(){
		if(!participantIdentifierInput.isDisplayed()){
			return false;
		}
		if(!participantSchemeInput.isDisplayed()){
			return false;
		}
		return domainSelect.isLoaded();
	}

	public void filter(String identifier, String scheme, String domain){
		clearAndFillInput(participantIdentifierInput, identifier);
		clearAndFillInput(participantSchemeInput, scheme);

		if(null != domain && !domain.isEmpty()){
			domainSelect.selectOptionByText(domain);
		}

		searchButton.click();
//		TODO - wait for loading bar to disappear
		waitForXMillis(1000);

	}


}
