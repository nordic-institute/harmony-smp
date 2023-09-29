package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.administration.editDomainsPage.EditDomainsPage;
import rest.models.DomainModel;
import rest.models.MemberModel;
import rest.models.UserModel;

public class EditGroupsPgTests extends SeleniumTest {
    DomiSMPPage homePage;
    LoginPage loginPage;
    EditDomainsPage editDomainPage;
    String domainId;
    DomainModel domainModel;
    UserModel adminUser;
    SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        domainModel = DomainModel.generatePublicDomainModelWithSML();
        adminUser = UserModel.generateUserWithADMINrole();

        MemberModel domainMember = new MemberModel();
        domainMember.setUsername(adminUser.getUsername());
        domainMember.setRoleType("ADMIN");

        rest.users().createUser(adminUser);
        JSONObject domainJson = rest.domains().createDomain(domainModel);
        domainId = domainJson.get("domainId").toString();
        rest.domains().addMembersToDomain(domainId, domainMember);

        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        editDomainPage = homePage.getSidebar().navigateTo(Pages.ADMINISTRATION_EDIT_DOMAINS);
    }

    @Test(description = "EDTDOM-01 Domain admins are able to invite/edit/remove members")
    public void DomainAdminsAreAbleToInviteEditRemoveMembers() throws Exception {

    }
}
