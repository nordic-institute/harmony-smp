package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editResourcesPage.EditResourcePage;
import rest.models.*;
import utils.TestRunData;

import java.util.Arrays;
import java.util.List;

public class EditResourcePgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    EditResourcePage editResourcePage;
    UserModel adminUser;
    DomainModel domainModel;
    GroupModel groupModel;
    ResourceModel resourceModel;
    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();
        groupModel = GroupModel.generatePublicGroup();
        resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();

        MemberModel adminMember = new MemberModel() {
        };
        adminMember.setUsername(adminUser.getUsername());
        adminMember.setRoleType("ADMIN");

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create user
        rest.users().createUser(adminUser).getString("userId");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, adminMember);
        rest.domains().addMembersToDomain(domainModel, superMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);

        //add users to groups
        rest.groups().addMembersToGroup(domainModel, groupModel, adminMember);

        //add resource to group
        resourceModel = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, adminMember);


        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), TestRunData.getInstance().getNewPassword());
        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);
    }

    @Test(description = "EDTRES-01 Resource admins are able to invite/edit/remove resource members", priority = 0)
    public void resourceAdminsAreAbleToInviteEditRemoveMembers() {

        UserModel domainMember = UserModel.generateUserWithUSERrole();
        rest.users().createUser(domainMember);

        editResourcePage.selectDomain(domainModel, groupModel, resourceModel);
        editResourcePage.goToTab("Members");
        //Add user
        editResourcePage.getResourceMembersTab().getInviteMemberBtn().click();
        editResourcePage.getResourceMembersTab().getInviteMembersPopup().selectMember(domainMember.getUsername(), "VIEWER");
        soft.assertTrue(editResourcePage.getResourceMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        //Change role of user
        editResourcePage.getResourceMembersTab().changeRoleOfUser(domainMember.getUsername(), "ADMIN");
        String currentRoleTypeOfuser = editResourcePage.getResourceMembersTab().getMembersGrid().getColumnValueForSpecificRow("Username", domainMember.getUsername(), "Role type");
        soft.assertEquals(currentRoleTypeOfuser, "ADMIN");

        //Remove user
        editResourcePage.getResourceMembersTab().removeUser(domainMember.getUsername());
        soft.assertFalse(editResourcePage.getResourceMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        soft.assertAll();
    }

    @Test(description = "EDTRES-02 Resource admins are to view their resources", priority = 1)
    public void resourceAdminsAreAbleToViewTheirResources() {

        UserModel domainMember = UserModel.generateUserWithUSERrole();
        rest.users().createUser(domainMember);

        editResourcePage.selectDomain(domainModel, groupModel, resourceModel);
        editResourcePage.goToTab("Members");
        //Add user
        editResourcePage.getResourceMembersTab().getInviteMemberBtn().click();
        editResourcePage.getResourceMembersTab().getInviteMembersPopup().selectMember(domainMember.getUsername(), "VIEWER");
        soft.assertTrue(editResourcePage.getResourceMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));
    }

    @Test(description = "EDTGRP-02 Group admins are able to create new resources", priority = 0)
    public void groupsAdminsAreAbleToCreateNewResources() throws Exception {

        UserModel resourseAdmin = UserModel.generateUserWithADMINrole();
        rest.users().createUser(resourseAdmin);

        MemberModel resourceMember = new MemberModel();
        resourceMember.setUsername(resourseAdmin.getUsername());
        resourceMember.setRoleType("VIEWER");

        homePage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(resourseAdmin.getUsername(), TestRunData.getInstance().getNewPassword());
        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);
        soft.assertTrue(editResourcePage.isCurrentUserAdministrator());

        resourceMember = rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, resourceMember);
        //Check if viewer can see resources in Edit Resource page
        editResourcePage.refreshPage();
        soft.assertTrue(editResourcePage.isCurrentUserAdministrator());

        resourceMember.setRoleType("ADMIN");
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, resourceMember);
        //Check if admin can see resources in Edit Resource page

        editResourcePage.refreshPage();
        soft.assertTrue(editResourcePage.getLeftSideGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()));
        soft.assertAll();
    }
}
