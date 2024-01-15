package pages.administration.editDomainsPage;

import ddsl.dcomponents.commonComponents.members.InviteMembersWithGridPopup;
import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Page object Groups tab of Edit Groups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class GroupTab extends SubcategoryTabComponent {
    public GroupTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public CreateGroupDetailsDialog clickCreateNewGroup(){
        create();
        return new CreateGroupDetailsDialog(driver);
    }

    public CreateGroupDetailsDialog clickEditGroup(String domainCode) throws Exception {
        edit("Group name", domainCode);
        return new CreateGroupDetailsDialog(driver);
    }

    public InviteMembersWithGridPopup clickOnGroupMembersBtn() {
        return clickOnMembersButton();
    }


    public void deleteGroup(String domainCode){
        delete("Group name", domainCode);
    }

}
