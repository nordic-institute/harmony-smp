package ddsl.dcomponents.commonComponents.members;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import ddsl.dobjects.DButton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MembersComponent extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(MembersComponent.class);

    @FindBy(id = "addMemberButton")
    private WebElement inviteMemberBtn;

    @FindBy(id = "deleteButton")
    private WebElement removeMemberBtn;
    @FindBy(css = "div domain-member-panel")
    private WebElement sidePanel;

    public MembersComponent(WebDriver driver) {
        super(driver);
        LOG.debug("Members component is loaded");
        //wait.forElementToBeVisible(inviteMemberBtn);
    }

    public SmallGrid getMembersGrid() {
        return new SmallGrid(driver, sidePanel);
    }

    public InviteMembersPopup getInviteMembersPopup() {
        return new InviteMembersPopup(driver);
    }

    public DButton getInviteMemberBtn() {
        return weToDButton(inviteMemberBtn);
    }

    public void removeUser(String username) throws Exception {
        getMembersGrid().searchValueInColumn("Username", username).click();
        weToDButton(removeMemberBtn).click();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
        confirmationDialog.confirm();
    }

    public void changeRoleOfUser(String username, String newRole) throws Exception {
        getMembersGrid().searchValueInColumn("Username", username).click();
        weToDButton(sidePanel.findElement(By.id("editButton"))).click();
        getInviteMembersPopup().changeRole(newRole);


    }

}
