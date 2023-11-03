package pages.administration.editResourcesPage;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Edit resource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
class EditResourceDocumentPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(EditResourceDocumentPage.class);

    protected EditResourceDocumentPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Edit resource document page.");

    }
}
