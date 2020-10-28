package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

import java.util.List;

public class ServiceGroupPopup extends PageComponent {
	public ServiceGroupPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		try {
			ownersPanel = new AccordionSection(driver, ownersPanelContainer);
		} catch (Exception e) {

		}
		domainsPanel = new AccordionSection(driver, domainsPanelContainer);

	}

	@FindBy(css = "#owner_expansion_panel_id")
	private WebElement ownersPanelContainer;
	public AccordionSection ownersPanel;

	@FindBy(css = "#domain_expansion_panel_id")
	private WebElement domainsPanelContainer;
	public AccordionSection domainsPanel;

	@FindBy(css = "mat-dialog-actions > div > button:nth-child(1)")
	private WebElement okButton;

	@FindBy(css = "mat-dialog-actions > div > button:nth-child(2)")
	private WebElement cancelButton;

	@FindBy(css = "#participantIdentifier_id")
	private WebElement participantIdentifierInput;

	@FindBy(css = "#participantScheme_id")
	private WebElement participantSchemeInput;

	@FindBy(css = "#extensionTextArea")
	private WebElement extensionTextArea;

	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(1)")
	private WebElement clearExtensionButton;

	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(2)")
	private WebElement extensionWizardButton;

	@FindBy(css = "mat-card-content > mat-toolbar > mat-toolbar-row > button:nth-child(3)")
	private WebElement validateExtensionButton;

	@FindBy(css = "mat-card-content > div > div.ng-star-inserted")
	private WebElement errorContainer;


	public boolean isOKButtonPresent(){
		log.info("is ok button visible");
		return isVisible(okButton);
	}
	public boolean isCancelButtonPresent(){
		log.info("is cancel button visible");
		return isVisible(cancelButton);
	}

	public boolean isExtensionAreaEditable(){
		log.info("is Extension Area Editable");
		return isEnabled( extensionTextArea);
	}
	public boolean isParticipantIdentifierInputEnabled(){
		log.info("is Participant Identifier Input Enabled");
		return isEnabled( participantIdentifierInput);
	}

	public boolean isParticipantSchemeInputEnabled(){
		log.info("is Participant Scheme Input Enabled");
		return isEnabled( participantSchemeInput);
	}

	public boolean isOwnersPanelEnabled(){
		log.info("check owner panel is enabled");
		return ownersPanel.optionsEnabled();
	}

	public boolean isOwnersPanelPresent(){
		log.info("check owner panel is present");
		return null == ownersPanel;
	}

	public boolean isDomainsPanelEnabled(){
		log.info("check domains panel is enabled");
		return domainsPanel.optionsEnabled();
	}

	public void clickOK(){
		log.info("click ok..");
		waitForElementToBeClickable(okButton).click();
		waitForElementToBeGone(okButton);
	}

	public void clickClear(){
		log.info("click clear..");
		waitForElementToBeClickable(clearExtensionButton).click();
		waitForXMillis(100);
	}

	public void clickCancel(){
		log.info("click cancel..");
		waitForElementToBeClickable(cancelButton).click();
		waitForElementToBeGone(okButton);
	}

	public void fillForm(String identifier, String scheme, List<String> owners, List<String> domains, String extension){
		log.info("filling form..");

		waitForElementToBeVisible(participantIdentifierInput);

		clearAndFillInput(participantIdentifierInput, identifier);
		clearAndFillInput(participantSchemeInput, scheme);

		for (String owner : owners) {
			ownersPanel.selectOptionWithText(owner);
		}

		domainsPanel.expandSection();

		for (String domain : domains) {
			domainsPanel.selectOptionWithText(domain);
		}

		clearAndFillInput(extensionTextArea, extension);

	}

	public String getParticipantIdentifierValue(){
		return waitForElementToBeVisible(participantIdentifierInput).getAttribute("value").trim();
	}

	public String getParticipantSchemeValue(){
		return participantSchemeInput.getAttribute("value").trim();
	}

	public String getErrorMessage(){
		return errorContainer.getText().trim();
	}

	public String getExtensionAreaContent(){
		log.info("getting Extension Area Content");

		waitForElementToBeVisible(extensionTextArea);
		waitForXMillis(500);
		return extensionTextArea.getAttribute("value").trim();
	}

	public void enterDataInExtensionTextArea(String text){
		waitForElementToBeVisible(extensionTextArea).clear();
		extensionTextArea.sendKeys(text);
		waitForXMillis(1000);
	}

	public void fillParticipantIdentifier(String participantIdentifier){
		clearAndFillInput(participantIdentifierInput, participantIdentifier);
	}
	public void fillParticipantScheme(String participantScheme){
		clearAndFillInput(participantSchemeInput, participantScheme);
	}
	public void chooseFirstOwner(){
		if(!ownersPanel.isExpanded()){ ownersPanel.expandSection();}
		ownersPanel.selectOptionWithIndex(0);

	}
	public void chooseFirstDomain(){
		if(!domainsPanel.isExpanded()){ domainsPanel.expandSection();}
		domainsPanel.selectOptionWithIndex(0);
	}
	public void fillExtensionArea(String extension){
		clearAndFillInput(extensionTextArea, extension);
	}

	public void generateRndExtension(){
		extensionWizardButton.click();
		new SGExtensionWizzard(driver).fillWithRndStrings();

	}




}
