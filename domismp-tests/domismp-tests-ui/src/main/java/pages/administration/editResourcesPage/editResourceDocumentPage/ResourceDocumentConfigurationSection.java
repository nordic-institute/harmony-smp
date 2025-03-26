package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonDocumentConfigurationComponent;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Subresource Document Properties section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceDocumentConfigurationSection extends CommonDocumentConfigurationComponent {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceDocumentConfigurationSection.class);

    public ResourceDocumentConfigurationSection(WebDriver driver) {
        super(driver);
        LOG.debug("Resource Document Configuration section is opened!");
    }

    public SelectResourceDocumentDialog clickOnSelectReferenceBtn() {
        weToDButton(selectReferenceBtn).click();
        return new SelectResourceDocumentDialog(driver);
    }

}
