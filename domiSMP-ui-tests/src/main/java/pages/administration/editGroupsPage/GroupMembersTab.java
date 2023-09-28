package pages.administration.editGroupsPage;

import ddsl.dcomponents.commonComponents.members.MembersComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class GroupMembersTab extends MembersComponent {

    public GroupMembersTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }
}

