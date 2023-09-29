package pages.administration.editGroupsPage;

import ddsl.CommonPageWithTabs;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;

public class EditGroupsPage extends CommonPageWithTabs {
    /**
     * Page object for the Edit groups page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(EditGroupsPage.class);

    @FindBy(id = "domain_id")
    private WebElement domainDdl;
    @FindBy(id = "group_id")
    private Select groupDdl;

    public EditGroupsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Edit groups page.");
    }

    public void selectGroup(DomainModel domainModel, GroupModel groupModel) throws Exception {
        weToMatDSelect(domainDdl).selectOptionByText(domainModel.getDomainCode());
    }

    public GroupMembersTab getDomainMembersTab() {
        return new GroupMembersTab(driver);
    }

    public ResourceTab getGroupTab() {
        return new ResourceTab(driver);
    }

}
