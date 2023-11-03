package pages.administration.editResourcesPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Page object Resource details tab of Edit Resource page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceDetailsTab extends DComponent {

    @FindBy(id = "showResource")
    private WebElement editDocumentBtn;

    public ResourceDetailsTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public EditResourceDocumentPage clickOnEditDocument() throws ElementNotInteractableException {
        weToDButton(editDocumentBtn).click();
        return new EditResourceDocumentPage(driver);
    }
}
