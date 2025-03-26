package domiSMPTests.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
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
import pages.administration.editResourcesPage.editSubresourceDocumentPage.SelectSubResourceDocumentDialog;
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
    MemberModel superMember;
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

        superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");
        superMember.setHasPermissionReview(true);

        //create user
        rest.users().createUser(adminUser);

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
    public void resourceAdministratorsAndResourceViewerCanApproveDocumentsUnderReview() throws Exception {
        //create data
        ResourceModel currentResourceWithReview = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        UserModel currentSimpleUser = UserModel.generateUserWithUSERrole();

        MemberModel resourceViewMemberWithReview = new MemberModel() {
        };
        resourceViewMemberWithReview.setUsername(currentSimpleUser.getUsername());
        resourceViewMemberWithReview.setRoleType("VIEWER");
        resourceViewMemberWithReview.setHasPermissionReview(true);

        rest.users().createUser(currentSimpleUser);

        //add currentResourceWithReview to group
        currentResourceWithReview = rest.resources().createResourceForGroup(domainModel, groupModel, currentResourceWithReview);

        rest.resources().addMembersToResource(domainModel, groupModel, currentResourceWithReview, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, currentResourceWithReview, resourceViewMemberWithReview);

        //Create new version under review for Resource administrator
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, currentResourceWithReview);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        //Create new version under review for Resource viewer
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        ////Validate Resource administrator can approve/reject
        //Resource administrator - Reject review task from Review Document screen
        ReviewTasksPage reviewTasksPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);
        ReviewDocumentPage reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(currentResourceWithReview.getIdentifierValue(), 2);
        soft.assertEquals(reviewDocumentPage.getStatusValue(), "UNDER_REVIEW", "Document does not have UNDER_REVIEW status");
        reviewDocumentPage.clickOnARejectAndConfirm();

        String documentid = rest.resources().getDocumentID(currentResourceWithReview);

        rest.resources().reviewRequest(currentResourceWithReview, documentid, 2);
        reviewDocumentPage.refreshPage();

        //Resource administrator - Approve review task from Review Document screen
        reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(currentResourceWithReview.getIdentifierValue(), 2);

        reviewDocumentPage.clickOnApproveAndConfirm();
        soft.assertFalse(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 2));
        //Validate is Viewer user can approve/reject tasks
        reviewDocumentPage.logout();
        homePage.goToLoginPage().login(currentSimpleUser.getUsername(), TestRunData.getInstance().getNewPassword());
        homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);

        //Resource viewer - Reject review task from Review Document screen
        reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(currentResourceWithReview.getIdentifierValue(), 3);
        soft.assertEquals(reviewDocumentPage.getStatusValue(), "UNDER_REVIEW", "Document does not have UNDER_REVIEW status");
        reviewDocumentPage.clickOnARejectAndConfirm();

        rest.resources().reviewRequest(currentResourceWithReview, documentid, 3);

        reviewDocumentPage.refreshPage();
        //Resource viewer - Approve review task from Review Document screen
        reviewDocumentPage = reviewTasksPage.getGrid().openDocumentTask(currentResourceWithReview.getIdentifierValue(), 3);
        reviewDocumentPage.clickOnApproveAndConfirm();
        soft.assertFalse(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 3));

        soft.assertAll();

    }

    @Test(description = "EDTRES-18 - Resource Administrator AND Resource viewer are seeing/not seeing review tasks  based on changes of resource review status", priority = 1)
    public void resourceAdministratorsAndResourceViewersAreSeeingReviewTasksBasedOnChangesOfResourceReviewStatus() throws Exception {
        //create data
        ResourceModel currentResourceWithReview = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);
        UserModel currentSimpleUser = UserModel.generateUserWithUSERrole();

        MemberModel resourceViewMemberWithReview = new MemberModel() {
        };
        resourceViewMemberWithReview.setUsername(currentSimpleUser.getUsername());
        resourceViewMemberWithReview.setRoleType("VIEWER");
        resourceViewMemberWithReview.setHasPermissionReview(true);

        rest.users().createUser(currentSimpleUser);

        //add currentResourceWithReview to group
        currentResourceWithReview = rest.resources().createResourceForGroup(domainModel, groupModel, currentResourceWithReview);

        rest.resources().addMembersToResource(domainModel, groupModel, currentResourceWithReview, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, currentResourceWithReview, resourceViewMemberWithReview);

        //Create new version under review for Resource administrator
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, currentResourceWithReview);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();
        //Create new version under review for Resource viewer
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        //Resource administrator - Can see tasks
        ReviewTasksPage reviewTasksPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);

        soft.assertTrue(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 2), "Resource administrators cannot see tasks for resource with review enabled!");

        reviewTasksPage.logout();

        //Resource viewer - Can see tasks
        homePage.goToLoginPage().login(currentSimpleUser.getUsername(), TestRunData.getInstance().getNewPassword());
        reviewTasksPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);
        soft.assertTrue(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 3), "Resource viewers cannot see tasks for resource with review enabled!");

        //Disable review for resource with tasks
        currentResourceWithReview.setReviewEnabled(false);
        rest.resources().updateResource(domainModel, groupModel, currentResourceWithReview);

        //Resource viewer - Cannot see task after review was disabled.
        reviewTasksPage.refreshPage();
        soft.assertFalse(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 3), "Resource viewers can see tasks for resource with review disabled!");

        reviewTasksPage.logout();

        //Resource administrators - Cannot see task after review was disabled.
        homePage.goToLoginPage().login(adminMember.getUsername(), TestRunData.getInstance().getNewPassword());
        reviewTasksPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_REVIEW_TASKS);
        soft.assertFalse(reviewTasksPage.getGrid().isDocumentTaskPresent(currentResourceWithReview.getIdentifierValue(), 2), "Resource administrators can see tasks for resource with review disabled!");

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

    @Test(description = "EDTRES-26- Resource administrator is able to remove the sharing option for a document which is already used by other documents", priority = 1)
    public void resourceAdministratorIsAbleToRemoveTheSharingOptionForADocumentWhichIsAlreadyUsedByOtherDocuments_RESOURCE() throws Exception {

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
        editResourceDocumentPage.getBackBtn().click();

        //Remove sharing reference option
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1ToBeShared);
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getDocumentConfigurationSection().disableSharing();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getBackBtn().click();

        //Validate that opened document is not containing the reference anymore
        ResourcesPage resourcesPage = editResourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLResouceDocument(resourceModelOasis1UsesReference.getIdentifierValue(), resourceModelOasis1UsesReference.getIdentifierScheme());
        soft.assertNotNull(documentXML);
        soft.assertEquals(documentXML.getNodeValue("ParticipantIdentifier"), resourceModelOasis1UsesReference.getIdentifierValue(), "Open document contains the referenced document");
        soft.assertEquals(documentXML.getAttributeValueForNode("ParticipantIdentifier", "scheme"), resourceModelOasis1UsesReference.getIdentifierScheme(), "Open document contains the referenced document");
        soft.assertNull(documentXML.getNodeValue("ext:referenceNode"), "Open document contains the referenced document");

        soft.assertAll();
    }

    //Failed due to bug
    @Test(description = "EDTRES-26- Resource administrator is able to remove the sharing option for a document which is already used by other documents", priority = 1)
    public void resourceAdministratorIsAbleToRemoveTheSharingOptionForADocumentWhichIsAlreadyUsedByOtherDocuments_SUBRESOURCE() throws Exception {

        ResourceModel resourceModelOasis1ToBeShared = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        SubresourceModel subresourceModelToBeShared = SubresourceModel.generatePublicSubResource();

        ResourceModel resourceModelOasis1UsesReference = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        SubresourceModel subresourceModelToBeUsesReference = SubresourceModel.generatePublicSubResource();

        //add resource to group
        resourceModelOasis1ToBeShared = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1ToBeShared);
        resourceModelOasis1UsesReference = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModelOasis1UsesReference);

        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1ToBeShared, adminMember);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModelOasis1UsesReference, adminMember);

        //Create and Select subresource and document to be shared
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1ToBeShared);
        editResourcePage.goToTab("Subresources");
        CreateSubresourceDetailsDialog createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModelToBeShared);
        createSubresourceDetailsDialog.tryClickOnSave();

        EditSubresourceDocumentPage editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModelToBeShared);
        editSubresourceDocumentPage.getNewVersionBtn().click();
        ResourceDocumentEditor editor = editSubresourceDocumentPage.getEditor();
        editor.setContextValueForNode("EndpointURI", "www.test.test");
        editSubresourceDocumentPage.setDocumentValue(editor.printDoc());
        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.clickOnPublishAndConfirm();
        editSubresourceDocumentPage.getDocumentConfigurationSection().enableSharing();
        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.getBackBtn().click();


        //Create and Select subresource and document which will use as a reference the shared document
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1UsesReference);
        editResourcePage.goToTab("Subresources");
        createSubresourceDetailsDialog = editResourcePage.getSubresourceTab().createSubresource();
        createSubresourceDetailsDialog.fillResourceDetails(subresourceModelToBeUsesReference);
        createSubresourceDetailsDialog.tryClickOnSave();

        editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModelToBeUsesReference);
        SelectSubResourceDocumentDialog selectResourceDocumentDialog = editSubresourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        selectResourceDocumentDialog.selectSubResourceReferenceBySubResourceIdentifier(subresourceModelToBeShared.getIdentifierValue());
        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.getBackBtn().click();

        //Remove sharing reference option
        editResourcePage.selectDomain(domainModel, groupModel, resourceModelOasis1ToBeShared);
        editResourcePage.goToTab("Subresources");
        editSubresourceDocumentPage = editResourcePage.getSubresourceTab().editSubresouceDocument(subresourceModelToBeShared);
        editSubresourceDocumentPage.getDocumentConfigurationSection().disableSharing();
        editSubresourceDocumentPage.getSaveBtn().click();
        editSubresourceDocumentPage.getBackBtn().click();

        //Validate that opened document is not containing the reference anymore
        ResourcesPage resourcesPage = editSubresourceDocumentPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        XMLUtils documentXML = resourcesPage.openURLSubResouceDocument(resourceModelOasis1UsesReference.getIdentifierValue(), resourceModelOasis1UsesReference.getIdentifierScheme(), subresourceModelToBeUsesReference.getIdentifierValue());
        soft.assertNotNull(documentXML);
        soft.assertEquals(documentXML.getNodeValue("DocumentIdentifier"), subresourceModelToBeUsesReference.getIdentifierValue(), "Open document contains the referenced document");
        soft.assertEquals(documentXML.getAttributeValueForNode("DocumentIdentifier", "scheme"), subresourceModelToBeUsesReference.getIdentifierScheme(), "Open document contains the referenced document");
        soft.assertEquals(documentXML.getNodeValue("EndpointURI"), "https://mypage.eu", "Open document contains the referenced document");

        soft.assertAll();
    }

    @Test(description = "EDTRES-27- Resource administrators not able to see shared documents for private resources", priority = 1)
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

    @Test(description = "EDTRES-28- Resource administrators is able to see public resources from public groups or the same group as the document")
    public void resourceAdministratorIsAbleToSeePublicResourceFromPublicGroupsOrTheSameGroupAsTheDocument() throws Exception {

        DomainModel domainPublic_A = DomainModel.generatePublicDomainModelWithoutSML();
        DomainModel domainPublic_B = DomainModel.generatePublicDomainModelWithoutSML();

        GroupModel group_A1 = GroupModel.generatePublicGroup();
        GroupModel group_A2 = GroupModel.generatePublicGroup();
        GroupModel group_B = GroupModel.generatePublicGroup();

        //Generating resources for Public resource Private group Public Domain
        ResourceModel resourceA_A1_underTest = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceA_A1 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceA_A2 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceB = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);


        //generating member models
        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        domainPublic_A = rest.domains().createDomain(domainPublic_A);
        domainPublic_B = rest.domains().createDomain(domainPublic_B);


        //add users to domains
        rest.domains().addMembersToDomain(domainPublic_A, superMember);
        rest.domains().addMembersToDomain(domainPublic_B, superMember);


        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPublic_A = rest.domains().addResourcesToDomain(domainPublic_A, resourcesTypesToBeAdded);
        domainPublic_B = rest.domains().addResourcesToDomain(domainPublic_B, resourcesTypesToBeAdded);


        //create groups for domains
        group_A1 = rest.domains().createGroupForDomain(domainPublic_A, group_A1);
        group_A2 = rest.domains().createGroupForDomain(domainPublic_A, group_A2);
        group_B = rest.domains().createGroupForDomain(domainPublic_B, group_B);


        //add resources to groups
        resourceA_A1_underTest = rest.resources().createResourceForGroup(domainPublic_A, group_A1, resourceA_A1_underTest);
        resourceA_A1 = rest.resources().createResourceForGroup(domainPublic_A, group_A1, resourceA_A1);
        resourceA_A2 = rest.resources().createResourceForGroup(domainPublic_A, group_A2, resourceA_A2);
        resourceB = rest.resources().createResourceForGroup(domainPublic_B, group_B, resourceB);

        //Enable sharing for resources
        rest.resources().enableSharingForResourceDocument(resourceA_A1, true);
        rest.resources().enableSharingForResourceDocument(resourceA_A2, true);
        rest.resources().enableSharingForResourceDocument(resourceB, true);

        //Add resources member to resource
        rest.resources().addMembersToResource(domainPublic_A, group_A1, resourceA_A1_underTest, adminMember);
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainPublic_A, group_A1, resourceA_A1_underTest);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        SelectResourceDocumentDialog selectResourceDocumentDialog = editResourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceA_A1.getIdentifierValue()), "Shared public resource of same group is not visible");
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceA_A2.getIdentifierValue()), "Shared public resource of different group of same domain is not visible");
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceB.getIdentifierValue()), "Shared public resource of different domain is not visible");


        soft.assertAll();
    }

    @Test(description = "EDTRES-29- Resource administrators is able to see public resources from public domains or the same domain as the document")
    public void resourceAdministratorIsAbleToSeePublicResourcesFromPublicDomainsOrTheSameDomainAsTheDocument() throws Exception {

        DomainModel domainPublic_A = DomainModel.generatePublicDomainModelWithoutSML();
        DomainModel domainPublic_B = DomainModel.generatePublicDomainModelWithoutSML();

        GroupModel group_A1 = GroupModel.generatePublicGroup();
        GroupModel group_A2 = GroupModel.generatePublicGroup();

        GroupModel group_A3 = GroupModel.generatePublicGroup();

        GroupModel group_B = GroupModel.generatePublicGroup();

        //Generating resources for Public resource Private group Public Domain
        ResourceModel resourceA_A1_underTest = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceA_A1 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceA_A2 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceA_A3 = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);
        ResourceModel resourceB = ResourceModel.generatePublicResource(ResourceTypes.OASIS1);


        //generating member models
        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        domainPublic_A = rest.domains().createDomain(domainPublic_A);
        domainPublic_B = rest.domains().createDomain(domainPublic_B);


        //add users to domains
        rest.domains().addMembersToDomain(domainPublic_A, superMember);
        rest.domains().addMembersToDomain(domainPublic_B, superMember);


        //add resource types to domains
        List<ResourceTypes> resourcesTypesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainPublic_A = rest.domains().addResourcesToDomain(domainPublic_A, resourcesTypesToBeAdded);
        domainPublic_B = rest.domains().addResourcesToDomain(domainPublic_B, resourcesTypesToBeAdded);


        //create groups for domains
        group_A1 = rest.domains().createGroupForDomain(domainPublic_A, group_A1);
        group_A2 = rest.domains().createGroupForDomain(domainPublic_A, group_A2);
        group_A3 = rest.domains().createGroupForDomain(domainPublic_A, group_A3);

        group_B = rest.domains().createGroupForDomain(domainPublic_B, group_B);


        //add resources to groups
        resourceA_A1_underTest = rest.resources().createResourceForGroup(domainPublic_A, group_A1, resourceA_A1_underTest);
        resourceA_A1 = rest.resources().createResourceForGroup(domainPublic_A, group_A1, resourceA_A1);
        resourceA_A2 = rest.resources().createResourceForGroup(domainPublic_A, group_A2, resourceA_A2);
        resourceA_A3 = rest.resources().createResourceForGroup(domainPublic_A, group_A3, resourceA_A3);

        resourceB = rest.resources().createResourceForGroup(domainPublic_B, group_B, resourceB);

        //Enable sharing for resources
        rest.resources().enableSharingForResourceDocument(resourceA_A1, true);
        rest.resources().enableSharingForResourceDocument(resourceA_A2, true);
        rest.resources().enableSharingForResourceDocument(resourceA_A3, true);
        rest.resources().enableSharingForResourceDocument(resourceB, true);

        //Add resources member to resource
        rest.resources().addMembersToResource(domainPublic_A, group_A1, resourceA_A1_underTest, adminMember);
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainPublic_A, group_A1, resourceA_A1_underTest);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        SelectResourceDocumentDialog selectResourceDocumentDialog = editResourceDocumentPage.getDocumentConfigurationSection().clickOnSelectReferenceBtn();
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceA_A1.getIdentifierValue()), "Shared public resource of same group is not visible");
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceA_A2.getIdentifierValue()), "Shared public resource of different group of same domain is not visible");
        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceA_A3.getIdentifierValue()), "Shared public resource of private group of same domain is not visible");

        soft.assertTrue(selectResourceDocumentDialog.isResourceReferenceByResourceIdentifierPresent(resourceB.getIdentifierValue()), "Shared public resource of different domain is not visible");


        soft.assertAll();
    }


    @Test(description = "EDTRES-30- Resource Administrator are able to disable review process for resource which has documents in all states", priority = 1)
    public void resourceAdministratorsAreAbleToDsiableReviewProcessForResourceWhichHasDocumentsInAllStates() throws Exception {
        //create data
        ResourceModel currentResourceWithReview = ResourceModel.generatePublicResourceWithReview(ResourceTypes.OASIS1);

        //add currentResourceWithReview to group
        currentResourceWithReview = rest.resources().createResourceForGroup(domainModel, groupModel, currentResourceWithReview);

        rest.resources().addMembersToResource(domainModel, groupModel, currentResourceWithReview, adminMember);

        //Added review permission to system because the restapi calls are made with system user
        superMember.setHasPermissionReview(true);

        //Update system user to have review rights
        rest.resources().updateMemberOfResource(domainModel, groupModel, currentResourceWithReview, superMember);


        String documentid = rest.resources().getDocumentID(currentResourceWithReview);

        //Create new version for under review(version 2)
        editResourcePage.refreshPage();
        editResourcePage.selectDomain(domainModel, groupModel, currentResourceWithReview);
        EditResourceDocumentPage editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        //Create new version for rejected (version 3)
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        rest.resources().rejectReview(currentResourceWithReview, documentid, 3);

        //Create new version for approved (version 4)
        editResourceDocumentPage.getNewVersionBtn().click();
        editResourceDocumentPage.getSaveBtn().click();
        editResourceDocumentPage.getRequestReviewBtn().click();

        rest.resources().approveReview(currentResourceWithReview, documentid, 4);

        editResourceDocumentPage.selectVersion(2);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "UNDER_REVIEW");

        editResourceDocumentPage.selectVersion(3);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "REJECTED");

        editResourceDocumentPage.selectVersion(4);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "APPROVED");

        editResourceDocumentPage.getBackBtn().click();
        editResourcePage.selectDomain(domainModel, groupModel, currentResourceWithReview);
        editResourcePage.getResourceDetailsTab().getReviewProcessEnabledCheckbox().uncheck();
        new ConfirmationDialog(driver).confirm();
        soft.assertEquals(editResourcePage.getResourceDetailsTab().getReviewProcessWarning(), "All document versions with review statuses (UNDER_REVIEW, APPROVED, REJECTED) will be set to DRAFT Do you want to continue?");

        editResourcePage.getResourceDetailsTab().getSaveBtn().click();
        editResourceDocumentPage = editResourcePage.getResourceDetailsTab().clickOnEditDocument();

        //Check if version have status draft
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "PUBLISHED");

        editResourceDocumentPage.selectVersion(2);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "DRAFT");

        editResourceDocumentPage.selectVersion(3);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "DRAFT");

        editResourceDocumentPage.selectVersion(4);
        soft.assertEquals(editResourceDocumentPage.getStatusValue(), "DRAFT");
        soft.assertAll();

    }


}



