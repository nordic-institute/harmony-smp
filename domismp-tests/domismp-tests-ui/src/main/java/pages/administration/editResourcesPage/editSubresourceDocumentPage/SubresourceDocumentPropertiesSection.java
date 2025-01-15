package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.administration.editResourcesPage.common.CommonDocumentPropertiesComponent;

/**
 * Page object for the Subresource Document Properties section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SubresourceDocumentPropertiesSection extends CommonDocumentPropertiesComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SubresourceDocumentPropertiesSection.class);

    public SubresourceDocumentPropertiesSection(WebDriver driver) {
        super(driver);
        LOG.debug("Subresource Document Properties section is opened!");
    }
}
