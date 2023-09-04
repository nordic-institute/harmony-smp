package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.domainsPage.DomainsPage;
import rest.models.DomainModel;
import rest.models.UserModel;

public class EditDomainsPgTests extends SeleniumTest {

    DomiSMPPage homePage;
    LoginPage loginPage;
    DomainsPage domainsPage;
    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        DomainModel domainModel = DomainModel.generatePublicDomainModelWithSML();
        UserModel adminUser = UserModel.generateUserWithADMINrole();

        rest.users().createUser(adminUser);

        JSONObject domainJson = rest.domains().createDomain(domainModel);
        String domainId = domainJson.get("domainId").toString();
        rest.domains().AddMembersToDomain(domainId, adminUser.getUsername(), "ADMIN");

        homePage = new DomiSMPPage(driver);


        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        domainsPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
    }

    @Test(description = "DOM-02 System admin can integrates domain with SMP")
    public void SystemAdminCanIntegrateDomainWithSMssP() throws Exception {
    }


}
