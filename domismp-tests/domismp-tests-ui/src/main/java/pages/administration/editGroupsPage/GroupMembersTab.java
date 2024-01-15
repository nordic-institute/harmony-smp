package pages.administration.editGroupsPage;

import ddsl.dcomponents.commonComponents.members.MembersComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
/**
 * Page object Groups tab of EditGroups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class GroupMembersTab extends MembersComponent {
    public GroupMembersTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }
}

