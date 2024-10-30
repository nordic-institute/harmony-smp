package pages.search;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.Set;

public class ResourcesPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(ResourcesPage.class);

    @FindBy(id = "ResourceIdentifier")
    private WebElement resourceIdentifierInput;
    @FindBy(id = "ResourceScheme")
    private WebElement resourceSchemeInput;
    @FindBy(id = "Domain")
    private WebElement domainDdl;
    @FindBy(id = "DocumentType")
    private WebElement documentTypeDdl;
    @FindBy(id = "searchbutton_id")
    private WebElement searchBtn;
    @FindBy(css = "smp-search-table")
    private WebElement panel;

    public ResourcesPage(WebDriver driver) {
        super(driver);
    }

    private ResourcePageGrid getGrid() {
        return new ResourcePageGrid(driver, panel);
    }

    public XMLUtils openURLResouceDocument(String resourceIdentifier, String resourceScheme) {
        weToDInput(resourceIdentifierInput).fill(resourceIdentifier);
        weToDInput(resourceSchemeInput).fill(resourceScheme);
        LOG.debug("Click on Open URL for " + resourceIdentifier);
        weToDButton(searchBtn).click();
        getGrid().searchAndClickElementInColumn("Resource URL", "Open URL");
        Set<String> handles = driver.getWindowHandles();
        Iterator<String> iterator = handles.iterator();

        // Switch to the new tab
        String original = iterator.next();
        String newTab = iterator.next();
        driver.switchTo().window(newTab);
        LOG.debug("Switching to new tab" + newTab);

        try {
            XMLUtils documentXML = new XMLUtils(driver.getPageSource());
            LOG.debug("Reading document value");
            return documentXML;
        } catch (ParserConfigurationException e) {
            LOG.error("Reading document value");

            throw new RuntimeException(e);

        }
    }

    public XMLUtils openURLSubResouceDocument(String resourceIdentifier, String resourceScheme, String subresourceIdentifier) {
        weToDInput(resourceIdentifierInput).fill(resourceIdentifier);
        weToDInput(resourceSchemeInput).fill(resourceScheme);
        LOG.debug("Click on Open URL for " + resourceIdentifier);
        weToDButton(searchBtn).click();
        getGrid().openSubresource("Upd.", "chevron_right", "Subresource identifier", subresourceIdentifier);
        Set<String> handles = driver.getWindowHandles();
        Iterator<String> iterator = handles.iterator();

        // Switch to the new tab
        String original = iterator.next();
        String newTab = iterator.next();
        driver.switchTo().window(newTab);
        LOG.debug("Switching to new tab" + newTab);

        try {
            XMLUtils documentXML = new XMLUtils(driver.getPageSource());
            LOG.debug("Reading document value");
            return documentXML;
        } catch (ParserConfigurationException e) {
            LOG.error("Reading document value");
            throw new RuntimeException(e);
        }

    }
}
