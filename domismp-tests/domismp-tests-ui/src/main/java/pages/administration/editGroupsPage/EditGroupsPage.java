package pages.administration.editGroupsPage;

import ddsl.CommonPageWithTabs;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;
/**
 * Page object for the Edit groups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditGroupsPage extends CommonPageWithTabs {
    private final static Logger LOG = LoggerFactory.getLogger(EditGroupsPage.class);
    @FindBy(id = "domain_id")
    private WebElement domainDdl;
    @FindBy(id = "group_id")
    private WebElement groupDdl;

    public EditGroupsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Edit groups page.");
    }

    public void selectGroup(DomainModel domainModel, GroupModel groupModel){
        weToMatSelect(domainDdl).selectByVisibleText(domainModel.getDomainCode());
    }

    public GroupMembersTab getGroupMembersTab() {
        return new GroupMembersTab(driver);
    }

    public ResourceTab getResourceTab() {
        return new ResourceTab(driver);
    }

    public void selectDomain(DomainModel domainModel, GroupModel groupModel) {
        weToMatSelect(domainDdl).selectByVisibleText(domainModel.getDomainCode());
        weToMatSelect(groupDdl).selectByVisibleText(groupModel.getGroupName());
    }

}
