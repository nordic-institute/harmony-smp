package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editResourcesPage.EditResourceDocumentPage;
import pages.administration.editResourcesPage.EditResourcePage;
import rest.models.*;
import utils.TestRunData;
import utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
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

    MemberModel adminMember;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();
        groupModel = GroupModel.generatePublicGroup();
        resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();

        adminMember = new MemberModel() {
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

    @Test(description = "EDTRES-01 Resource admins are able to invite/edit/remove resource members")
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

    @Test(description = "EDTRES-03 Resource admins are able to add generated document", priority = 1)
    public void resourceAdminsAreAbleToAddGeneratedDocument() throws Exception {

        //Generate resource Oasis 3
        ResourceModel resourceModelOasis3 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis3.setResourceTypeIdentifier(ResourceTypes.OASIS3.getName());
        resourceModelOasis3 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis3);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis3, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis3);
        editResourcePage.goToTab("Resource details");
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.clickOnGenerate();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.getAlertArea().closeAlert();

        String currentGeneratedValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.clickOnValidate();
        soft.assertEquals(editResourceDocumentPage.getAlertArea().getAlertMessage(), "Document is Valid.", "Generated document is not valid");

        soft.assertNotNull(currentGeneratedValue, "Document is empty");
        XMLUtils documentXML = new XMLUtils(currentGeneratedValue);
        soft.assertTrue(documentXML.isNodePresent("CPP"), "Node is not present in generated document");

        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);

        //Generate resource Oasis 2
        ResourceModel resourceModelOasis2 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis2.setResourceTypeIdentifier(ResourceTypes.OASIS2.getName());
        resourceModelOasis2 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis2);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis2, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis2);
        editResourcePage.goToTab("Resource details");
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.clickOnGenerate();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.getAlertArea().closeAlert();
        String oasis2GeneratedDocumentValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.clickOnValidate();

        soft.assertEquals(editResourceDocumentPage.getAlertArea().getAlertMessage(), "Document is Valid.", "Generated document is not valid");
        soft.assertNotNull(oasis2GeneratedDocumentValue, "Document is empty");
        XMLUtils oasis2DocumentXML = new XMLUtils(oasis2GeneratedDocumentValue);
        soft.assertTrue(oasis2DocumentXML.isNodePresent("ns5:ServiceGroup"), " Service group Node is not present in generated document");

        //Generate resource Oasis 3
        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModelOasis1.setResourceTypeIdentifier(ResourceTypes.OASIS1.getName());
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);
        editResourcePage.goToTab("Resource details");
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.clickOnGenerate();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.getAlertArea().closeAlert();


        String oasis1GeneratedDocumentValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.clickOnValidate();

        soft.assertEquals(editResourceDocumentPage.getAlertArea().getAlertMessage(), "Document is Valid.", "Generated document is not valid");
        soft.assertNotNull(oasis1GeneratedDocumentValue, "Document is empty");
        XMLUtils oasis1DocumentXML = new XMLUtils(oasis1GeneratedDocumentValue);
        soft.assertTrue(oasis1DocumentXML.isNodePresent("ServiceGroup"), " Service group Node is not present in generated document");
        soft.assertAll();
    }

    @Ignore //TODO: continue test with select version
    @Test(description = "EDTRES-04 Resource admins are able to add generated document", priority = 1)
    public void resourceAdminsAreAbleToAddGeneratedDocument2() throws ParserConfigurationException {

        editResourcePage.goToTab("Resource details");
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.clickOnGenerate();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.getAlertArea().closeAlert();
        XMLUtils document1 = new XMLUtils(editResourceDocumentPage.getDocumentValue());

    }
}



