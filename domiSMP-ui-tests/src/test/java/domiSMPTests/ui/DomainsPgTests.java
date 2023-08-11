package domiSMPTests.ui;
import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DomainsPage.DomainsPage;
import pages.LoginPage;
import pages.SmlPage;
import rest.models.DomainModel;
import rest.models.UserModel;

public class DomainsPgTests extends SeleniumTest {

    /**
     * This class has the tests against Domains Page
     */
    @Test(description = "DOM-01 System admin is able to create Domains")
    public void SystemAdminIsAbleToCreateDomains() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        DomainsPage domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlert();
        Assert.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getDataPanelGrid().searchValueInColumn("Domain code", domainModel.getDomainCode()).click();
        Assert.assertEquals(domainModel.getSignatureKeyAlias(), domainsPage.getDomainTab().getResponseSignatureCertificateSelectedValue());
        Assert.assertEquals(domainModel.getVisibility(), domainsPage.getDomainTab().getVisibilityOfDomainSelectedValue());
        Assert.assertEquals("To complete domain configuration, please select at least one resource type from the Resource Types tab", domainsPage.getDomainWarningMessage());
    }


    @Test(description = "DOM-02 System admin can integrates domain with SMP")
    public void SystemAdminCanIntegrateDomainWithSMP() throws Exception {
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        DomainsPage domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlert();
        Assert.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getDataPanelGrid().searchValueInColumn("Domain code", domainModel.getDomainCode()).click();
        domainsPage.goToTab("SML integration");
        domainsPage.getSMLIntegrationTab().fillSMLIntegrationTab(domainModel);
        domainsPage.getSMLIntegrationTab().saveChanges();
        domainsPage.getSMLIntegrationTab().registerToSML();

        alert = domainsPage.getAlert();
        Assert.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] registered to sml!");

        //Go to SML
        driver.get(data.getSMLUrl());
        SmlPage smlPage = new SmlPage(driver);
        Assert.assertTrue(smlPage.isDomainRegistered(domainModel), "Domain is not present in SML");

    }

    @Test(description = "DOM-03 System admin is able to Invite/Remove users from domains")
    public void SystemAdminIsAbleToInviteRemoveUsersFromDomains() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();

        rest.users().createUser(normalUser);

        DomainModel domainModel = DomainModel.generatePublicDomainModelWithoutSML();
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        DomainsPage domainsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.getDomainTab().fillDomainData(domainModel);
        domainsPage.getDomainTab().saveChanges();
        String alert = domainsPage.getAlert();
        Assert.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.goToTab("Members");
        domainsPage.getMembersTab().getInviteMemberBtn().click();
        domainsPage.getMembersTab().getInviteMembersPopup().selectMember(normalUser.getUsername(), "VIEWER");
        WebElement el = domainsPage.getMembersTab().getMembersGrid().searchValueInColumn("Username", normalUser.getUsername());
        Assert.assertNotNull(el);

        homePage.logout();
        homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());


    }
}
