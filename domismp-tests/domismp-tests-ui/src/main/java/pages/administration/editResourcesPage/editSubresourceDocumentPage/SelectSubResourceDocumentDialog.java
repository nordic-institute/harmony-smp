package pages.administration.editResourcesPage.editSubresourceDocumentPage;

import ddsl.commonPages.commonDocumentPage.CommonSelectReferenceDialog;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SelectSubResourceDocumentDialog extends CommonSelectReferenceDialog {
    @FindBy(id = "subresource-value_id")
    private WebElement subresourceValueInput;
    @FindBy(id = "subresource-scheme_id")
    private WebElement subresourceSchemeInput;


    public SelectSubResourceDocumentDialog(WebDriver driver) {
        super(driver);
    }

    public void selectSubResourceReferenceBySubResourceIdentifier(String subresourceIdentifier) {
        weToDInput(subresourceValueInput).fill(subresourceIdentifier);
        weToDButton(searchBtn).click();
        getGrid().searchAndClickElementInColumn("Subr. value", subresourceIdentifier);
        weToDButton(saveBtn).click();
    }


}
