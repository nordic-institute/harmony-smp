package pages.service_groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class FilterArea extends PageComponent {
	public GenericSelect domainSelect;
	@FindBy(id = "participantIdentifier")
	private WebElement participantIdentifierInput;

	@FindBy(id = "participantScheme")
	private WebElement participantSchemeInput;

	@FindBy(id = "domain_id")
	private WebElement domainSelectContainer;
	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "searchbutton_id")
	private WebElement searchButton;

	public FilterArea(WebDriver driver) {
		super(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		domainSelect = new GenericSelect(driver, domainSelectContainer);

	}

	public String getParticipantIdentifierInputValue() {
		log.info("getting text in participant Identifier Input");
		waitForElementToBeVisible(participantIdentifierInput);
		return participantIdentifierInput.getText().trim();
	}

	public String getParticipantSchemeInputValue() {
		log.info("getting text in participant Scheme Input");
		waitForElementToBeVisible(participantSchemeInput);
		return participantSchemeInput.getText().trim();
	}

	public boolean isLoaded() {
		log.info("checking filter area is properly loaded");
		if (!isVisible(participantIdentifierInput)) {
			return false;
		}
		if (!isVisible(participantSchemeInput)) {
			return false;
		}
		return domainSelect.isLoaded();
	}

	public void filter(String identifier, String scheme, String domain) {
		log.info(String.format("filtering by %s, %s, %s", identifier, scheme, domain));
		clearAndFillInput(participantIdentifierInput, identifier);
		clearAndFillInput(participantSchemeInput, scheme);

		if (null != domain && !domain.isEmpty()) {
			domainSelect.selectOptionByText(domain);
		}

		log.info("clicking search");
		waitForElementToBeClickable(searchButton).click();
		waitForXMillis(1000);
	}

	public boolean isSearchButtonVisible() {
		try {
			return searchButton.isDisplayed();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isSearchButtonEnable() {
		try {
			return searchButton.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
