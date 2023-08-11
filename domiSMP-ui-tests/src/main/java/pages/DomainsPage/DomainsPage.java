package pages.DomainsPage;

import ddsl.PageWithGrid;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DomainsPage extends PageWithGrid {
    /**
     * Page object for the Users page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(DomainsPage.class);

    @FindBy(css = "[role = \"tab\"]")
    private List<WebElement> tabList;

    @FindBy(css = "smp-warning-panel span")
    private WebElement warningLabel;


    public DomainsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Domains page.");
    }

    public DButton getCreateDomainBtn() {
        return new DButton(driver, addBtn);
    }

    public ResourceTab getResourceTab() {

        return new ResourceTab(driver);
    }

    public DomainTab getDomainTab() {

        return new DomainTab(driver);
    }

    public SMLIntegrationTab getSMLIntegrationTab() {

        return new SMLIntegrationTab(driver);
    }

    public MembersTab getMembersTab() {

        return new MembersTab(driver);
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

    public String getAlert() {

        return getAlertArea().getAlertMessage();
    }

    public String getDomainWarningMessage() {
        return warningLabel.getText();
    }


}
