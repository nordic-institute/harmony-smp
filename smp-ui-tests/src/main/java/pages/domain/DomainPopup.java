package pages.domain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class DomainPopup extends PageComponent {
	@FindBy(css = "domain-details-dialog button:nth-child(1)")
	WebElement okBtn;
	@FindBy(css = "domain-details-dialog button:nth-child(2)")
	WebElement cancelBtn;
	@FindBy(css = "#domainCode_id")
	WebElement domainCodeInput;
	@FindBy(css = "#smldomain_id")
	WebElement smlDomainInput;
	@FindBy(css = "#signatureKeyAlias_id")
	WebElement signatureCertSelectContainer;
	GenericSelect signatureCertSelect;
	@FindBy(css = "#smlSMPId_id")
	WebElement smlSMPIdInput;
	@FindBy(css = "span.mat-slide-toggle-bar")
	WebElement userClientCertHeaderToggle;
	@FindBy(css = "#smlClientCertHeaderAuth_id-input")
	WebElement userClientCertHeaderToggleInput;
	@FindBy(css = "div.mat-form-field-infix > div.ng-star-inserted")
	WebElement domainCodeValidationError;
	@FindBy(css = "#smlClientKeyAlias_id")
	WebElement smlClientAliasSelectContainer;
	GenericSelect smlClientAliasSelect;
	@FindBy(css = "#MetadataTextArea")
	private WebElement metadataTextArea;
	public DomainPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		signatureCertSelect = new GenericSelect(driver, signatureCertSelectContainer);
		smlClientAliasSelect = new GenericSelect(driver, smlClientAliasSelectContainer);


	}

	public void clickOK() {
		waitForElementToBeClickable(okBtn).click();
		waitForXMillis(500);
		waitForElementToBeGone(okBtn);
	}

	public void clickCancel() {
		waitForElementToBeClickable(cancelBtn).click();
		waitForElementToBeGone(cancelBtn);
	}

	public boolean isLoaded() {
		log.info("checking if domain popup is properly loaded");

		waitForElementToBeEnabled(okBtn);

		return isVisible(okBtn)
				&& isVisible(domainCodeInput)
				&& isVisible(smlDomainInput)
				&& isVisible(cancelBtn)
				&& isEnabled(cancelBtn)
				&& (null != signatureCertSelect)
				&& (null != smlClientAliasSelect);
	}

	public boolean isDomainCodeInputEnabled() {
		log.info("domain code input");
		return isEnabled(domainCodeInput);
	}

	public boolean isSMLDomainInputEnabled() {
		return isEnabled(smlDomainInput);
	}

	public void fillSMLSMPIdInput(String text) {
		log.info("fill sml smp input with " + text);
		waitForXMillis(500);
		clearAndFillInput(smlSMPIdInput, text);
	}

	public void fillDataForNewDomain(String domainCode, String smlDomain, String smlSmpID, String clientCertHeader) {
		log.info("filling data for new domain");
		clearAndFillInput(domainCodeInput, domainCode);
		clearAndFillInput(smlDomainInput, smlDomain);
		signatureCertSelect.selectFirstOption();
		clearAndFillInput(smlSMPIdInput, smlSmpID);
		smlClientAliasSelect.selectFirstOption();
	}

	public String domainCodeValidationGetErrMsg() {
		try {
			waitForElementToBeVisible(domainCodeValidationError);
			return domainCodeValidationError.getText();
		} catch (Exception e) {
		}
		return null;
	}


	public String getDuplicateDomainErrorMsgText() {
		WebElement duplicateDomainErrorMsg = driver.findElement(By.cssSelector(".mat-form-field-infix > div.ng-star-inserted"));
		return duplicateDomainErrorMsg.getText();
	}

	public String getSmlSmpIdValidationMsg() {
		WebElement invalidSmlSmpIdErrorMsg = driver.findElement(By.cssSelector("div.mat-form-field-infix > div.ng-star-inserted"));
		try {
			waitForElementToBeVisible(invalidSmlSmpIdErrorMsg);
			return invalidSmlSmpIdErrorMsg.getText();
		} catch (Exception e) {
		}
		return null;
	}

	public boolean isEnableOkButton() {
		try {
			return okBtn.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isEnableCancelButton() {
		try {
			return cancelBtn.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public String domainCode63CharValidationGetErrMsg() {
		try {
			waitForElementToBeVisible(domainCodeValidationError);
			return domainCodeValidationError.getAttribute("value");
		} catch (Exception e) {
		}
		return null;
	}

	public String getSmlSmpId63CharValidationMsg() {
		WebElement invalidSmlSmpIdErrorMsg = driver.findElement(By.cssSelector("div.mat-form-field-infix > div.ng-star-inserted"));
		try {
			waitForElementToBeVisible(invalidSmlSmpIdErrorMsg);
			return invalidSmlSmpIdErrorMsg.getAttribute("value");
		} catch (Exception e) {
		}
		return null;
	}

	public String xmlFieldVALUE() {
		log.info("value is " + metadataTextArea.getAttribute("value"));
		return metadataTextArea.getAttribute("value");
	}

	public void clearAndFillDomainCodeInput(String domainCode) {
		log.info("clear and fill domain code data");
		clearAndFillInput(domainCodeInput, domainCode);
	}

	public void clearAndFillSMLDomainInput(String SMLDomain) {
		log.info("filling only domain code data for new domain");
		clearAndFillInput(smlDomainInput, SMLDomain);
	}

	public void clickUserClientCertHeaderToggle() {
		waitForElementToBeClickable(userClientCertHeaderToggle);
		waitForXMillis(500);
		userClientCertHeaderToggle.click();
		waitForXMillis(500);

	}

	public String checkedUserClientCertHeaderToggl() {

		return userClientCertHeaderToggleInput.getAttribute("aria-checked");


	}


}
