package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.json.JSONObject;
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

public class EditDomainsPgTests extends SeleniumTest {

    DomiSMPPage homePage;
    LoginPage loginPage;
    EditDomainsPage editDomainPage;
    String domainId;
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
        JSONObject domainJson = rest.domains().createDomain(domainModel);
        domainId = domainJson.get("domainId").toString();
        rest.domains().addMembersToDomain(domainId, domainMember);

        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
    }

    @Test(description = "EDTDOM-01 Domain admins are able to invite/edit/remove members")
    public void DomainAdminsAreAbleToInviteEditRemoveMembers() throws Exception {
        UserModel memberUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(memberUser);

        //Invite user as VIEW and check if he has admin rights for domain
        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        editDomainPage.getDomainMembersTab().getInviteMemberBtn().click();
        editDomainPage.getDomainMembersTab().getInviteMembersPopup().selectMember(memberUser.getUsername(), "VIEWER");
        WebElement userMemberElement = editDomainPage.getDomainMembersTab().getMembersGrid().searchAndGetElementInColumn("Username", memberUser.getUsername());
        soft.assertNotNull(userMemberElement, "Invited user is found");

        //check if user has admin rights to domain as VIEWER
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(memberUser.getUsername(), data.getNewPassword());
        EditDomainsPage editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        WebElement domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        editDomainPage.getDomainMembersTab().changeRoleOfUser(memberUser.getUsername(), "ADMIN");

        //check if user has admin rights to domain as Admin
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(memberUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNotNull(domainElement, "Domain found for user which doesn't have rights");


        //Remove member user and check if he has access to the domain
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        editDomainPage.getDomainMembersTab().removeUser(memberUser.getUsername());
        userMemberElement = editDomainPage.getDomainMembersTab().getMembersGrid().searchAndGetElementInColumn("Username", memberUser.getUsername());
        soft.assertNull(userMemberElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(memberUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");
        soft.assertAll();

    }

    @Test(description = "EDTDOM-02 Domain admins are able to create new groups")
    public void DomainAdminsAreAbleToCreate() throws Exception {
        GroupModel groupToBeCreated = GroupModel.generatePublicDomain();
        editDomainPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();

        editDomainPage.goToTab("Group");

        CreateGroupDetailsDialog createGroupDetailsDialog = editDomainPage.getGroupTab().clickOnCreateNewGroup();
        createGroupDetailsDialog.fillGroupDetails(groupToBeCreated);
        Boolean isSaveSuccesfully = createGroupDetailsDialog.tryClickOnSave();
        soft.assertTrue(isSaveSuccesfully);

        WebElement createGroup = editDomainPage.getGroupTab().getGrid().searchAndGetElementInColumn("Group name", groupToBeCreated.getGroupName());
        soft.assertNotNull(createGroup);

        EditGroupsPage editgroupPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        soft.assertAll();

    }
}
