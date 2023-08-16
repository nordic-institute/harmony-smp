package domiSMPTests.ui;
import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import ddsl.enums.ResourceTypes;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DomainsPage.DomainsPage;
import pages.LoginPage;
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

        DomainsPage domainsPage = homePage.getSidebar().navigateTo2(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.fillDomainData(domainModel);
        String alert = domainsPage.saveChangesAndGetMessage();
        Assert.assertEquals(alert, "Domain: [" + domainModel.getDomainCode() + "] was created!");

        domainsPage.getGrid().searchValueInColumn("Domain code", domainModel.getDomainCode()).click();

        domainsPage.goToTab("Resource Types");
        domainsPage.getResourceTab().checkResource(ResourceTypes.getRandomResourceType());
        domainsPage.getResourceTab().saveChanges();


    }
}
