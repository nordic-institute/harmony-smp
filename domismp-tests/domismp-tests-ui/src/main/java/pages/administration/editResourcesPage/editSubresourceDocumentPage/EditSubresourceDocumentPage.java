package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import org.openqa.selenium.WebDriver;
import pages.administration.editResourcesPage.common.CommonEditDocumentPage;

/**
 * Page object for the Edit subresource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditSubresourceDocumentPage extends CommonEditDocumentPage {

    public EditSubresourceDocumentPage(WebDriver driver) {
        super(driver);

    }

    public SubresourceWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new SubresourceWizardDialog(driver);
    }
}
