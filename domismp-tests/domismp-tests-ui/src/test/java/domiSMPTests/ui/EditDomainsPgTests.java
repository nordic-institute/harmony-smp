package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.commonComponents.domanPropertyEditDialog.DomainPropertyEditDialog;
import ddsl.dcomponents.commonComponents.members.InviteMembersWithGridPopup;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editDomainsPage.CreateGroupDetailsDialog;
import pages.administration.editDomainsPage.EditDomainsPage;
import pages.administration.editGroupsPage.CreateResourceDetailsDialog;
import pages.administration.editGroupsPage.EditGroupsPage;
import rest.models.*;
import utils.TestRunData;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for Edit domains page tests.
 */

public class EditDomainsPgTests extends SeleniumTest {

    DomiSMPPage homePage;
    LoginPage loginPage;
    EditDomainsPage editDomainPage;
    DomainModel domainModel;
    UserModel adminUser;
    UserModel normalUser;
    MemberModel memberAdmin;
    MemberModel memberUser;

    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();
        normalUser = UserModel.generateUserWithUSERrole();

        memberAdmin = new MemberModel();
        memberAdmin.setUsername(adminUser.getUsername());
        memberAdmin.setRoleType("ADMIN");

        memberUser = new MemberModel();
        memberUser.setUsername(normalUser.getUsername());
        memberUser.setRoleType("ADMIN");

        rest.users().createUser(adminUser);
        rest.users().createUser(normalUser);

        domainModel = rest.domains().createDomain(domainModel);

        rest.domains().addMembersToDomain(domainModel, memberAdmin);
        rest.domains().addMembersToDomain(domainModel, memberUser);


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
        soft.assertTrue(isSaveSuccesfully, "Domain creation alert is not present");

        WebElement createGroup = editDomainPage.getGroupTab().getGrid().searchAndGetElementInColumn("Group name", groupToBeCreated.getGroupName());
        soft.assertNotNull(createGroup);
        createGroup.click();
        InviteMembersWithGridPopup inviteMembersWithGridPopup = editDomainPage.getGroupTab().clickOnGroupMembersBtn();
        soft.assertTrue(inviteMembersWithGridPopup.isMemberPresentByUsername(adminUser), "User is not present in the members list");
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

    @Test(description = "EDTDOM-09 Domain admins are able to change default properties for domains")
    public void domainAdminsAreAbleToChangeDefaultPropertiesForDomains() throws Exception {
        DomainModel currentDomainModel = DomainModel.generatePublicDomainModelWithSML();
        //create domain
        currentDomainModel = rest.domains().createDomain(currentDomainModel);
        //  rest.domains().addMembersToDomain(domainModel, adminMember);
        rest.domains().addMembersToDomain(currentDomainModel, memberUser);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        currentDomainModel = rest.domains().addResourcesToDomain(currentDomainModel, resourcesToBeAdded);

        editDomainPage.logout();
        //Login with user role which is domain admin
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        editDomainPage
                .getLeftSideGrid().searchAndClickElementInColumn("Domain code", currentDomainModel.getDomainCode());
        editDomainPage.goToTab("Configuration");

        //Check is modifying boolean values
        String boolPropertyName = "identifiersBehaviour.scheme.mandatory";
        DomainPropertyEditDialog domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(boolPropertyName);
        domainPropertyEditDialog.setDomainValue(false);
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();

        //verify changes
        soft.assertFalse(editDomainPage.getConfigurationTab().isSystemValueUsed(boolPropertyName), "Property is marked as it's using system value");
        soft.assertEquals("false", editDomainPage.getConfigurationTab().getCurrentPropertyValue(boolPropertyName));


        //Verify disabling system property
        String useDomainProperty = "identifiersBehaviour.ParticipantIdentifierScheme.validationRegex";
        domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(useDomainProperty);
        domainPropertyEditDialog.disableSystemValue();
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();
        //verify changes
        soft.assertFalse(editDomainPage.getConfigurationTab().isSystemValueUsed(useDomainProperty), "Property is marked as it's using system value");

        //Verify change to enabling system property
        domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(useDomainProperty);
        domainPropertyEditDialog.enableSystemValue();
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();
        //verify changes
        soft.assertTrue(editDomainPage.getConfigurationTab().isSystemValueUsed(useDomainProperty));

        // String property value
        String stringProperty = "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes";
        String defaultPropertyValue = editDomainPage.getConfigurationTab().getCurrentPropertyValue(stringProperty);

        domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(stringProperty);
        domainPropertyEditDialog.setDomainValue("${identifier}${identifier}");
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();

        soft.assertFalse(editDomainPage.getConfigurationTab().isSystemValueUsed(stringProperty), "Property is marked as it's using system value");
        soft.assertTrue(editDomainPage.getConfigurationTab().getCurrentPropertyValue(stringProperty).equalsIgnoreCase("${identifier}${identifier}"), "Configuration table is not showing updated value");

        //Check if the property value is updated with system value after use system value is enabled
        domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(stringProperty);
        domainPropertyEditDialog.enableSystemValue();
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();
        soft.assertTrue(editDomainPage.getConfigurationTab().getCurrentPropertyValue(stringProperty).equalsIgnoreCase(defaultPropertyValue), "Configuration table is not showing system value");

        soft.assertAll();
    }

    @Test(description = "EDTDOM-11 Domain properties values are applied")
    public void domanPropertiesAreApplied() throws Exception {
        DomainModel currentDomainModel = DomainModel.generatePublicDomainModelWithSML();
        GroupModel currentGroupModel = GroupModel.generatePublicGroup();
        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        currentDomainModel = rest.domains().createDomain(currentDomainModel);
        rest.domains().addMembersToDomain(currentDomainModel, memberUser);
        rest.domains().addMembersToDomain(currentDomainModel, superMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        currentDomainModel = rest.domains().addResourcesToDomain(currentDomainModel, resourcesToBeAdded);

        //create group for domain
        currentGroupModel = rest.domains().createGroupForDomain(currentDomainModel, currentGroupModel);

        //add users to groups
        rest.groups().addMembersToGroup(currentDomainModel, currentGroupModel, memberUser);

        editDomainPage.logout();
        //Login with user role which is domain admin
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        editDomainPage
                .getLeftSideGrid().searchAndClickElementInColumn("Domain code", currentDomainModel.getDomainCode());
        editDomainPage.goToTab("Configuration");

        //Remove resource schema mandatory
        String boolPropertyName = "identifiersBehaviour.scheme.mandatory";
        DomainPropertyEditDialog domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(boolPropertyName);
        domainPropertyEditDialog.setDomainValue(false);
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();


        EditGroupsPage editGroupsPage = editDomainPage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        editGroupsPage.selectDomain(currentDomainModel, currentGroupModel);
        editGroupsPage.goToTab("Resources");
        CreateResourceDetailsDialog createResourceDetailsDialog = editGroupsPage.getResourceTab().clickOnCreateNewResource();

        //create resource without Resource schema field
        ResourceModel resourceModel = ResourceModel.generatePublicResource();
        resourceModel.setIdentifierScheme("");


        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();
        soft.assertTrue(editGroupsPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()), "Resource was not found in the grid");

        //Enable resource schema mandatory
        editGroupsPage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        editDomainPage
                .getLeftSideGrid().searchAndClickElementInColumn("Domain code", currentDomainModel.getDomainCode());
        editDomainPage.goToTab("Configuration");

        domainPropertyEditDialog = editDomainPage.getConfigurationTab().openProperty(boolPropertyName);
        domainPropertyEditDialog.enableSystemValue();
        domainPropertyEditDialog.pressOk();
        editDomainPage.getConfigurationTab().saveChanges();

        //verify is schema is  mandatory - using system property value
        editGroupsPage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        editGroupsPage.selectDomain(currentDomainModel, currentGroupModel);
        editGroupsPage.goToTab("Resources");
        createResourceDetailsDialog = editGroupsPage.getResourceTab().clickOnCreateNewResource();

        //create resource without Resource schema field
        ResourceModel resourceModel2 = ResourceModel.generatePublicResource();
        resourceModel2.setIdentifierScheme("");


        createResourceDetailsDialog.fillResourceDetails(resourceModel2);
        Boolean saveisDisabled = createResourceDetailsDialog.tryClickOnSave();
        soft.assertFalse(saveisDisabled, "Save action didn't worked");
        soft.assertFalse(editGroupsPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel2.getIdentifierValue()), "Resource is  present in the grid");


        soft.assertAll();
    }


}
