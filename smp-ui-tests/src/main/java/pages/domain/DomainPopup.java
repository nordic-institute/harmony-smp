package pages.domain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class DomainPopup extends PageComponent {
	public DomainPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

		signatureCertSelect = new GenericSelect(driver, signatureCertSelectContainer);
		smlClientAliasSelect = new GenericSelect(driver, smlClientAliasSelectContainer);


	}
	
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
	
//	@FindBy(css = "#smlClientHeader_id")
//	WebElement smlClientHeaderInput;
	
	@FindBy(css = "#smlClientKeyAlias_id")
	WebElement smlClientAliasSelectContainer;
	GenericSelect smlClientAliasSelect;


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
		if(!smlDomainInput.isDisplayed()){return false;}
		if(null == signatureCertSelect){return false;}
//		if(!smlClientHeaderInput.isDisplayed()){return false;}
		if(null == smlClientAliasSelect){return false;}
		return cancelBtn.isDisplayed() && cancelBtn.isEnabled();
	}

	public boolean isDomainCodeInputEnabled() {return waitForElementToBeVisible(domainCodeInput).isEnabled();}
	public boolean isSMLDomainInputEnabled() {return waitForElementToBeVisible(smlDomainInput).isEnabled();}

	public void fillSMLSMPIdInput(String text){
		waitForXMillis(500);
		clearAndFillInput(smlSMPIdInput, text);
	}

	public void fillDataForNewDomain(String domainCode, String smlDomain, String smlSmpID, String clientCertHeader){
		clearAndFillInput(domainCodeInput, domainCode);
		clearAndFillInput(smlDomainInput, smlDomain);
		signatureCertSelect.selectFirstOption();
		clearAndFillInput(smlSMPIdInput, smlSmpID);
//		clearAndFillInput(smlClientHeaderInput, clientCertHeader);
		smlClientAliasSelect.selectFirstOption();
	}



}
