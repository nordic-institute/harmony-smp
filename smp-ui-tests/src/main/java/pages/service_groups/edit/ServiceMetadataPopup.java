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
	public ServiceMetadataPopup(WebDriver driver) {
		super(driver);

		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		domainSelect = new GenericSelect(driver, domainSelectContainer);
	}

	@FindBy(css = "mat-dialog-actions > button:nth-child(1)")
	private WebElement okButton;

	@FindBy(css = "mat-dialog-actions > button:nth-child(2)")
	private WebElement cancelButton;

	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(1)")
	private WebElement clearButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(2)")
	private WebElement generateXMLButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(3)")
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

	@FindBy(css = "mat-dialog-content #domain_id")
	private WebElement domainSelectContainer;
	private GenericSelect domainSelect;


	public EditPage clickOK(){
		/*waitForElementToBeClickable(okButton);
		okButton.click();
		waitForElementToBeGone(okButton);
		return new EditPage(driver);*/
		waitForElementToBeVisible(okButton);
		okButton.click();
		return new EditPage(driver);
	}
	public boolean isOKBtnEnabled(){
		return okButton.isEnabled();
	}
	public void fillDocIdAndDocIdScheme(String docID, String docScheme){
		waitForElementToBeVisible(documentIdentifierInput);
		clearAndFillInput(documentIdentifierInput, docID);
		clearAndFillInput(documentSchemeInput, docScheme);

	}

	public void fillForm(String domain, String docID, String docScheme) {
		waitForElementToBeVisible(documentIdentifierInput);
		domainSelect.selectWithIndex(0);

		clearAndFillInput(documentIdentifierInput, docID);
		clearAndFillInput(documentSchemeInput, docScheme);

		generateXMLButton.click();

	}


	public String docIDFieldContain(){
		return documentIdentifierInput.getAttribute("value");
	}

	public String docIDSchemeFieldContain(){
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

	public ServiceMetadataWizardPopup clickMetadataWizard(){
		waitForElementToBeClickable(metadataWizardBtn);
		metadataWizardBtn.click();
		return new ServiceMetadataWizardPopup(driver);

	}

	public boolean isDocumentSchemeEnabled() {
		return isEnabled(documentSchemeInput);
	}


	public String getListedDomain() {
		return domainSelect.getSelectedValue().trim();
	}


}
