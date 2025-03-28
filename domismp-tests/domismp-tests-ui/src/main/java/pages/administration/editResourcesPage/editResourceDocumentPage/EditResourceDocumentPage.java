package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonExtendedEditDocumentPage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Edit resource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourceDocumentPage extends CommonExtendedEditDocumentPage {
    private final static Logger LOG = LoggerFactory.getLogger(EditResourceDocumentPage.class);

    public EditResourceDocumentPage(WebDriver driver) {
        super(driver);
    }

    public EditResourceDocumentWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new EditResourceDocumentWizardDialog(driver);
    }

    public ResourceDocumentConfigurationSection getDocumentConfigurationSection() {
        weToDButton(documentConfigurationMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentConfigurationMenuBtn).click();

        return new
                ResourceDocumentConfigurationSection(driver);
    }

    public ResourceDocumentConfigurationSection getDocumentPropertiesSection() {
        wait.forXMillis(500);
        weToDButton(documentPropertiesnMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentPropertiesnMenuBtn).click();
        LOG.debug("Opening Document properties section.");
        return new ResourceDocumentConfigurationSection(driver);
    }




}
