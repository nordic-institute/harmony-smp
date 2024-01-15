package pages.administration.editGroupsPage;

import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
/**
 * Page object Resource tab of EditGroups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceTab extends SubcategoryTabComponent {
    public ResourceTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public CreateResourceDetailsDialog clickOnCreateNewResource() throws ElementNotInteractableException {
        create();
        return new CreateResourceDetailsDialog(driver);
    }
}
