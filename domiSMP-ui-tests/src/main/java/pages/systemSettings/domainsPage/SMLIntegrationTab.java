package pages.systemSettings.domainsPage;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
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

    @FindBy(id = "smlClientKeyAlias_id")
    private WebElement smlClientCertificateAliasDdl;

    @FindBy(id = "smlClientCertHeaderAuth_id-button")
    private WebElement useClientCertBtn;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    @FindBy(id = "registerButton")
    private WebElement registerBtn;

    public SMLIntegrationTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public void fillSMLIntegrationTab(DomainModel domainModel) {
        try {
            weToDInput(smlDomainInput).fill(domainModel.getSmlSubdomain());
            weToDInput(smlsmpIdentifierInput).fill(domainModel.getSmlSmpId());
            //TODO : check of clientcertificatealias is changed from mat-select to select
            weToDSelect(smlClientCertificateAliasDdl).selectByVisibleText(domainModel.getSignatureKeyAlias());
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
            if (saveBtn.getAttribute("disabled").equals("true")) {
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

}
