package ddsl.dcomponents.commonComponents.members;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.UserModel;

/**
 * Generic component for Invite members with members grid popup. It contains the WebElements and the methods specific to the dialog.
 */
public class InviteMembersWithGridPopup extends DComponent {

    private final static Logger LOG = LoggerFactory.getLogger(InviteMembersWithGridPopup.class);
    @FindBy(id = "addMemberButton")
    private WebElement inviteMemberBtn;
    @FindBy(id = "editButton")
    private WebElement editMemberBtn;
    @FindBy(id = "closeDialogButton")
    private WebElement closeBtn;
    @FindBy(css = "domain-member-panel")
    private WebElement panel;

    public InviteMembersWithGridPopup(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, panel);
    }

    protected InviteMembersPopup inviteMember() throws ElementNotInteractableException {
        weToDButton(inviteMemberBtn).click();
        return new InviteMembersPopup(driver);
    }

    protected InviteMembersPopup editMember(String columnName, String value) {
        WebElement tobeEdited = getGrid().searchAndGetElementInColumn(columnName, value);
        tobeEdited.click();
        wait.forElementToBeEnabled(editMemberBtn);
        weToDButton(editMemberBtn).click();
        return new InviteMembersPopup(driver);
    }

    public boolean isMemberPresentByUsername(UserModel userModel) {
        return getGrid().isValuePresentInColumn("Username", userModel.getUsername());
    }

    public void clickOnCloseBtn() {
        weToDButton(closeBtn).click();
    }

}
