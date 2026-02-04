package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonSelectReferenceDialog;
import org.openqa.selenium.WebDriver;

public class SelectResourceDocumentDialog extends CommonSelectReferenceDialog {
    public SelectResourceDocumentDialog(WebDriver driver) {
        super(driver);
    }

    public void selectResourceReferenceByResourceIdentifier(String resourceIdentifier) {
        weToDInput(resourceValueInput).fill(resourceIdentifier);
        weToDButton(searchBtn).click();
        getGrid().searchAndClickElementInColumn("Res. value", resourceIdentifier);
        weToDButton(saveBtn).click();
    }

    public boolean isResourceReferenceByResourceIdentifierPresent(String resourceIdentifier) {
        weToDInput(resourceValueInput).fill(resourceIdentifier);
        weToDButton(searchBtn).click();
        return getGrid().isValuePresentInColumn("Res. value", resourceIdentifier);

    }
}
