package pages.DomainsPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;

public class DomainTab extends DComponent {
    /**
     * Page object for the Domains tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(DomainTab.class);
    @FindBy(id = "domainCode_id")
    private WebElement domainIdInput;
    @FindBy(id = "signatureKeyAlias_id")
    private WebElement responseSignatureCertificateDdl;
    @FindBy(id = "domainVisibility_id")
    private WebElement visibilityOfDomainDdl;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    public DomainTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);

    }

    public WebElement getDomainIdInput() {
        return domainIdInput;
    }

    public String getResponseSignatureCertificateSelectedValue() {
        return weToDSelect(responseSignatureCertificateDdl).getCurrentValue();
    }

    public String getVisibilityOfDomainSelectedValue() {
        return weToDSelect(visibilityOfDomainDdl).getCurrentValue().toUpperCase();
    }

    public void fillDomainData(DomainModel domainModel) {

        domainIdInput.sendKeys(domainModel.getDomainCode());
        weToDSelect(responseSignatureCertificateDdl).selectByVisibleText(domainModel.getSignatureKeyAlias(), true);
        weToDSelect(visibilityOfDomainDdl).selectValue(domainModel.getVisibility());
    }

    public void saveChanges() {
        if (saveBtn.isEnabled()) {
            saveBtn.click();
            wait.forElementToBeDisabled(saveBtn);
            if (saveBtn.getAttribute("disabled").equals("true")) {
                LOG.debug("Domain tab changes were succesfully saved");
            } else {
                LOG.error("Domain tab changes were not saved");
            }
        }
    }


}