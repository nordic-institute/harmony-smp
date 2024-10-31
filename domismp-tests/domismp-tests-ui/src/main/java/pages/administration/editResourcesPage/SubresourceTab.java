package pages.administration.editResourcesPage;

import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditSubresourceDocumentPage;
import rest.models.SubresourceModel;

/**
 * Page object Subresource tab of Edit Resource page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SubresourceTab extends SubcategoryTabComponent {
    public SubresourceTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public CreateSubresourceDetailsDialog createSubresource() throws ElementNotInteractableException {
        create();
        return new CreateSubresourceDetailsDialog(driver);
    }

    public EditSubresourceDocumentPage editSubresouceDocument(SubresourceModel subresourceModel) throws Exception {
        edit("Identifier", subresourceModel.getIdentifierValue());
        return new EditSubresourceDocumentPage(driver);
    }

}

