package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editGroupsPage.CreateResourceDetailsDialog;
import pages.administration.editGroupsPage.EditGroupsPage;
import pages.administration.editResourcesPage.CreateSubresourceDetailsDialog;
import pages.administration.editResourcesPage.EditResourcePage;
import pages.systemSettings.domainsPage.DomainsPage;
import rest.models.*;
import utils.TestRunData;

import java.util.Arrays;
import java.util.List;

public class EditGroupsPgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    EditGroupsPage editGroupPage;
    UserModel adminUser;
    DomainModel generatedDomainModel;
    DomainModel domainModel;

    GroupModel groupModel;
    SoftAssert soft;
    MemberModel superMember;
    MemberModel adminMember;
    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        generatedDomainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();
        groupModel = GroupModel.generatePublicGroup();
        adminMember = new MemberModel() {
        };
        adminMember.setUsername(adminUser.getUsername());
        adminMember.setRoleType("ADMIN");

        superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create user
        rest.users().createUser(adminUser).getString("userId");

        //create domain
        domainModel = rest.domains().createDomain(generatedDomainModel);

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


        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), TestRunData.getInstance().getNewPassword());
        editGroupPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
    }

    @Test(description = "EDTGRP-01 Group admins are able to invite/edit/remove group members", priority = 2)
    public void groupAdminsAreAbleToInviteEditRemoveMembers() {

        UserModel domainMember = UserModel.generateUserWithUSERrole();
        rest.users().createUser(domainMember);

        editGroupPage.selectDomain(domainModel, groupModel);
        //Add user
        editGroupPage.getGroupMembersTab().getInviteMemberBtn().click();
        editGroupPage.getGroupMembersTab().getInviteMembersPopup().selectMember(domainMember.getUsername(), "VIEWER");
        soft.assertTrue(editGroupPage.getGroupMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        //Change role of user
        editGroupPage.getGroupMembersTab().changeRoleOfUser(domainMember.getUsername(), "ADMIN");
        String currentRoleTypeOfuser = editGroupPage.getGroupMembersTab().getMembersGrid().getColumnValueForSpecificRow("Username", domainMember.getUsername(), "Role type");
        soft.assertEquals(currentRoleTypeOfuser, "ADMIN");

        //Remove user
        editGroupPage.getGroupMembersTab().removeUser(domainMember.getUsername());
        soft.assertFalse(editGroupPage.getGroupMembersTab().getMembersGrid().isValuePresentInColumn("Username", domainMember.getUsername()));

        soft.assertAll();
    }

    @Test(description = "EDTGRP-02 Group admins are able to create new resources")
    public void groupsAdminsAreAbleToCreateNewResources() {
        ResourceModel resourceModel = ResourceModel.generatePublicResource();

        editGroupPage.selectDomain(domainModel, groupModel);
        editGroupPage.goToTab("Resources");
        CreateResourceDetailsDialog createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();
        soft.assertTrue(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()));
        soft.assertAll();
    }

    @Test(description = "EDTGRP-03 Resource schema field is according to properties")
    public void resourceSchemeFieldIsAccordingToProperties() throws Exception {

        // Update identifiersBehaviour.ParticipantIdentifierScheme.validationRegex property value
        JSONObject propertyRegex = rest.propertiesClient().getProperty("identifiersBehaviour.ParticipantIdentifierScheme.validationRegex");
        propertyRegex.put("value", ".");
        rest.propertiesClient().setPropertyValue(propertyRegex);

        // Validate if the new value of property is used
        ResourceModel resourceModel = ResourceModel.generatePublicResource();
        resourceModel.setIdentifierScheme("2");
        editGroupPage.selectDomain(domainModel, groupModel);
        editGroupPage.goToTab("Resources");
        CreateResourceDetailsDialog createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();
        soft.assertTrue(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()));

        //Restore the old value
        propertyRegex.put("value", "^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$");
        rest.propertiesClient().setPropertyValue(propertyRegex);

        // Update identifiersBehaviour.scheme.mandatory property value
        JSONObject propertyMandatory = rest.propertiesClient().getProperty("identifiersBehaviour.scheme.mandatory");
        propertyMandatory.put("value", "false");
        //Restore the old value
        rest.propertiesClient().setPropertyValue(propertyMandatory);

        // Validate if the new value of property is used
        ResourceModel resourceModelForMandatory = ResourceModel.generatePublicResource();
        resourceModelForMandatory.setIdentifierScheme("");
        editGroupPage.refreshPage();
        editGroupPage.selectDomain(domainModel, groupModel);
        editGroupPage.goToTab("Resources");
        createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModelForMandatory);
        createResourceDetailsDialog.tryClickOnSave();
        soft.assertTrue(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModelForMandatory.getIdentifierValue()));

        //Restore the old value
        propertyMandatory.put("value", "true");
        rest.propertiesClient().setPropertyValue(propertyMandatory);

        soft.assertAll();
    }

    @Test(description = "EDTGRP-04 Group admins are not able to create duplicated resources with same identifier and schema")
    public void groupsAdminsAreNotAbleToCreateDuplicatedResourcesWithSameIdentifierAndSchema() {
        ResourceModel resourceModel = ResourceModel.generatePublicResource();

        editGroupPage.selectDomain(domainModel, groupModel);
        editGroupPage.goToTab("Resources");
        CreateResourceDetailsDialog createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();

        //Create resource with same identifier but different schema
        resourceModel.setIdentifierScheme("123-123-123");
        createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();
        soft.assertTrue(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()));

        //Try to create duplicated resource
        createResourceDetailsDialog = editGroupPage.getResourceTab().clickOnCreateNewResource();
        createResourceDetailsDialog.fillResourceDetails(resourceModel);
        createResourceDetailsDialog.tryClickOnSave();
        String duplicatedResourceMessage = editGroupPage.getAlertMessageAndClose();
        soft.assertEquals(duplicatedResourceMessage, String.format("Invalid request [CreateResourceForGroup]. Error: Resource [val:%s scheme:%s] already exists for domain!!", resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()));

        soft.assertAll();
    }

    @Test(description = "EDTGRP-05 Group admins are able to delete any resource")
    public void groupAdminsAreAbleToDeleteAnyResource() throws Exception {

        ResourceModel resourceModel = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        SubresourceModel subresourceModel = SubresourceModel.generatePublicSubResource();

        //Create resource for the group
        resourceModel = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, adminMember);

        EditResourcePage editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);
        editResourcePage.selectDomain(domainModel, groupModel, resourceModel);
        //Create subresource for the resouce
        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModel);
        createSubresourceDetailsDialog.tryClickOnSave();

        //Delete resouce with subresource and documents
        editGroupPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        editGroupPage.goToTab("Resources");
        editGroupPage.getResourceTab().deleteResource(resourceModel.getIdentifierValue());
        soft.assertFalse(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModel.getIdentifierValue()), "Deleted resource is still present in the grid");

        //Register domain to SML
        DomainsPage domainsPage = editGroupPage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getLeftSideGrid().searchAndClickElementInColumn("Domain code", domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(generatedDomainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();


        //create resource for domain registered to SML
        ResourceModel resourceModelForSML = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        resourceModelForSML.setIdentifierValue("0106:test");
        //add resource to group
        resourceModelForSML = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelForSML);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelForSML, adminMember);

        editGroupPage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_GROUPS);
        editGroupPage.goToTab("Resources");
        editGroupPage.getResourceTab().deleteResource(resourceModelForSML.getIdentifierValue());
        soft.assertFalse(editGroupPage.getResourceTab().getGrid().isValuePresentInColumn("Identifier", resourceModelForSML.getIdentifierValue()), "Deleted resource is still present in the grid");

        soft.assertAll();


    }


}
