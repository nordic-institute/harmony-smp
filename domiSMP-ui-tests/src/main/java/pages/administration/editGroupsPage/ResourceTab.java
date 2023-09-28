package pages.administration.editGroupsPage;

import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.administration.editDomainsPage.CreateGroupDetailsDialog;

public class ResourceTab extends SubcategoryTabComponent {
    public ResourceTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);

    }

    public CreateGroupDetailsDialog clickOnCreateNewResource() throws Exception {
        create();
        return new CreateGroupDetailsDialog(driver);
    }
}
