package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.Test;
import pages.DomainsPage;
import pages.LoginPage;

public class DomainsPgTests extends SeleniumTest {

    /**
     * This class has the tests against Domains Page
     */
    @Test(description = "DOM-01 System admin is able to create Domains")
    public void SystemAdminIsAbleToCreateDomains() throws Exception {
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        DomainsPage domainsPage = homePage.getSidebar().navigateTo2(Pages.SYSTEM_SETTINGS_DOMAINS);
        domainsPage.getCreateDomainBtn().click();
        domainsPage.fillDomainData("dsadsadas");

    }
}
