package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonDocumentPropertiesComponent;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Subresource Document Properties section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceDocumentPropertiesSection extends CommonDocumentPropertiesComponent {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceDocumentPropertiesSection.class);

    public ResourceDocumentPropertiesSection(WebDriver driver) {
        super(driver);
        LOG.debug("Resource Document Properties section is opened!");
    }
}
