package domiSMPTests.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editResourcesPage.EditResourcePage;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditResourceDocumentPage;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditResourceDocumentWizardDialog;
import rest.models.*;
import utils.Generator;
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

    @Test(description = "EDTRES-04 Resource admins are able to add document using Document wizard for Oasis 1.0", priority = 1)
    public void resourceAdminsAreAbleToAddDocimentUsingDocumentWizardOasis1() throws ParserConfigurationException, JsonProcessingException {

        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);


        //add resource to group
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);

        editResourcePage.goToTab("Resource details");
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.clickOnGenerate();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.getAlertArea().closeAlert();
        editResourceDocumentPage.clickOnValidate();
        soft.assertEquals(editResourceDocumentPage.getAlertArea().getAlertMessage(), "Document is Valid.");

        EditResourceDocumentWizardDialog editResourceDocumentWizardDialog = editResourceDocumentPage.clickOnDocumentWizard();

        String generatedExtensionIdvalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionNamevalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionAgencyIdvalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionAgencyNamevalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionAgencyURIvalue = "www." + Generator.randomAlphaNumericValue(8) + ".com";
        String generatedExtensionVersionIdvalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionURIvalue = "www." + Generator.randomAlphaNumericValue(8) + ".com";
        String generatedExtensionReasonCodevalue = Generator.randomAlphaNumericValue(8);
        String generatedExtensionReasonvalue = Generator.randomAlphaNumericValue(8);


        editResourceDocumentWizardDialog.getExtensionIdInput().fill(generatedExtensionIdvalue);
        editResourceDocumentWizardDialog.getExtensionNamenput().fill(generatedExtensionNamevalue);
        editResourceDocumentWizardDialog.getExtensionAgencyIdnput().fill(generatedExtensionAgencyIdvalue);
        editResourceDocumentWizardDialog.getExtensionAgencyNameInput().fill(generatedExtensionAgencyNamevalue);
        editResourceDocumentWizardDialog.getExtensionAgencyURIInput().fill(generatedExtensionAgencyURIvalue);
        editResourceDocumentWizardDialog.getExtensionVersionIDInput().fill(generatedExtensionVersionIdvalue);
        editResourceDocumentWizardDialog.getExtensionURIInput().fill(generatedExtensionURIvalue);
        editResourceDocumentWizardDialog.getExtensionReasonCodeInput().fill(generatedExtensionReasonCodevalue);
        editResourceDocumentWizardDialog.getExtensionReasonInput().fill(generatedExtensionReasonvalue);

        editResourceDocumentWizardDialog.clickOK();
        editResourceDocumentPage.clickOnSave();
        editResourceDocumentPage.clickOnValidate();

        String document = editResourceDocumentPage.getDocumentValue();
        XMLUtils documentXML = new XMLUtils(document);


        soft.assertEquals(documentXML.getNodeValue("ExtensionID"), generatedExtensionIdvalue, "Wrong ExtensionId value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionName"), generatedExtensionNamevalue, "Wrong ExtensionName value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionAgencyID"), generatedExtensionAgencyIdvalue, "Wrong ExtensionAgencyID value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionAgencyName"), generatedExtensionAgencyNamevalue, "Wrong ExtensionAgencyName value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionAgencyURI"), generatedExtensionAgencyURIvalue, "Wrong ExtensionAgencyURI value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionVersionID"), generatedExtensionVersionIdvalue, "Wrong ExtensionVersionID value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionURI"), generatedExtensionURIvalue, "Wrong ExtensionURI value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionReasonCode"), generatedExtensionReasonCodevalue, "Wrong ExtensionReasonCode value");
        soft.assertEquals(documentXML.getNodeValue("ExtensionReason"), generatedExtensionReasonvalue, "Wrong ExtensionReason value");

        soft.assertAll();
    }


}



