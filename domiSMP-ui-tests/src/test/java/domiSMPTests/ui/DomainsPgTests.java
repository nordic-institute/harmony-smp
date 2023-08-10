package domiSMPTests.ui;
import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DomainsPage.DomainsPage;
import pages.LoginPage;
import pages.SmlPage;
import rest.models.DomainModel;

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

        domainsPage.getGrid().searchValueInColumn("Domain code", domainModel.getDomainCode()).click();
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

        domainsPage.getGrid().searchValueInColumn("Domain code", domainModel.getDomainCode()).click();
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
}
