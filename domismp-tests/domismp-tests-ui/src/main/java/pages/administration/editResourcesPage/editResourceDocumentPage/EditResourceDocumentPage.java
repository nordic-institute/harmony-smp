package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonExtendedEditDocumentPage;
import org.openqa.selenium.WebDriver;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Page object for the Edit resource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourceDocumentPage extends CommonExtendedEditDocumentPage {

    public EditResourceDocumentPage(WebDriver driver) {
        super(driver);
    }

    public EditResourceDocumentWizardDialog clickOnDocumentWizard() {
        weToDButton(documentWizardBtn).click();
        return new EditResourceDocumentWizardDialog(driver);
    }

    public ResourceDocumentEditor getEditor() throws ParserConfigurationException {
        return new ResourceDocumentEditor(this.getDocumentValue());
    }


}
