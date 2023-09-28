package pages.administration.editGroupsPage;

import ddsl.CommonPageWithTabs;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditGroupsPage extends CommonPageWithTabs {
    /**
     * Page object for the Edit groups page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(EditGroupsPage.class);

    @FindBy(id = "domain_id")
    private WebElement domainDdl;
    @FindBy(id = "group_id")
    private WebElement groupDdl;

    public EditGroupsPage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Edit groups page.");
    }

    public GroupMembersTab getDomainMembersTab() {
        return new GroupMembersTab(driver);
    }

    public ResourceTab getGroupTab() {
        return new ResourceTab(driver);
    }

}
