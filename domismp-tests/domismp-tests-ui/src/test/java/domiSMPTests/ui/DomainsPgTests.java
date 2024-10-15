package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.commonComponents.domanPropertyEditDialog.DomainPropertyEditDialog;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import ddsl.enums.ResponseCertificates;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.SmlPage;
import pages.administration.editDomainsPage.EditDomainsPage;
import pages.systemSettings.domainsPage.DomainsPage;
import rest.models.DomainModel;
import rest.models.MemberModel;
import rest.models.UserModel;
import utils.TestRunData;

import java.util.Arrays;
import java.util.List;

/**
 * This class has the tests against Domains Page
 */
//@Ignore("DomainsPgTests:beforeTest Failing tests: org.openqa.selenium.ElementClickInterceptedException: Element <select id=\"signatureKeyAlias_id\" " +
//        "class=\"mat-mdc-input-element mat-mdc-tooltip-trigger ng-tns-c1205077789-11 ng-untouched ng-pristine ng-valid " +
//        "mat-mdc-form-field-input-control mdc-text-field__input cdk-text-field-autofill-monitored cdk-focused cdk-program-focused\"> " +
//        "is not clickable at point (1014,364) because another element <mat-label class=\"ng-tns-c1205077789-11\"> obscures it" )
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

        domainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        soft.assertEquals(ResponseCertificates.getTextForAlias(domainModel.getSignatureKeyAlias()), domainsPage.getDomainTab().getResponseSignatureCertificateSelectedValue());
        soft.assertEquals(domainModel.getVisibility(), domainsPage.getDomainTab().getVisibilityOfDomainSelectedValue());
        soft.assertEquals(domainsPage.getDomainWarningMessage(), "To complete domain configuration, please:\n" +
                "select at least one resource type from the Resource Types tab\n" +
                "add a domain member with 'ADMIN' role from the Members tab!");
        soft.assertAll();
    }


    @Test(description = "DOM-02 System admin can integrates domain with SMP")
    public void systemAdminCanIntegrateDomainWithSMP() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
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
        domainsPage.getMembersTab().getInviteMemberBtn().click();
        domainsPage.getMembersTab().getInviteMembersPopup().selectMember(normalUser.getUsername(), "VIEWER");
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
        domainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
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
        domainsPage.getLeftSideGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
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
        domainsPage
                .getLeftSideGrid().searchAndClickElementInColumn("Domain code", domainModel.getDomainCode());
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

}
