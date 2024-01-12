package pages.systemSettings.domainsPage;

import ddsl.dcomponents.DComponent;
import ddsl.enums.ResponseCertificates;
import org.apache.poi.util.StringUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
/**
 * Page object for the Domains tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class DomainTab extends DComponent {
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
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public WebElement getDomainIdInput() {
        return domainIdInput;
    }

    public String getResponseSignatureCertificateSelectedValue() {
        return weToMatSelect(responseSignatureCertificateDdl).getCurrentText();
    }

    public String getVisibilityOfDomainSelectedValue() {
        return weToDSelect(visibilityOfDomainDdl).getCurrentValue().toUpperCase();
    }

    public void fillDomainData(DomainModel domainModel) {
        ResponseCertificates responseCertificates = ResponseCertificates
                .getByAlias(domainModel.getSignatureKeyAlias());
        if (StringUtil.isNotBlank(domainModel.getSignatureKeyAlias()) && responseCertificates == null) {
            LOG.warn("Cannot find signature key for alias [{}]", domainModel.getSignatureKeyAlias());
        }

        domainIdInput.sendKeys(domainModel.getDomainCode());

        if (responseCertificates != null) {
            weToMatSelect(responseSignatureCertificateDdl)
                    .selectByVisibleText(responseCertificates.getText());
        }
        weToDSelect(visibilityOfDomainDdl).selectValue(domainModel.getVisibility());
    }

    public void saveChanges() {
        if (saveBtn.isEnabled()) {
            saveBtn.click();
            wait.forElementToBeDisabled(saveBtn);
            try {
                saveBtn.getAttribute("disabled").equals("true");
                LOG.debug("Domain tab changes were succesfully saved");

            } catch (NullPointerException e) {
                LOG.debug("Domain tab changes were not saved");
            }

        }
    }


}
