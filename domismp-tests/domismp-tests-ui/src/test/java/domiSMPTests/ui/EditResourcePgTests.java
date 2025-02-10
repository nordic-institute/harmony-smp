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
import pages.administration.editResourcesPage.CreateSubresourceDetailsDialog;
import pages.administration.editResourcesPage.EditResourcePage;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditResourceDocumentPage;
import pages.administration.editResourcesPage.editResourceDocumentPage.EditResourceDocumentWizardDialog;
import pages.administration.editResourcesPage.editResourceDocumentPage.ResourceDocumentEditor;
import pages.administration.editResourcesPage.editResourceDocumentPage.SelectResourceDocumentDialog;
import pages.administration.editResourcesPage.editSubresourceDocumentPage.EditSubresourceDocumentPage;
import pages.administration.editResourcesPage.editSubresourceDocumentPage.SubresourceDocumentPropertiesSection;
import pages.administration.editResourcesPage.editSubresourceDocumentPage.SubresourceWizardDialog;
import pages.administration.reviewTasksPage.ReviewDocumentPage;
import pages.administration.reviewTasksPage.ReviewTasksPage;
import pages.search.ResourcesPage;
import rest.models.*;
import utils.FileUtils;
import utils.Generator;
import utils.TestRunData;
import utils.XMLUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

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
        adminMember.setHasPermissionReview(true);

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

    @Test(description = "EDTRES-01 Resource admins are able to invite        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);\n/edit/remove resource members")
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
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getGenerateBtn().click();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getAlertArea().closeAlert();

        String currentGeneratedValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.getValidateBtn().click();
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
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getGenerateBtn().click();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getAlertArea().closeAlert();
        String oasis2GeneratedDocumentValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.getValidateBtn().click();

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
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getGenerateBtn().click();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getAlertArea().closeAlert();


        String oasis1GeneratedDocumentValue = editResourceDocumentPage.getDocumentValue();

        editResourceDocumentPage.getValidateBtn().click();

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
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getGenerateBtn().click();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getAlertArea().closeAlert();
        editResourceDocumentPage.getValidateBtn().click();
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
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getValidateBtn().click();

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

    @Test(description = "EDTRES-06 Resource admin is not able to Save invalid documents", priority = 1)
    public void resourceAdminIsNotAbleToSaveInvalidDocuments() throws Exception {
        //Generate resource Oasis 3
        ResourceModel resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModel.setResourceTypeIdentifier(ResourceTypes.OASIS1.getName());
        resourceModel = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModel);
        editResourcePage.goToTab("Resource details");
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();

        String currentGeneratedValue = editResourceDocumentPage.getDocumentValue();
        XMLUtils documentXML = new XMLUtils(currentGeneratedValue);

        //set invalid value for scheme
        String invalidScheme = "wrong-scheme";
        documentXML.setAttributeValueForNode("ParticipantIdentifier", "scheme", invalidScheme);
        editResourceDocumentPage.setDocumentValue(documentXML.printDoc());
        editResourceDocumentPage.getSaveBtn().click();

        String error = editResourceDocumentPage.getAlertArea().getAlertMessage();
        soft.assertEquals(error, "Invalid Identifier: [" + invalidScheme + "::" + resourceModel.getIdentifierValue() + "]. Invalid scheme [" + invalidScheme + "]!", "Wrong error message for invalid scheme: ");


        editResourceDocumentPage.clickOnCancelAndConfirm();
        editResourceDocumentPage.getNewVersionBtn().click();

        documentXML = new XMLUtils(currentGeneratedValue);

        //set invalid value for scheme
        String invalidParticipant = "wrong-participant";
        documentXML.setContextValueForNode("ParticipantIdentifier", invalidParticipant);
        editResourceDocumentPage.setDocumentValue(documentXML.printDoc());
        editResourceDocumentPage.getSaveBtn().click();

        error = editResourceDocumentPage.getAlertArea().getAlertMessage();
        soft.assertTrue(error.startsWith("Invalid request [StoreResourceValidation]. Error: ResourceException: Participant identifiers don't match between URL parameter [ResourceIdentifier"), "Wrong error message for invalid participant identifier: ");

        soft.assertAll();
    }

    @Test(description = "EDTRES-08 Resource admin is able to add subresources with valid document", priority = 1)
    public void resourceAdminIsAbleToAddSubresourceWithValidDocument() throws Exception {

        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        SubresourceModel subresourceModel = SubresourceModel.generatePublicSubResource();

        //add resource to group
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);

        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModel);
        createSubresourceDetailsDialog.tryClickOnSave();

        EditSubresourceDocumentPage editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModel);
        //validate document metadata
        soft.assertEquals(editSubresourceDocumentPage.getDocumentConfigurationSection().getDocumentPublishedVersion(), "1", "Published version is wrong");

        SubresourceDocumentPropertiesSection subresourceDocumentPropertiesSection = editSubresourceDocumentPage.getDocumentPropertiesSection();
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("document.name"), subresourceModel.getIdentifierValue(), "Document name is wrong!");
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("document.mimetype"), "text/xml", "Mime type value is wrong");
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("resource.identifier.value"), resourceModelOasis1.getIdentifierValue(), "Resource identifier value is wrong!");
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("resource.identifier.scheme"), resourceModelOasis1.getIdentifierScheme(), "Resource identifier scheme is wrong!");
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("subresource.identifier.value"), subresourceModel.getIdentifierValue(), "Subresource identifier value is wrong!");
        soft.assertEquals(subresourceDocumentPropertiesSection.getPropertyValue("subresource.identifier.scheme"), subresourceModel.getIdentifierScheme(), "Subresource identifier scheme is wrong!");

        //Validate is document is present and can be opened
        ResourcesPage resourcesPage = editSubresourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLSubResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme(), subresourceModel.getIdentifierValue());
        soft.assertNotNull(documentXML);
        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1.getIdentifierValue(), "EndpointURI value is wrong");
        soft.assertAll();

    }


    @Test(description = "EDTRES-11 Resource admin is able to delete subresource", priority = 1)
    public void resourceAdminsIsAbleToDeleteSubResource() throws Exception {

        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        SubresourceModel subresourceModel = SubresourceModel.generatePublicSubResource();

        //add resource to group
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);

        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModel);
        createSubresourceDetailsDialog.tryClickOnSave();
        editResourcePage.getSubresourceTab().deleteSubresouceDocument(subresourceModel);
        String deleteResourceAlert = editResourcePage.getAlertMessageAndClose();


        soft.assertEquals(deleteResourceAlert, "Subresource with scheme [" + subresourceModel.getIdentifierScheme() + "] and identifier: [" + subresourceModel.getIdentifierValue() + "] deleted.");
        soft.assertFalse(editResourcePage.getSubresourceTab().getGrid().isValuePresentInColumn("Identifier", subresourceModel.getIdentifierValue()), "Deleted subresource is stil in the grid.");
        ResourcesPage resourcesPage = editResourcePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        try {
            resourcesPage.openURLSubResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme(), subresourceModel.getIdentifierValue());
            soft.assertTrue(false);
        } catch (NoSuchElementException e) {
            soft.assertTrue(true);
        }
        soft.assertAll();

    }

    @Test(description = "EDTRES-15 - Resource Administrator can publish resource documents with approve status", priority = 1)
    public void resourceAdministratorsCanPublisResourceDocumentsWithApproveStatus() throws JsonProcessingException {

        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);


        //add resource to group
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);

        editResourcePage.goToTab("Resource details");
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.clickOnDocumentWizard().getExtensionAgencyIdnput().fill("NewVersion");
        new EditResourceDocumentWizardDialog(driver).clickOK();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getAlertArea().closeAlert();

        soft.assertTrue(editResourceDocumentPage.getRequestReviewBtn().isEnabled(), "Request review button is not enabled");
        soft.assertEquals("DRAFT", editResourceDocumentPage.getStatusValue());

        //Request review
        editResourceDocumentPage.getRequestReviewBtn().click();
        soft.assertEquals("UNDER_REVIEW", editResourceDocumentPage.getStatusValue());

        //Self approve
        soft.assertTrue(editResourceDocumentPage.getApproveBtn().isEnabled(), "Approve button is not enabled");
        editResourceDocumentPage.clickOnApproveAndConfirm();
        soft.assertEquals("APPROVED", editResourceDocumentPage.getStatusValue());

        soft.assertTrue(editResourceDocumentPage.getPublishBtn().isEnabled(), "Publish is not enabled");
        editResourceDocumentPage.clickOnPublishAndConfirm();
        soft.assertEquals("PUBLISHED", editResourceDocumentPage.getStatusValue());
        editResourceDocumentPage.selectVersion(1);
        soft.assertEquals("RETIRED", editResourceDocumentPage.getStatusValue());
        ResourcesPage resourcesPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme());
        soft.assertEquals(documentXML.getNodeValue("ExtensionAgencyID"), "NewVersion", "Document value is wrong");
        soft.assertAll();

    }

    @Test(description = "EDTRES-16 - Resource Administrator can publish SubResource documents with approve status", priority = 1)

    public void resourceAdministratorsCanPublishSUBResourceDocumentsWithApproveStatus() throws Exception {

        ResourceModel resourceModelOasis1 = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        SubresourceModel subresourceModel = SubresourceModel.generatePublicSubResource();

        //add resource to group
        resourceModelOasis1 = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1);

        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModel);
        createSubresourceDetailsDialog.tryClickOnSave();

        EditSubresourceDocumentPage editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModel);
        editSubresourceDocumentPage.getNewVersionBtn().click();
        SubresourceWizardDialog subresourceWizardDialog = editSubresourceDocumentPage.clickOnDocumentWizard();
        subresourceWizardDialog.processIdentifierInput().fill("123-123-123");
        subresourceWizardDialog.accessPointUrlInput().fill("www.domibustest.com");
        String path = FileUtils.getAbsoluteTruststorePath("validCertificate.cer");


        subresourceWizardDialog.uploadCertificateBtn(path);
        subresourceWizardDialog.clickOK();

        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.getAlertArea().closeAlert();

        soft.assertTrue(editSubresourceDocumentPage.getRequestReviewBtn().isEnabled(), "Request review button is not enabled");
        soft.assertEquals("DRAFT", editSubresourceDocumentPage.getStatusValue());

        //Request review
        editSubresourceDocumentPage.getRequestReviewBtn().click();
        soft.assertEquals("UNDER_REVIEW", editSubresourceDocumentPage.getStatusValue());

        //Self approve
        soft.assertTrue(editSubresourceDocumentPage.getApproveBtn().isEnabled(), "Approve button is not enabled");
        editSubresourceDocumentPage.clickOnApproveAndConfirm();
        soft.assertEquals("APPROVED", editSubresourceDocumentPage.getStatusValue());

        soft.assertTrue(editSubresourceDocumentPage.getPublishBtn().isEnabled(), "Publish is not enabled");
        editSubresourceDocumentPage.clickOnPublishAndConfirm();
        soft.assertEquals("PUBLISHED", editSubresourceDocumentPage.getStatusValue());
        editSubresourceDocumentPage.selectVersion(1);
        soft.assertEquals("RETIRED", editSubresourceDocumentPage.getStatusValue());
        ResourcesPage resourcesPage = editSubresourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLSubResouceDocument(resourceModelOasis1.getIdentifierValue(), resourceModelOasis1.getIdentifierScheme(), subresourceModel.getIdentifierValue());

        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1.getIdentifierValue(), "EndpointURI value is wrong");
        soft.assertEquals(documentXML.getNodeValue("DocumentIdentifier"), subresourceModel.getIdentifierValue(), "EndpointURI value is wrong");
        soft.assertEquals(documentXML.getNodeValue("EndpointURI"), "www.domibustest.com", "EndpointURI value is wrong");
        soft.assertAll();
    }

    @Test(description = "EDTRES-17 - Resource Administrator AND Resource viewer can approve documents under review", priority = 1)

    public void test() throws Exception {

        ResourceModel resource = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        //add resource to group
        resource = rest.resources().createResourceForGroup(domainModel, groupModel, resource);

        rest.resources().addMembersToResource(domainModel, groupModel, resource, adminMember);

        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resource);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        //Create new version under review for Resource viewer
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        //Reject review task from Review Document screen
        ReviewTasksPage reviewTasksPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);
        ReviewDocumentPage reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(resource.getIdentifierValue());
        soft.assertEquals(reviewDocumentPage.getStatusValue(), "UNDER_REVIEW", "Document does not have UNDER_REVIEW status");
        reviewDocumentPage.clickOnARejectAndConfirm();


        //  String documentid =  rest.resources().getDocumentID(resourceModel);

        //rest.startSessionWithUser(adminUser.getUsername(),TestRunData.getInstance().getNewPassword() );
        String documentid = rest.resources().getDocumentID(resourceModel);

        rest.resources().reviewRequest(resource, documentid, 2);
        reviewDocumentPage.refreshPage();


//        //Send document back to review
//        editResourcePage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_RESOURCES);
//        editResourcePage.selectDomain(domainModel, groupModel, resource);
//        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
//        //Select rejected version of document
//        editResourceDocumentPage.selectVersion(2);
//        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "REJECTED", "Document is not in the rejected status");
//        editResourceDocumentPage.getRequestReviewBtn().click();

        //Approve review task from Review Document screen
        reviewTasksPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);
        reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(resource.getIdentifierValue());

        reviewDocumentPage.clickOnApproveAndConfirm();


        soft.assertAll();

    }

    @Test(description = "EDTRES-19 - Resource Administrator with review rights are able to review documents inside the Edit Resource document screen", priority = 1)

    public void resourceAdministratorWithReviewRightsAreAbleToReviewDocumentsInsideTheEditResourceDocumentScreen() throws Exception {

        ResourceModel resource = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        //add resource to group
        resource = rest.resources().createResourceForGroup(domainModel, groupModel, resource);

        rest.resources().addMembersToResource(domainModel, groupModel, resource, adminMember);

        //Select resource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resource);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();

        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        soft.assertTrue(editResourceDocumentPage.getApproveBtn().isEnabled(), "Approve button is not enabled");
        soft.assertTrue(editResourceDocumentPage.getRejectBtn().isEnabled(), "Reject button is not enabled");

        editResourceDocumentPage.clickOnARejectAndConfirm();
        soft.assertTrue(editResourceDocumentPage.getApproveBtn().isDisabled(), "Approve button is enabled in REJECTED status.");
        soft.assertTrue(editResourceDocumentPage.getRejectBtn().isDisabled(), "Reject button is  enabled in REJECTED status.");

        editResourceDocumentPage.getRequestReviewBtn().click();
        editResourceDocumentPage.clickOnApproveAndConfirm();
        soft.assertTrue(editResourceDocumentPage.getApproveBtn().isDisabled(), "Approve button is enabled in APPROVED status.");
        soft.assertFalse(editResourceDocumentPage.getRejectBtn().isDisabled(), "Reject button is  enabled in APPROVED status.");

        editResourceDocumentPage.clickOnPublishAndConfirm();
        soft.assertAll();

    }

    @Test(description = "EDTRES-20- Resource Administrator with review rights are able to review documents inside the Edit Subresource document screen", priority = 1)

    public void resourceAdministratorWithReviewRightsAreAbleToReviewDocumentsInsideTheEditResourceSubDocumentScreen() throws Exception {

        ResourceModel resource = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        SubresourceModel subresourceModel = SubresourceModel.generatePublicSubResource();

        //add resource to group
        resource = rest.resources().createResourceForGroup(domainModel, groupModel, resource);

        rest.resources().addMembersToResource(domainModel, groupModel, resource, adminMember);

        //Select resource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resource);
        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModel);
        createSubresourceDetailsDialog.tryClickOnSave();

        EditSubresourceDocumentPage editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModel);
        editSubresourceDocumentPage.getNewVersionBtn().click();
        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.getRequestReviewBtn().click();
        soft.assertTrue(editSubresourceDocumentPage.getApproveBtn().isEnabled(), "Approve button is not enabled");
        soft.assertTrue(editSubresourceDocumentPage.getRejectBtn().isEnabled(), "Reject button is not enabled");

        editSubresourceDocumentPage.clickOnARejectAndConfirm();
        soft.assertTrue(editSubresourceDocumentPage.getApproveBtn().isDisabled(), "Approve button is enabled in REJECTED status.");
        soft.assertTrue(editSubresourceDocumentPage.getRejectBtn().isDisabled(), "Reject button is  enabled in REJECTED status.");

        editSubresourceDocumentPage.getRequestReviewBtn().click();
        editSubresourceDocumentPage.clickOnApproveAndConfirm();
        soft.assertTrue(editSubresourceDocumentPage.getApproveBtn().isDisabled(), "Approve button is enabled in APPROVED status.");
        soft.assertFalse(editSubresourceDocumentPage.getRejectBtn().isDisabled(), "Reject button is  enabled in APPROVED status.");

        editSubresourceDocumentPage.clickOnPublishAndConfirm();
        soft.assertAll();

    }


    @Test(description = "EDTRES-21- Resource Administrator can share a document as reference by clicking on the sharing enabled" +
            "EDTRES-24- Resource Administrator is able to see the refence document if the current document uses a reference", priority = 1)

    public void resourceAdministratorCanShareADocumentAsAReferenceByClickingOnSharingEnabled() throws Exception {

        ResourceModel resourceModelOasis1ToBeShared = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceModelOasis1UsesReference = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        //add resource to group
        resourceModelOasis1ToBeShared = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1ToBeShared);
        resourceModelOasis1UsesReference = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1UsesReference);

        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1ToBeShared, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1UsesReference, adminMember);

        //Select resource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1ToBeShared);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();

        //Add extension tag to highlight the reference
        ResourceDocumentEditor editor = editResourceDocumentPage.getEditor();
        editor.addExtensionTag();
        editor.addNewExtesionCode("referenceNode", "test", "referenceValue");
        editResourceDocumentPage.setDocumentValue(editor.printDoc());
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.clickOnPublishAndConfirm();
        editResourceDocumentPage.getDocumentConfigurationSection().enableSharing();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getBackBtn().click();

        //Select resource and document which will use as a reference the shared document
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1UsesReference);
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        SelectResourceDocumentDialog selectResourceDocumentDialog = editResourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        selectResourceDocumentDialog.selectResourceReferenceByResourceIdentifier(resourceModelOasis1ToBeShared.getIdentifierValue());
        editResourceDocumentPage.getSaveBtn().click();

        soft.assertEquals(editResourceDocumentPage.getDocumentConfigurationSection().getReferenceDocumentName(), resourceModelOasis1ToBeShared.getIdentifierValue(), "Wrong reference name");
        soft.assertTrue(editResourceDocumentPage.getViewDocumentSelect().getCurrentText().equals("Reference document"), "View document select current value is wrong");

        //Validate that opened document contains the reference
        ResourcesPage resourcesPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1UsesReference.getIdentifierValue(), resourceModelOasis1UsesReference.getIdentifierScheme());
        soft.assertNotNull(documentXML);
        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1UsesReference.getIdentifierValue(), "Open document does not contain the referenced document");
        soft.assertEquals(documentXML.getAttributeValueForNode("ParticipantIdentifier", "scheme"), resourceModelOasis1UsesReference.getIdentifierScheme(), "Open document does not contain the referenced document");

        soft.assertEquals(documentXML.getNodeValue("ext:referenceNode"), "referenceValue", "Open document does not contain the referenced document");


        soft.assertAll();

    }

    @Test(description = "EDTRES-25 The document using a shared reference sees only the published version of the reference", priority = 1)

    public void documentUsingASharedReferenceSeesOnlyThePublishedVersionOfTheReference() throws Exception {

        ResourceModel resourceModelOasis1ToBeShared = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        ResourceModel resourceModelOasis1UsesReference = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        //add resource to group
        resourceModelOasis1ToBeShared = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1ToBeShared);
        resourceModelOasis1UsesReference = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1UsesReference);

        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1ToBeShared, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1UsesReference, adminMember);

        //Select resource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1ToBeShared);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();

        //Add extension tag to highlight the reference
        ResourceDocumentEditor editor = editResourceDocumentPage.getEditor();
        editor.addExtensionTag();
        editor.addNewExtesionCode("referenceNode", "test", "referenceValue");
        editResourceDocumentPage.setDocumentValue(editor.printDoc());
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        editResourceDocumentPage.clickOnApproveAndConfirm();
        editResourceDocumentPage.clickOnPublishAndConfirm();
        editResourceDocumentPage.getDocumentConfigurationSection().enableSharing();
        editResourceDocumentPage.getSaveBtn().click();

        //create document in draft status
        editResourceDocumentPage.getNewVersionBtn().click();
        editor.addNewExtesionCode("draftReferenceNode", "test", "referenceValue");
        editResourceDocumentPage.getSaveBtn().click();


        //create document in approved status
        editResourceDocumentPage.getNewVersionBtn().click();
        editor.addNewExtesionCode("approvedReferenceNode", "test", "referenceValue");
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        editResourceDocumentPage.clickOnApproveAndConfirm();

        //create document in rejected status
        editResourceDocumentPage.getNewVersionBtn().click();
        editor.addNewExtesionCode("rejectedReferenceNode", "test", "referenceValue");
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        editResourceDocumentPage.clickOnARejectAndConfirm();

        editResourceDocumentPage.getBackBtn().click();

        //Select resource and document which will use as a reference the shared document
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1UsesReference);
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        SelectResourceDocumentDialog selectResourceDocumentDialog = editResourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        selectResourceDocumentDialog.selectResourceReferenceByResourceIdentifier(resourceModelOasis1ToBeShared.getIdentifierValue());
        editResourceDocumentPage.getSaveBtn().click();

        //Validate that opened document contains the reference
        ResourcesPage resourcesPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1UsesReference.getIdentifierValue(), resourceModelOasis1UsesReference.getIdentifierScheme());
        soft.assertEquals(documentXML.getNodeValue("ext:referenceNode"), "referenceValue", "Open document does not contain the referenced document");
        soft.assertEquals(documentXML.getNodeValue("ext:draftReferenceNode"), null, "Open document contains node from draft version of referenced document");
        soft.assertEquals(documentXML.getNodeValue("ext:approvedReferenceNode"), null, "Open document contains node from draft version of referenced document");
        soft.assertEquals(documentXML.getNodeValue("ext:rejectedReferenceNode"), null, "Open document contains node from draft version of referenced document");


        soft.assertAll();

    }

    @Test(description = "EDTRES-27- Resource administratoris not able to see shared documents for private resources", priority = 1)

    public void resourceAdministratorsAreNotAbleToSeeSharedDocumentsForPrivateResources() throws Exception {

        ResourceModel resouceModelPrivateToBeShared = ResourceModel.generatePrivateResource(ResourceTypes.OASIS1);
        ResourceModel resourceModelOasis1UsesReference = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        //add resource to group
        resouceModelPrivateToBeShared = rest.resources().createResourceForGroup(domainModel, groupModel, resouceModelPrivateToBeShared);
        resourceModelOasis1UsesReference = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1UsesReference);

        rest.resources().addMembersToResource(domainModel, groupModel, resouceModelPrivateToBeShared, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1UsesReference, adminMember);

        //Select resource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resouceModelPrivateToBeShared);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getDocumentConfigurationSection().enableSharing();
        editResourceDocumentPage.getSaveBtn().click();

        editResourceDocumentPage.getBackBtn().click();

        //Select resource and document which will use as a reference the shared document
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1UsesReference);
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        SelectResourceDocumentDialog selectResourceDocumentDialog = editResourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        soft.assertFalse(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resouceModelPrivateToBeShared.getIdentifierValue()), "Shared private resource is visible");

        // make current group private and resource public
        groupModel.setVisibility("PRIVATE");
        rest.domains().updateGroupForDomain(domainModel, groupModel);

        resouceModelPrivateToBeShared.setVisibility("PUBLIC");
        rest.resources().updateResource(domainModel, groupModel, resouceModelPrivateToBeShared);

        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resouceModelPrivateToBeShared.getIdentifierValue()), "Shared private resource is visible");

        soft.assertAll();

    }


}



