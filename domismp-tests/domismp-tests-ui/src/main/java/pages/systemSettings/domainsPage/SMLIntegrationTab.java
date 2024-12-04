package pages.systemSettings.domainsPage;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DInput;
import ddsl.dobjects.DSelect;
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
 * Page object for the SML integration tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SMLIntegrationTab extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SMLIntegrationTab.class);
    @FindBy(id = "smldomain_id")
    private WebElement smlDomainInput;

    @FindBy(id = "smlSMPId_id")
    private WebElement smlsmpIdentifierInput;
    @FindBy(css = ".mdc-text-field--invalid > div:nth-child(2) > div:nth-child(1) > div")
    private WebElement smlsmpIdentifierInputError;

    @FindBy(id = "smlClientKeyAlias_id")
    private WebElement smlClientCertificateAliasDdl;

    @FindBy(id = "smlClientCertHeaderAuth_id-button")
    private WebElement useClientCertBtn;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    @FindBy(id = "registerButton")
    private WebElement registerBtn;
    @FindBy(id = "unregisterButton")
    private WebElement unregisterBtn;


    public SMLIntegrationTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public void fillSMLIntegrationTab(DomainModel domainModel) {
        ResponseCertificates responseCertificates = ResponseCertificates
                .getByAlias(domainModel.getSignatureKeyAlias());
        if (StringUtil.isNotBlank(domainModel.getSignatureKeyAlias()) && responseCertificates == null) {
            LOG.warn("Cannot find signature key for alias [{}]", domainModel.getSignatureKeyAlias());
        }
        try {

            weToDInput(smlDomainInput).fill(domainModel.getSmlSubdomain());
            weToDInput(smlsmpIdentifierInput).fill(domainModel.getSmlSmpId());
            if (responseCertificates != null) {
                weToDSelect(smlClientCertificateAliasDdl)
                        .selectByVisibleText(responseCertificates.getText(), true);
            }
            weToDButton(useClientCertBtn).click();

        } catch (Exception e) {
            LOG.error("Cannot fill SML integration data");
            throw new RuntimeException(e);
        }

    }
    public void saveChanges() {
        if (saveBtn.isEnabled()) {
            saveBtn.click();
            wait.forElementToBeDisabled(saveBtn);
            if (saveBtn.getDomAttribute("disabled").equals("true")) {
                LOG.debug("SML Integration tab changes were succesfully saved");
            } else {
                LOG.error("SML Integration  tab changes were not saved");
            }
        }
    }

    public void registerToSML() throws Exception {
        try {
            if (weToDButton(registerBtn).isEnabled()) {
                weToDButton(registerBtn).click();
                ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
                confirmationDialog.confirm();
            }

        } catch (Exception e) {
            LOG.error("Register button is not enabled");
            throw new Exception(e);
        }

    }

    public void unregisterToSML() throws Exception {
        try {
            if (weToDButton(unregisterBtn).isEnabled()) {
                weToDButton(unregisterBtn).click();
                ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
                confirmationDialog.confirm();
            }

        } catch (Exception e) {
            LOG.error("Unregister button is not enabled");
            throw new Exception(e);
        }

    }

    public String getSMLSMPErrorMessage() {
        try {
            return smlsmpIdentifierInputError.getText();
        } catch (Exception e) {
            LOG.debug("No SMLSMP validation error");
            return null;
        }
    }

    public DInput getsmlsmpIdentifierInput() {
        return weToDInput(smlsmpIdentifierInput);
    }

    public DSelect getSMLClientCertificateAliasDdl() {
        return weToDSelect(smlClientCertificateAliasDdl);
    }



}
