package pages.administration.editDomainsPage;

import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class GroupTab extends SubcategoryTabComponent {
    public GroupTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public CreateGroupDetailsDialog clickCreateNewGroup() throws Exception {
        create();
        return new CreateGroupDetailsDialog(driver);
    }

    public CreateGroupDetailsDialog clickEditGroup(String domainCode) throws Exception {
        edit("Group name", domainCode);
        return new CreateGroupDetailsDialog(driver);
    }

    public void deleteGroup(String domainCode) throws Exception {
        delete("Group name", domainCode);
    }

}
