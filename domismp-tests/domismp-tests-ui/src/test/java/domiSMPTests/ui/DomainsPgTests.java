package domiSMPTests.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import ddsl.DomiSMPPage;
import ddsl.dcomponents.commonComponents.domanPropertyEditDialog.DomainPropertyEditDialog;
import ddsl.dcomponents.commonComponents.members.InviteMembersPopup;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import ddsl.enums.ResponseCertificates;
import domiSMPTests.SeleniumTest;
import org.json.JSONArray;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.SmlPage;
import pages.administration.editDomainsPage.EditDomainsPage;
import pages.search.ResourcesPage;
import pages.systemSettings.domainsPage.DomainsPage;
import rest.models.*;
import utils.Generator;
import utils.TestRunData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_01;

/**
 * This class has the tests against Domains Page
 */

public class DomainsPgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    DomainsPage domainsPage;
    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
    }


    @Test(description = "DOM-01 System admin is able to create Domains")
    public void systemAdminIsAbleToCreateDomains() {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        soft.assertEquals(ResponseCertificates.getTextForAlias(domainModel.getSignatureKeyAlias()), domainsPage.getDomainTab().getResponseSignatureCertificateSelectedValue());
        soft.assertEquals(domainModel.getVisibility(), domainsPage.getDomainTab().getVisibilityOfDomainSelectedValue());
        soft.assertEquals(domainsPage.getDomainWarningMessage(), "To complete domain configuration, please:\n" +
                "select at least one resource type from the Resource Types tab\n" +
                "add a domain member with 'ADMIN' role from the Members tab!");
        soft.assertAll();
    }


    @Test(description = "DOM-02 System admin can integrates domain with SML")
    public void systemAdminCanIntegrateDomainWithSMP() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to SML!");

        //Go to SML
        driver.get(data.getSMLUrl());
        SmlPage smlPage = new SmlPage(driver);
        soft.assertTrue(smlPage.isDomainRegistered(domainModel), "Domain is not present in SML");
        soft.assertAll();

    }

    @Test(description = "DOM-03 System admin is able to Invite/Remove users from domains")
    public void systemAdminIsAbleToInviteRemoveUsersFromDomains() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();

        rest.users().createUser(normalUser);

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        //Invite user as VIEW and check if he has admin rights for domain
        domainsPage.goToTab("Members");
        InviteMembersPopup inviteMembersPopup = domainsPage.getMembersTab().clickOnInviteMemberBtn();
        inviteMembersPopup.selectMember(normalUser.getUsername(), "VIEWER");
        WebElement userMemberElement = domainsPage.getMembersTab().getMembersGrid().searchAndGetElementInColumn("Username", normalUser.getUsername());
        soft.assertNotNull(userMemberElement, "Invited user not found");

        //check if user has admin rights to domain as VIEWER
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        EditDomainsPage editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        WebElement domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("Members");
        domainsPage.getMembersTab().changeRoleOfUser(normalUser.getUsername(), "ADMIN");

        //check if user has admin rights to domain as Admin
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNotNull(domainElement, "Domain found for user which doesn't have rights");


        //Remove member user and check if he has access to the domain
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("Members");
        domainsPage.getMembersTab().removeUser(normalUser.getUsername());
        userMemberElement = domainsPage.getMembersTab().getMembersGrid().searchAndGetElementInColumn("Username", normalUser.getUsername());
        soft.assertNull(userMemberElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");

        soft.assertAll();

    }

    @Test(description = "DOM-04 System admin is not able to create duplicated Domains")
    public void systemAdminIsNotAbleToCreateDuplicatedDomains() {
        UserModel normalUser = UserModel.generateUserWithUSERrole();
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();

        rest.users().createUser(normalUser);

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Invalid domain data! Domain with code [" + domainModel.getDomainCode() + "] already exists!");
        soft.assertAll();
    }

    @Test(description = "DOM-05 System admin is able to delete domains without Resources")
    public void systemAdminIsAbleToDeleteDomainsWithoutResources() throws JsonProcessingException {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        domainsPage.refreshPage();
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.deleteandConfirm();
        soft.assertEquals(domainsPage.getAlertArea().getAlertMessage(), "Domain: [" + domainModel.getDomainCode() + "] is removed!", "Alert message is wrong");

        soft.assertFalse(domainsPage.IsDomainInGrid(domainModel.getDomainCode()));
        soft.assertAll();
    }

    @Test(description = "DOM-06 System admin is not able to delete domains with Resources")
    public void systemAdminIsNotAbleToDeleteDomainsWithResources() throws JsonProcessingException {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();
        GroupModel groupModel = GroupModel.generatePublicGroup();
        ResourceModel resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);

        //add resource to group
        rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);

        domainsPage.refreshPage();
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.deleteandConfirm();
        soft.assertEquals(domainsPage.getAlertArea().getAlertMessage(), "Can not delete domain because it has resources [1]! Delete resources first!", "Alert message is wrong");
        soft.assertTrue(domainsPage
                .getLeftSideGrid().isValuePresentInColumn("Domain code", domainModel.getDomainCode()));
        soft.assertAll();
    }

    @Test(description = "DOM-07 System admin can delete only unregister SML domains")
    public void systemAdminCanDeleteOnlyUnregisterSMLDomains() throws Exception {
        DomainModel domainModelGenerated = DomainModel.generatePublicDomainModelWithSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create domain
        DomainModel domainModel = rest.domains().createDomain(domainModelGenerated);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        domainsPage.refreshPage();

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to SML!");
        soft.assertFalse(domainsPage.getDeleteBtn().isEnabled(), "Delete button is enabled!");

        domainsPage.getSMLIntegrationTab().unregisterToSML();
        domainsPage.deleteandConfirm();
        soft.assertFalse(domainsPage.getLeftSideGrid().isValuePresentInColumn("Domain code", domainModel.getDomainCode()), "Deleted domain is still in the grid");
        soft.assertAll();
    }

    @Test(description = "DOM-08 System admin is able to change the visibility of a domain")
    public void systemAdminIsAbleToChangeTheVisibilityOfADomain() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();
        domainModel.setVisibility("PRIVATE");
        UserModel normalUserForAdminRole = UserModel.generateUserWithUSERrole();
        UserModel normalUserForViewerRole = UserModel.generateUserWithUSERrole();
        UserModel norightsUser = UserModel.generateUserWithUSERrole();

        GroupModel groupModel = GroupModel.generatePublicGroup();
        ResourceModel resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();

        MemberModel adminMember = new MemberModel() {
        };
        adminMember.setUsername(normalUserForAdminRole.getUsername());
        adminMember.setRoleType("ADMIN");
        adminMember.setHasPermissionReview(true);

        MemberModel viewMember = new MemberModel() {
        };
        viewMember.setUsername(normalUserForViewerRole.getUsername());
        viewMember.setRoleType("VIEWER");
        viewMember.setHasPermissionReview(true);

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create user
        String noRightsUserId = rest.users().createUser(norightsUser).getString("userId");
        String viewUserId = rest.users().createUser(normalUserForViewerRole).getString("userId");
        String adminUserId = rest.users().createUser(normalUserForAdminRole).getString("userId");

        //Set password for users
        rest.users().changePassword(noRightsUserId, data.getNewPassword());
        rest.users().changePassword(viewUserId, data.getNewPassword());
        rest.users().changePassword(adminUserId, data.getNewPassword());

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, viewMember);
        rest.domains().addMembersToDomain(domainModel, superMember);
        rest.domains().addMembersToDomain(domainModel, adminMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);

        //add users to groups
        rest.groups().addMembersToGroup(domainModel, groupModel, viewMember);

        //add resource to group
        resourceModel = rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);
        rest.resources().addMembersToResource(domainModel, groupModel, resourceModel, viewMember);

        //System is able to  see private resource with private domain
        ResourcesPage searchResourcesPage = domainsPage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "System admin is NOT able to see private resource with private domain");

        //Not logged user is not able to see  private resource with private domain.
        searchResourcesPage.logout();
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "Not logged user is able to see private resource with private domain");

        //Admin user with domain Admin rights can see private resource with private domain
        loginPage = homePage.goToLoginPage();
        loginPage.login(normalUserForViewerRole.getUsername(), data.getNewPassword());
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "Admin user with domain Admin rights can NOT see private resource with private domain");

        //normal user with domain Admin rights is able to see resource of private domain.
        searchResourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(normalUserForAdminRole.getUsername(), data.getNewPassword());
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "User with domain admin rights is NOT able to public resource of private domain");

        //user without rights is NOT able to see resource of private domain.
        searchResourcesPage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(norightsUser.getUsername(), data.getNewPassword());
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertFalse(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "User with no rights is able to see public resource of private domain");
        searchResourcesPage.logout();

        //Change the visibility of domain to PUBLIC
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.getDomainTab().changeVisibility("PUBLIC");
        domainsPage.getDomainTab().saveChanges();

        //User not logged in able to see public resource of public domain.
        searchResourcesPage.logout();
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "Not login user is able not to see public resource of public domain");

        //User with no rights  in able to see public resource of public domain.
        loginPage = homePage.goToLoginPage();
        loginPage.login(norightsUser.getUsername(), data.getNewPassword());
        searchResourcesPage = homePage.getSidebar().navigateTo(Pages.SEARCH_RESOURCES);
        soft.assertTrue(searchResourcesPage.isResourcePresent(resourceModel.getIdentifierValue(), resourceModel.getIdentifierScheme()), "User with no rights is NOT able to see public resource of public domain");
        searchResourcesPage.logout();

        soft.assertAll();
    }

    @Test(description = "DOM-09 System admin is able to modify the domain")
    public void systemAdminIsAbleToModifyTheDomain() throws Exception {
        DomainModel domainModelGenerated = DomainModel.generatePublicDomainModelWithSML();
        domainModelGenerated.setSignatureKeyAlias(SMP_DOMAIN_01.getAlias());

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        DomainModel domainModel = rest.domains().createDomain(domainModelGenerated);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        domainsPage.refreshPage();

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.getDomainTab().getResponseSigunatureCertificateDdl().selectByVisibleText("smp_domain_02 (CN=smp_domain_02,O=digit,C=eu:000000006443d987)");
        domainsPage.getDomainTab().getDefaultResourceTypeDdl().selectValue(ResourceTypes.OASIS2.getName());

        domainsPage.getDomainTab().saveChanges();

        soft.assertEquals(domainsPage.getDomainTab().getResponseSignatureCertificateSelectedValue(), "smp_domain_02 (CN=smp_domain_02,O=digit,C=eu:000000006443d987)", "Response Signature Certificate was not updated");
        soft.assertEquals(domainsPage.getDomainTab().getDefaultResourceTypeDdl().getCurrentValue(), "Oasis SMP 2.0 ServiceGroup (edelivery-oasis-smp-2.0-servicegroup)", "Default ResourceType was not updated");

        //Register domain to SML
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to SML!");


        String smlDomain = "AUTNew" + Generator.randomAlphabeticalValue(3);
        domainsPage.getSMLIntegrationTab().getSMLDomainInput().fill(smlDomain);
        domainsPage.getSMLIntegrationTab().saveChanges();
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLDomainInput().getText(), smlDomain, "SML Domain value was not updated.");

        //Update SML Client Certificate Alias with a value which is not registered to SML
        domainsPage.getSMLIntegrationTab().getSMLClientCertificateAliasDdl().selectByVisibleText("sample_key (CN=demo-smp-signing-key,O=digit,C=eu:000000006443f9bc)");
        domainsPage.getSMLIntegrationTab().saveChanges();
        String smlIntegrationError = domainsPage.getAlertMessageAndClose();
        soft.assertTrue(smlIntegrationError.startsWith("SML integration error! Error: UnauthorizedFault: [ERR-102]"), "No error appears when selecting a certificate which cannot be added to SML");

        //Update SML Client Certificate Alias with a value which is registered to SML
        domainsPage.getSMLIntegrationTab().getSMLClientCertificateAliasDdl().selectByVisibleText("smp_domain_01 (CN=smp_domain_01,O=digit,C=eu:000000006443d8a8)");
        domainsPage.getSMLIntegrationTab().saveChanges();
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLClientCertificateAliasDdl().getCurrentValue(), "smp_domain_01 (CN=smp_domain_01,O=digit,C=eu:000000006443d8a8)", "SML Domain value was not updated.");


        soft.assertAll();
    }

    @Test(description = "DOM-10 System admin is able to change the Resource Type only if it is not used by a resource")
    public void systemAdminIsAbleToChangeTheResourceTypeOnlyIfItIsNotUsedByAResource() throws Exception {
        DomainModel domainModelGenerated = DomainModel.generatePublicDomainModelWithSML();
        GroupModel groupModel = GroupModel.generatePublicGroup();
        ResourceModel resourceModel = ResourceModel.generatePublicResourceUnregisteredToSML();
        resourceModel.setResourceTypeIdentifier(ResourceTypes.OASIS3.getName());

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        DomainModel domainModel = rest.domains().createDomain(domainModelGenerated);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        //create group for domain
        groupModel = rest.domains().createGroupForDomain(domainModel, groupModel);


        //add resource to group
        rest.resources().createResourceForGroup(domainModel, groupModel, resourceModel);
        domainsPage.refreshPage();

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("Resource Types");
        domainsPage.getResourceTypesTab().checkResource("edelivery-oasis-smp-1.0-servicegroup (smp-1)", false);
        domainsPage.getResourceTypesTab().saveChanges();
        soft.assertFalse(domainsPage.getResourceTypesTab().getResourceTypeStatus("edelivery-oasis-smp-1.0-servicegroup (smp-1)"));

        domainsPage.getResourceTypesTab().checkResource("edelivery-oasis-cppa-3.0-cpp (cpp)", false);
        domainsPage.getResourceTypesTab().saveChanges();
        soft.assertEquals(domainsPage.getAlertMessageAndClose(), "Can not remove resource definition [edelivery-oasis-cppa-3.0-cpp] from domain [" + domainModel.getDomainCode() + "], because it has resources. Resource count [1]!");

        soft.assertAll();
    }

    @Test(description = "DOM-12 - User is able to change SML domain for registered domains")
    public void userIsAbleToChangeSMLDomainForRegisteredDomains() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to SML!");
        String newDomainSMLValue = domainModel.getSmlSubdomain() + "2";
        domainsPage.getSMLIntegrationTab().getSMLDomainInput().fill(newDomainSMLValue);
        soft.assertTrue(domainsPage.getSMLIntegrationTab().getSaveButton().isEnabled(), "Save button is not enabled after modifying SML domain value");
        domainsPage.getSMLIntegrationTab().saveChanges();

        soft.assertFalse(domainsPage.getSMLIntegrationTab().getSaveButton().isEnabled(), "Saving SML domain value failed");
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLDomainInput().getText(), newDomainSMLValue, "SML Domain value is not changed");

        soft.assertAll();

    }

    @Test(description = "DOM-15 - User tries to add invalid SML SMP identifier and receives error")
    public void systemAdminTriesToAddInvalidSMLSMPIdentifierAndReceivesError() throws Exception {
        DomainModel domainModelGenerated = DomainModel.generatePublicDomainModelWithSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create domain
        DomainModel domainModel = rest.domains().createDomain(domainModelGenerated);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        domainsPage.refreshPage();

        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainModelGenerated.setSmlSmpId("43ASDASDASD");
        String smlsmpIdentifierError = "SML SMP ID should be up to 63 characters long, should only contain alphanumeric and hyphen characters, should not start with a digit nor a hyphen and should not end with a hyphen.";

        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLSMPErrorMessage(), smlsmpIdentifierError, "Validation error does not appear when identifier starts with number");

        domainModelGenerated.setSmlSmpId("-ASDASDASD");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLSMPErrorMessage(), smlsmpIdentifierError, "Validation error does not appear when identifier starts with hyphen");

        domainModelGenerated.setSmlSmpId("ASDASDASD-");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        soft.assertEquals(domainsPage.getSMLIntegrationTab().getSMLSMPErrorMessage(), smlsmpIdentifierError, "Validation error does not appear when identifier ends with hyphen");
        String longIdentifierValue = Generator.randomAlphabeticalValue(64);
        domainModelGenerated.setSmlSmpId(longIdentifierValue);
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModelGenerated);
        soft.assertNotEquals(domainsPage.getSMLIntegrationTab().getsmlsmpIdentifierInput().getText(), longIdentifierValue);

        soft.assertAll();
    }
    @Test(description = "DOM-16 - SML Cert Alias dropdown field contains all keys from the keystore")
    public void smlCertAliasDowndownContainsAllKeysFromKeystore() throws Exception {
        DomainModel domainModelGenerated = DomainModel.generatePublicDomainModelWithSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");


        //create domain
        DomainModel domainModel = rest.domains().createDomain(domainModelGenerated);

        //add users to domain
        rest.domains().addMembersToDomain(domainModel, superMember);
        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        domainsPage.refreshPage();
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        ArrayList<String> listOfCertificates = domainsPage.getSMLIntegrationTab().getSMLClientCertificateAliasDdl().getAllOptionValues();
        ArrayList<String> listOfKeystore = new ArrayList<>();
        JSONArray keystoreResponse = rest.keystoreClient().getAllKeystores();

        for (int i = 0; i < keystoreResponse.length(); i++) {
            listOfKeystore.add(rest.keystoreClient().getAllKeystores().getJSONObject(i).get("alias").toString());
        }
        soft.assertEquals(listOfKeystore, listOfCertificates);
        soft.assertAll();
    }
    @Test(description = "DOM-19 - Domain admins are able to change default properties for domains")
    public void systemAdminsAreAbleToChangeDefaultPropertiesForDomains() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        MemberModel superMember = new MemberModel();
        superMember.setUsername(TestRunData.getInstance().getAdminUsername());
        superMember.setRoleType("ADMIN");

        //create domain
        domainModel = rest.domains().createDomain(domainModel);

        //  rest.domains().addMembersToDomain(domainModel, adminMember);
        rest.domains().addMembersToDomain(domainModel, superMember);

        //add resources to domain
        List<ResourceTypes> resourcesToBeAdded = Arrays.asList(ResourceTypes.OASIS1, ResourceTypes.OASIS3, ResourceTypes.OASIS2);
        domainModel = rest.domains().addResourcesToDomain(domainModel, resourcesToBeAdded);

        domainsPage.refreshPage();
        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("Configuration");

        //Check is modifying boolean values
        String boolPropertyName = "identifiersBehaviour.scheme.mandatory";
        DomainPropertyEditDialog domainPropertyEditDialog = domainsPage.getConfigurationTab().openProperty(boolPropertyName);
        domainPropertyEditDialog.setDomainValue(false);
        domainPropertyEditDialog.pressOk();
        domainsPage.getConfigurationTab().saveChanges();

        //verify changes
        soft.assertFalse(domainsPage.getConfigurationTab().isSystemValueUsed(boolPropertyName), "Property is marked as it's using system value");
        soft.assertEquals("false", domainsPage.getConfigurationTab().getCurrentPropertyValue(boolPropertyName));


        //Verify disabling system property
        String useDomainProperty = "identifiersBehaviour.ParticipantIdentifierScheme.validationRegex";
        domainPropertyEditDialog = domainsPage.getConfigurationTab().openProperty(useDomainProperty);
        domainPropertyEditDialog.disableSystemValue();
        domainPropertyEditDialog.pressOk();
        domainsPage.getConfigurationTab().saveChanges();
        //verify changes
        soft.assertFalse(domainsPage.getConfigurationTab().isSystemValueUsed(useDomainProperty), "Property is marked as it's using system value");

        //Verify change to enabling system property
        domainPropertyEditDialog = domainsPage.getConfigurationTab().openProperty(useDomainProperty);
        domainPropertyEditDialog.enableSystemValue();
        domainPropertyEditDialog.pressOk();
        domainsPage.getConfigurationTab().saveChanges();
        //verify changes
        soft.assertTrue(domainsPage.getConfigurationTab().isSystemValueUsed(useDomainProperty));

        // String property value
        String stringProperty = "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes";
        String defaultPropertyValue = domainsPage.getConfigurationTab().getCurrentPropertyValue(stringProperty);

        domainPropertyEditDialog = domainsPage.getConfigurationTab().openProperty(stringProperty);
        domainPropertyEditDialog.setDomainValue("${identifier}${identifier}");
        domainPropertyEditDialog.pressOk();
        domainsPage.getConfigurationTab().saveChanges();

        soft.assertFalse(domainsPage.getConfigurationTab().isSystemValueUsed(stringProperty), "Property is marked as it's using system value");
        soft.assertTrue(domainsPage.getConfigurationTab().getCurrentPropertyValue(stringProperty).equalsIgnoreCase("${identifier}${identifier}"), "Configuration table is not showing updated value");

        //Check if the property value is updated with system value after use system value is enabled
        domainPropertyEditDialog = domainsPage.getConfigurationTab().openProperty(stringProperty);
        domainPropertyEditDialog.enableSystemValue();
        domainPropertyEditDialog.pressOk();
        domainsPage.getConfigurationTab().saveChanges();
        soft.assertTrue(domainsPage.getConfigurationTab().getCurrentPropertyValue(stringProperty).equalsIgnoreCase(defaultPropertyValue), "Configuration table is not showing system value");

        soft.assertAll();
    }

    @Test(description = "DOM-31 - Unregister button is active if SMP ID is registered (and BDSML integration is enabled)")
    public void unregisteredButtonIsActiveIfSMPIDIsRegistered() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");


        domainsPage.filterAndSelectDomain(domainModel.getDomainCode());
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to SML!");

        soft.assertTrue(domainsPage.getSMLIntegrationTab().getUnregisterButton().isEnabled());
        domainsPage.getSMLIntegrationTab().unregisterToSML();
        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] unregistered from SML!");

        //Go to SML
        driver.get(data.getSMLUrl());
        SmlPage smlPage = new SmlPage(driver);
        soft.assertFalse(smlPage.isDomainRegistered(domainModel), "Unregistered Domain is present in SML");
        soft.assertAll();


        soft.assertAll();

    }

    @Test(description = "DOM-32- User tries to create domain with more than 63 characters in domain code field and receives error")
    public void userTriesToCreateDomainWithMoreThan63CharactersInDomainCodeFieldReceivesWarningAndValueTruncated() {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();
        String domainCodeLong = Generator.randomAlphaNumericValue(64);
        domainModel.setDomainCode(domainCodeLong);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        soft.assertEquals(domainsPage.getDomainTab().getDomainCodeValidationMessage(), "Domain code must contain only chars and numbers and must be less than 63 chars long.");
        soft.assertTrue(domainsPage.getDomainTab().getDomainCodeInput().getText().length() == (63));

        soft.assertAll();
    }

    @Test(description = "DOM-33 - User tries to create domain with non alphanumeric characters in domain code field and receives error")
    public void userTriesToCreateDomainWithNonAlphanumericCharactersInDomainCodeAndReceivesError() {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();
        String domainCodeLong = Generator.randomAlphaNumericValue(10) + "( ) ` ~ ! @ # $ % ^ & * - + = | \\ { } [ ] : ; \" ' < > , . ? / _";
        domainModel.setDomainCode(domainCodeLong);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        soft.assertEquals(domainsPage.getDomainTab().getDomainCodeValidationMessage(), "Domain code must contain only chars and numbers and must be less than 63 chars long.",
                "Wrong warning message when DomainCode is longer than 63 characters");
        soft.assertFalse(domainsPage.getDomainTab().getSaveBtn().isEnabled(),
                "Save button is not disabled when Domain code contains non alphanumeric values1");

        soft.assertAll();
    }

}
