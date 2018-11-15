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

		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		domainSelect = new GenericSelect(driver, domainSelectContainer);
	}

	@FindBy(css = "mat-dialog-actions > div > button:nth-child(1)")
	private WebElement okButton;

	@FindBy(css = "mat-dialog-actions > div > button:nth-child(2)")
	private WebElement cancelButton;

	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(1)")
	private WebElement clearButon;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(2)")
	private WebElement generateXMLButton;
	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(3)")
	private WebElement validateButton;

	@FindBy(css = "#MetadataTextArea")
	private WebElement metadataTextArea;

	@FindBy(css = "#participanSchema_id")
	private WebElement participanSchemaInput;

	@FindBy(css = "#participantIdentifier_id")
	private WebElement participantIdentifierInput;

	@FindBy(css = "#documentScheme_id")
	private WebElement documentSchemeInput;

	@FindBy(css = "#documentIdentifier_id")
	private WebElement documentIdentifierInput;

	@FindBy(css = "mat-dialog-content #domain_id")
	private WebElement domainSelectContainer;
	private GenericSelect domainSelect;


	public void fillForm(String domain, String docID, String docScheme) {
		waitForElementToBeVisible(documentIdentifierInput);
		domainSelect.selectOptionWithText(domain);

		clearAndFillInput(documentIdentifierInput, docID);
		clearAndFillInput(documentSchemeInput, docScheme);

		generateXMLButton.click();

		waitForElementToBeClickable(okButton).click();

	}

	public String getParticipantSchemeValue(){return participanSchemaInput.getAttribute("value").trim();}
	public String getParticipantIdentifierValue(){return participantIdentifierInput.getAttribute("value").trim();}
	public String getDocumentIdentifierValue(){return documentIdentifierInput.getAttribute("value").trim();}
	public String getDocumentSchemeValue(){return documentSchemeInput.getAttribute("value").trim();}

	public boolean isParticipantSchemeEnabled(){return null == participanSchemaInput.getAttribute("disabled");}
	public boolean isParticipantIdentifierEnabled(){return null == participantIdentifierInput.getAttribute("disabled");}
	public boolean isDocumentIdentifierEnabled(){return null == documentIdentifierInput.getAttribute("disabled");}
	public boolean isDocumentSchemeEnabled(){return null == documentSchemeInput.getAttribute("disabled");}



	public String getlistedDomain(){return domainSelect.getSelectedValue().trim();}



}
