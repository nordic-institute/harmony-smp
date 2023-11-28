package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.commonComponents.members.InviteMembersWithGridPopup;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editDomainsPage.CreateGroupDetailsDialog;
import pages.administration.editDomainsPage.EditDomainsPage;
import pages.administration.editGroupsPage.EditGroupsPage;
import rest.models.DomainModel;
import rest.models.GroupModel;
import rest.models.MemberModel;
import rest.models.UserModel;

/**
 * Test class for Edit domains page tests.
 */

public class EditDomainsPgTests extends SeleniumTest {

    DomiSMPPage homePage;
    LoginPage loginPage;
    EditDomainsPage editDomainPage;
    DomainModel domainModel;
    UserModel adminUser;
    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();

        MemberModel domainMember = new MemberModel();
        domainMember.setUsername(adminUser.getUsername());
        domainMember.setRoleType("ADMIN");

        rest.users().createUser(adminUser);
        domainModel = rest.domains().createDomain(domainModel);
        rest.domains().addMembersToDomain(domainModel, domainMember);

        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
    }

    @Test(description = "EDTDOM-01 Domain admins are able to invite/edit/remove members")
    public void domainAdminsAreAbleToInviteEditRemoveMembers() {
        UserModel domainMember = UserModel.generateUserWithUSERrole();
        rest.users().createUser(domainMember);

        //Add user
        editDomainPage.getDomainMembersTab().getInviteMemberBtn().click();
        editDomainPage.getDomainMembersTab().getInviteMembersPopup().selectMember(domainMember.getUsername(), "VIEWER");
        soft.assertTrue(editDomainPage.getDomainMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        //Change role of user
        editDomainPage.getDomainMembersTab().changeRoleOfUser(domainMember.getUsername(), "ADMIN");
        String currentRoleTypeOfuser = editDomainPage.getDomainMembersTab().getMembersGrid().getColumnValueForSpecificRow("Username", domainMember.getUsername(), "Role type");
        soft.assertEquals(currentRoleTypeOfuser, "ADMIN");

        //Remove user
        editDomainPage.getDomainMembersTab().removeUser(domainMember.getUsername());
        soft.assertFalse(editDomainPage.getDomainMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        soft.assertAll();
    }

    @Test(description = "EDTDOM-02 Domain admins are able to create new groups")
    public void domainAdminsAreAbleToCreate() {
        GroupModel groupToBeCreated = GroupModel.generatePublicGroup();
        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();

        editDomainPage.goToTab("Group");

        CreateGroupDetailsDialog createGroupDetailsDialog = editDomainPage.getGroupTab().clickCreateNewGroup();
        createGroupDetailsDialog.fillGroupDetails(groupToBeCreated);
        Boolean isSaveSuccesfully = createGroupDetailsDialog.tryClickOnSave();
        soft.assertTrue(isSaveSuccesfully);

        WebElement createGroup = editDomainPage.getGroupTab().getGrid().searchAndGetElementInColumn("Group name", groupToBeCreated.getGroupName());
        soft.assertNotNull(createGroup);
        createGroup.click();
        InviteMembersWithGridPopup inviteMembersWithGridPopup = editDomainPage.getGroupTab().clickOnGroupMembersBtn();
        soft.assertTrue(inviteMembersWithGridPopup.isMemberPresentByUsername(adminUser));
        inviteMembersWithGridPopup.clickOnCloseBtn();

        EditGroupsPage editgroupPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        editgroupPage.selectDomain(domainModel, groupToBeCreated);
        soft.assertAll();

    }

    @Test(description = "EDTDOM-03 Domain admins are not able to create duplicated groups")
    public void domainAdminsAreNotAbleToCreateDuplicatedGroups() {
        GroupModel duplicatedGroup = GroupModel.generatePublicGroup();

        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        editDomainPage.goToTab("Group");
        CreateGroupDetailsDialog createGroupDetailsDialog = editDomainPage.getGroupTab().clickCreateNewGroup();
        createGroupDetailsDialog.fillGroupDetails(duplicatedGroup);
        Boolean isSaveSuccesfully = createGroupDetailsDialog.tryClickOnSave();
        soft.assertTrue(isSaveSuccesfully);


        createGroupDetailsDialog = editDomainPage.getGroupTab().clickCreateNewGroup();
        createGroupDetailsDialog.fillGroupDetails(duplicatedGroup);
        isSaveSuccesfully = createGroupDetailsDialog.tryClickOnSave();
        String duplicateAlertMessage = createGroupDetailsDialog.getAlertArea().getAlertMessage();
        soft.assertTrue(isSaveSuccesfully);
        soft.assertEquals(duplicateAlertMessage, String.format("Invalid request [CreateGroup]. Error: Group with name [%s] already exists!!", duplicatedGroup.getGroupName()));
        soft.assertAll();
    }

    @Test(description = "EDTDOM-04 Domain admins are able to delete groups without resources")
    public void domainAdminsAreNotAbleToDeleteGroups() {
        GroupModel groupToBeDeleted = GroupModel.generatePublicGroup();

        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        editDomainPage.goToTab("Group");
        CreateGroupDetailsDialog createGroupDetailsDialog = editDomainPage.getGroupTab().clickCreateNewGroup();
        createGroupDetailsDialog.fillGroupDetails(groupToBeDeleted);
        Boolean isSaveSuccesfully = createGroupDetailsDialog.tryClickOnSave();
        soft.assertTrue(isSaveSuccesfully);

        editDomainPage.getGroupTab().deleteGroup(groupToBeDeleted.getGroupName());
        String deleteMessage = editDomainPage.getAlertArea().getAlertMessage();
        soft.assertEquals(deleteMessage, String.format("Domain group [%s] deleted", groupToBeDeleted.getGroupName()));
        soft.assertAll();
    }
}
