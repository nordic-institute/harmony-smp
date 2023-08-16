package pages.DomainsPage;

import ddsl.PageWithGrid;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;

import java.util.List;

public class DomainsPage extends PageWithGrid {
    /**
     * Page object for the Users page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(DomainsPage.class);

    @FindBy(id = "domainCode_id")
    private WebElement domainIdInput;
    @FindBy(id = "signatureKeyAlias_id")
    private WebElement responseSignatureCertificateDdl;

    @FindBy(id = "domainVisibility_id")
    private WebElement visibilityOfDomainDdl;

    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    @FindBy(css = "[role = \"tab\"] ")
    private List<WebElement> tabList;

    private ResourceTab resourceTab;


    public DomainsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Domains page.");
    }

    public DButton getCreateDomainBtn() {
        return new DButton(driver, AddBtn);
    }

    public ResourceTab getResourceTab() {

        return new ResourceTab(driver);
    }


    public void fillDomainData(DomainModel domainModel) {

        domainIdInput.sendKeys(domainModel.getDomainCode());
        weToDSelect(responseSignatureCertificateDdl).selectValue(domainModel.getSignatureKeyAlias());
    }

    public String saveChangesAndGetMessage() {
        saveBtn.click();
        return getAlertArea().getAlertMessage();
    }

    public void goToTab(String tabName) {
        for (WebElement element : tabList) {
            if (element.getText().contains(tabName)) {
                element.click();
                wait.forAttributeToContain(element, "aria-selected", "true");
                LOG.debug("Domain tab {} is opened", tabName);
            }
        }
    }


}
