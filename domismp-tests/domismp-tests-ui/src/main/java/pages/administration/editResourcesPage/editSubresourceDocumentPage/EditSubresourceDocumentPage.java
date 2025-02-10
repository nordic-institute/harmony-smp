package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonExtendedEditDocumentPage;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Edit subresource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditSubresourceDocumentPage extends CommonExtendedEditDocumentPage {

    public EditSubresourceDocumentPage(WebDriver driver) {
        super(driver);

    }

    public SubresourceWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new SubresourceWizardDialog(driver);
    }
}
