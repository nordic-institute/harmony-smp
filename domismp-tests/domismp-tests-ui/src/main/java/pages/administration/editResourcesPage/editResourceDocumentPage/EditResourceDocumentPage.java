package pages.administration.editResourcesPage.editResourceDocumentPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for the Edit resource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourceDocumentPage extends CommonEditDocumentPage {

    public EditResourceDocumentPage(WebDriver driver) {
        super(driver);
    }

    public EditResourceDocumentWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new EditResourceDocumentWizardDialog(driver);
    }
}
