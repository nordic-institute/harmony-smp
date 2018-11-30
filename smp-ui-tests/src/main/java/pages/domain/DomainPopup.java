package pages.domain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class DomainPopup extends PageComponent {
	public DomainPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}
	
	@FindBy(css = "domain-details-dialog > table > tbody > tr > td > button:nth-child(1)")
	WebElement okBtn;
	
	@FindBy(css = "domain-details-dialog > table > tbody > tr > td > button:nth-child(2)")
	WebElement cancelBtn;
	
	@FindBy(css = "#domainCode_id")
	WebElement domainCodeInput;
	
	@FindBy(css = "#smldomain_id")
	WebElement smldomainInput;
	
	@FindBy(css = "#signatureKeyAlias_id")
	WebElement responseSignatureCertInput;

	@FindBy(css = "#smlSMPId_id")
	WebElement smlSMPIdInput;
	
	@FindBy(css = "#smlClientHeader_id")
	WebElement smlClientHeaderInput;
	
	@FindBy(css = "#smlClientKeyAlias_id")
	WebElement smlClientKeyAliasInput;

	public void clickOK(){
		waitForElementToBeClickable(okBtn).click();
		waitForElementToBeGone(okBtn);
	}
	public void clickCancel() {
		waitForElementToBeClickable(cancelBtn).click();
		waitForElementToBeGone(cancelBtn);
	}
	
	public boolean isLoaded() {
		waitForElementToBeVisible(okBtn);
		if(!okBtn.isDisplayed()){return false;}
		if(!domainCodeInput.isDisplayed()){return false;}
		if(!smldomainInput.isDisplayed()){return false;}
		if(!responseSignatureCertInput.isDisplayed()){return false;}
		if(!smlClientHeaderInput.isDisplayed()){return false;}
		if(!smlClientKeyAliasInput.isDisplayed()){return false;}
		if(!cancelBtn.isDisplayed() || !cancelBtn.isEnabled()){return false;}
		
		return true;
	}

	public boolean isOKButtonEnabled() {return waitForElementToBeVisible(okBtn).isEnabled();}
	public boolean isCancelButtonEnabled() {return waitForElementToBeVisible(cancelBtn).isEnabled();}

	public boolean isDomainCodeInputEnabled() {return waitForElementToBeVisible(domainCodeInput).isEnabled();}
	public boolean isSMLDomainInputEnabled() {return waitForElementToBeVisible(smldomainInput).isEnabled();}
	public boolean isResponseSignatureCertInputEnabled() {return waitForElementToBeVisible(responseSignatureCertInput).isEnabled();}

	public boolean isSMLSMPIdInputEnabled() {return waitForElementToBeVisible(smlSMPIdInput).isEnabled();}
	public boolean isSMLClientHeaderEnabled() {return waitForElementToBeVisible(smlClientHeaderInput).isEnabled();}
	public boolean isSMLClientKeyAliasInputEnabled() {return waitForElementToBeVisible(smlClientKeyAliasInput).isEnabled();}


	public void fillResponseSignatureCertInput(String text){
		clearAndFillInput(responseSignatureCertInput, text);
	}
	public void fillSMLSMPIdInput(String text){
		waitForXMillis(500);
		clearAndFillInput(smlSMPIdInput, text);
	}
	public void fillSMLClientHeader(String text){
		clearAndFillInput(smlClientHeaderInput, text);
	}
	public void fillSMLClientKeyAliasInput(String text){
		clearAndFillInput(smlClientKeyAliasInput, text);
	}

	public void fillDataForNewDomain(String domainCode, String smlDomain, String responseSignature, String smlSmpID, String clientCertHeader, String certAlias){
		clearAndFillInput(domainCodeInput, domainCode);
		clearAndFillInput(smldomainInput, smlDomain);
		clearAndFillInput(responseSignatureCertInput, responseSignature);
		clearAndFillInput(smlSMPIdInput, smlSmpID);
		clearAndFillInput(smlClientHeaderInput, clientCertHeader);
		clearAndFillInput(smlClientKeyAliasInput, certAlias);

	}



}
