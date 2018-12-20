package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.Generator;
import utils.PROPERTIES;

public class SGExtensionWizzard extends PageComponent {
	public SGExtensionWizzard(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@FindBy(css = "#ExtensionID_id")
	private WebElement extensionIDInput;

	@FindBy(css = "#ExtensionName_id")
	private WebElement extensionNameInput;

	@FindBy(css = "#ExtensionAgencyID_id")
	private WebElement extensionAgencyIDInput;

	@FindBy(css = "#ExtensionAgencyName_id")
	private WebElement extensionAgencyNameInput;

	@FindBy(css = "#ExtensionAgencyURI_id")
	private WebElement extensionAgencyURIInput;

	@FindBy(css = "#ExtensionVersionID_id")
	private WebElement extensionVersionIDInput;

	@FindBy(css = "#ExtensionURI_id")
	private WebElement eExtensionURIInput;

	@FindBy(css = "#ExtensionReasonCode_id")
	private WebElement extensionReasonCodeInput;

	@FindBy(css = "#ExtensionReason_id")
	private WebElement extensionReasonInput;

	@FindBy(css = "service-group-extension-wizard button:nth-of-type(1)")
	private WebElement okButton;

	public void fillWithRndStrings(){
		clearAndFillInput(extensionIDInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionNameInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionAgencyIDInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionAgencyNameInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionAgencyURIInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionVersionIDInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(eExtensionURIInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionReasonCodeInput, Generator.randomAlphaNumeric(10));
		clearAndFillInput(extensionReasonInput, Generator.randomAlphaNumeric(10));
		waitForElementToBeClickable(okButton).click();
		waitForElementToBeGone(okButton);
	}



}
