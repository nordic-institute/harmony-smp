package pages.administration.editResourcesPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DButton;
import ddsl.dobjects.DCheckbox;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditResourceDocumentPage;

/**
 * Page object Resource details tab of Edit Resource page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceDetailsTab extends DComponent {

    @FindBy(id = "showResource")
    private WebElement editDocumentBtn;

    @FindBy(id = "reviewEnabled_id")
    private WebElement reviewProcessEnabledCheckbox;

    @FindBy(css = ".alert-warning > span:nth-child(2)")
    private WebElement reviewProcessEnabledWarningLbl;

    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    public ResourceDetailsTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public EditResourceDocumentPage clickOnEditDocument() throws ElementNotInteractableException {
        weToDButton(editDocumentBtn).click();
        return new EditResourceDocumentPage(driver);
    }

    public DCheckbox getReviewProcessEnabledCheckbox() {
        return new DCheckbox(driver, reviewProcessEnabledCheckbox);
    }

    public String getReviewProcessWarning() {
        wait.forElementToBeVisible(reviewProcessEnabledWarningLbl);
        return reviewProcessEnabledWarningLbl.getText();
    }

    public DButton getSaveBtn() {
        return new DButton(driver, saveBtn);
    }
}
