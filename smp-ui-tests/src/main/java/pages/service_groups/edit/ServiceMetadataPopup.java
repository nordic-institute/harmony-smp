package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class ServiceMetadataPopup extends PageComponent {
	@FindBy(css = "mat-dialog-actions > button:nth-child(1)")
	private WebElement okButton;
	@FindBy(css = "mat-dialog-actions > button:nth-child(2)")
	private WebElement cancelButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(1)")
	private WebElement clearButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(2)")
	private WebElement generateXMLButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(4)")
	private WebElement validateButton;
	@FindBy(css = "#MetadataTextArea")
	private WebElement metadataTextArea;
	@FindBy(css = "#participanSchema_id")
	private WebElement participantSchemaInput;
	@FindBy(css = "#participantIdentifier_id")
	private WebElement participantIdentifierInput;
	@FindBy(css = "#documentScheme_id")
	private WebElement documentSchemeInput;
	@FindBy(css = "#documentIdentifier_id")
	private WebElement documentIdentifierInput;
	@FindBy(xpath = "//span[text() ='Metadata wizard']")
	private WebElement metadataWizardBtn;
	@FindBy(css = "mat-card-content > div > div.ng-star-inserted:nth-child(2)")
	private WebElement xmlValidationMsg;
	@FindBy(css = "mat-dialog-content #domain_id")
	private WebElement domainSelectContainer;
	private GenericSelect domainSelect;

	public ServiceMetadataPopup(WebDriver driver) {
		super(driver);

		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		waitForElementToBeVisible(domainSelectContainer);
		domainSelect = new GenericSelect(driver, domainSelectContainer);
	}

	public EditPage clickOK() {
		waitForElementToBeClickable(okButton);
		okButton.click();
		return new EditPage(driver);
	}

	public boolean isOKBtnEnabled() {
		return okButton.isEnabled();
	}

	public void fillDocIdAndDocIdScheme(String docID, String docScheme) {
		waitForElementToBeVisible(documentIdentifierInput);
		clearAndFillInput(documentIdentifierInput, docID);
		clearAndFillInput(documentSchemeInput, docScheme);

	}

	public String xmlFieldVALUE() {
		log.info("value is " + metadataTextArea.getAttribute("value"));
		return metadataTextArea.getAttribute("value");
	}

	public void fillForm(String domain, String docID, String docScheme) {
		waitForElementToBeVisible(documentIdentifierInput);
		domainSelect.selectWithIndex(0);

		clearAndFillInput(documentIdentifierInput, docID);
		clearAndFillInput(documentSchemeInput, docScheme);

		generateXMLButton.click();

	}

	public String captureTextOfMetadataTextArea() {
		return metadataTextArea.getText();
	}

	public void clickValidateBtn() {
		waitForElementToBeClickable(validateButton).click();
	}

	public void clickGenerateXMLBtn() {
		waitForElementToBeClickable(generateXMLButton).click();
	}

	public void clickClearBtn() {
		waitForElementToBeClickable(clearButton).click();
	}

	public String getXMLValidationMessage() {
		return xmlValidationMsg.getText();
	}

	public void addTextToMetadataTextArea(String generator) {
		metadataTextArea.sendKeys(generator);

	}

	public String docIDFieldValue() {
		return documentIdentifierInput.getAttribute("value");
	}

	public String docIDSchemeFieldValue() {
		return documentSchemeInput.getAttribute("value");
	}

	public String getParticipantSchemeValue() {
		waitForElementToBeVisible(participantSchemaInput);
		return participantSchemaInput.getAttribute("value").trim();
	}

	public String getParticipantIdentifierValue() {
		waitForElementToBeVisible(participantIdentifierInput);
		return participantIdentifierInput.getAttribute("value").trim();
	}

	public String getDocumentIdentifierValue() {
		waitForElementToBeVisible(documentIdentifierInput);
		return documentIdentifierInput.getAttribute("value").trim();
	}

	public String getDocumentSchemeValue() {
		waitForElementToBeVisible(documentSchemeInput);
		return documentSchemeInput.getAttribute("value").trim();
	}


	public boolean isParticipantSchemeEnabled() {
		return isEnabled(participantSchemaInput);
	}

	public boolean isParticipantIdentifierEnabled() {
		return isEnabled(participantIdentifierInput);
	}

	public boolean isDocumentIdentifierEnabled() {
		return isEnabled(documentIdentifierInput);
	}

	public ServiceMetadataWizardPopup clickMetadataWizard() {
		waitForElementToBeClickable(metadataWizardBtn).click();
		return new ServiceMetadataWizardPopup(driver);

	}

	public boolean isDocumentSchemeEnabled() {
		return isEnabled(documentSchemeInput);
	}


	public String getListedDomain() {
		return domainSelect.getSelectedValue().trim();
	}


}
