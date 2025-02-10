package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonDocumentConfigurationComponent;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Subresource Document Properties section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SubresourceDocumentConfigurationSection extends CommonDocumentConfigurationComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SubresourceDocumentConfigurationSection.class);

    public SubresourceDocumentConfigurationSection(WebDriver driver) {
        super(driver);
        LOG.debug("Subresource Document Configuration section is opened!");
    }
}
