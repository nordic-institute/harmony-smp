package pages;

import ddsl.PageWithGrid;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public DomainsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Domains page.");
    }

    public DButton getCreateDomainBtn() {
        return new DButton(driver, AddBtn);
    }

    public void fillDomainData(String domainId) {
        domainIdInput.sendKeys("domainId");
        weToDSelect(responseSignatureCertificateDdl).selectValue("asdasda");
    }


}
