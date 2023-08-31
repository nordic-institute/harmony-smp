package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.SmlPage;
import pages.administration.EditDomainsPage;
import pages.systemSettings.domainsPage.DomainsPage;
import rest.models.DomainModel;
import rest.models.UserModel;

public class DomainsPgTests extends SeleniumTest {

    /**
     * This class has the tests against Domains Page
     */
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
    public void SystemAdminIsAbleToCreateDomains() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        soft.assertEquals(domainModel.getSignatureKeyAlias(), domainsPage.getDomainTab().getResponseSignatureCertificateSelectedValue());
        soft.assertEquals(domainModel.getVisibility(), domainsPage.getDomainTab().getVisibilityOfDomainSelectedValue());
        soft.assertEquals("To complete domain configuration, please select at least one resource type from the Resource Types tab", domainsPage.getDomainWarningMessage());
        soft.assertAll();
    }


    @Test(description = "DOM-02 System admin can integrates domain with SMP")
    public void SystemAdminCanIntegrateDomainWithSMP() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();

        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        alert = domainsPage.getAlertMessageAndClose();
        soft.assertEquals(alert, "Domain [" + domainModel.getDomainCode() + "] registered to sml!");

        //Go to SML
        driver.get(data.getSMLUrl());
        SmlPage smlPage = new SmlPage(driver);
        soft.assertTrue(smlPage.isDomainRegistered(domainModel), "Domain is not present in SML");
        soft.assertAll();

    }

    @Test(description = "DOM-03 System admin is able to Invite/Remove users from domains")
    public void SystemAdminIsAbleToInviteRemoveUsersFromDomains() throws Exception {
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
        WebElement domainElement = editDomainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        domainsPage.goToTab("Members");
        domainsPage.getMembersTab().changeRoleOfUser(normalUser.getUsername(), "ADMIN");

        //check if user has admin rights to domain as Admin
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNotNull(domainElement, "Domain found for user which doesn't have rights");


        //Remove member user and check if he has access to the domain
        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode()).click();
        domainsPage.goToTab("Members");
        domainsPage.getMembersTab().removeUser(normalUser.getUsername());
        userMemberElement = domainsPage.getMembersTab().getMembersGrid().searchAndGetElementInColumn("Username", normalUser.getUsername());
        soft.assertNull(userMemberElement, "Domain found for user which doesn't have rights");

        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        editDomainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
        domainElement = editDomainsPage.getDataPanelGrid().searchAndGetElementInColumn("Domain code", domainModel.getDomainCode());
        soft.assertNull(domainElement, "Domain found for user which doesn't have rights");

        soft.assertAll();

    }


}