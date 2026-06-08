package pages.administration.editResourcesPage;

import ddsl.commonPages.CommonPageWithTabsAndGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.ResourceModel;

/**
 * Page object for the Edit resource page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourcePage extends CommonPageWithTabsAndGrid {
    private final static Logger LOG = LoggerFactory.getLogger(EditResourcePage.class);
    @FindBy(id = "domain_id")
    private WebElement domainDdl;
    @FindBy(id = "group_id")
    private WebElement groupDdl;

    @FindBy(css = ".smp-warning-panel")
    private WebElement notAdministratorWarning;


    public EditResourcePage(WebDriver driver) {
        super(driver);
        LOG.debug("Loading Edit resources page.");
    }
    public ResourcesMembersTab getResourceMembersTab() {
        return new ResourcesMembersTab(driver);
    }

    public ResourceDetailsTab getResourceDetailsTab() {
        return new ResourceDetailsTab(driver);
    }

    public SubresourceTab getSubresourceTab() {
        return new SubresourceTab(driver);
    }

    public void selectDomain(DomainModel domainModel, GroupModel groupModel, ResourceModel resourceModel) {
        weToMatSelect(domainDdl).selectByVisibleText(domainModel.getDomainCode());
        wait.forElementToBeClickable(groupDdl);
        weToMatSelect(groupDdl).selectByVisibleText(groupModel.getGroupName());
        String identifierValue = resourceModel.getIdentifierValue();
        // bug in filter -does not handle char _ O so search only by the last random part of username
        String searchKey = identifierValue.contains("_") ? identifierValue.substring(identifierValue.lastIndexOf("_") + 1) : identifierValue;
        weToDInput(filterInput).fill(searchKey);
        getLeftSideGrid().searchAndClickElementInColumn("Identifier", identifierValue);
    }
}
