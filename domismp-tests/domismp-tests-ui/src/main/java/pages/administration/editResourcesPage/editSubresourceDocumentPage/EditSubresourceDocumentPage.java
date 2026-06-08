package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonExtendedEditDocumentPage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Edit subresource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditSubresourceDocumentPage extends CommonExtendedEditDocumentPage {
    private final static Logger LOG = LoggerFactory.getLogger(EditSubresourceDocumentPage.class);

    public EditSubresourceDocumentPage(WebDriver driver) {
        super(driver);

    }

    public SubresourceWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new SubresourceWizardDialog(driver);
    }

    public SubresourceDocumentConfigurationSection getDocumentConfigurationSection() {
        weToDButton(documentConfigurationMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentConfigurationMenuBtn).click();

        return new
                SubresourceDocumentConfigurationSection(driver);
    }

    public SubresourceDocumentPropertiesSection getDocumentPropertiesSection() {
        wait.forXMillis(500);
        weToDButton(documentPropertiesnMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentPropertiesnMenuBtn).click();
        LOG.debug("Opening Document properties section.");
        return new SubresourceDocumentPropertiesSection(driver);
    }

}
