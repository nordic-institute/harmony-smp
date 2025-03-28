package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import rest.models.UserModel;

public class LoginPgTests extends SeleniumTest {

    SoftAssert soft = new SoftAssert();
    DomiSMPPage homePage;
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
    }

    @Test(description = "LGN-2 - Valid users can login via the Username/Password controls and the UI reflects their role")
    public void validUsersCanLoginAndUIReflectsTheirRole() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(normalUser);

        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);

        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        //Validate user role menu
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SEARCH_DNS_TOOLS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SEARCH_RESOURCES));

        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_DOMAINS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_GROUPS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_RESOURCES));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_REVIEW_TASKS));

        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_USERS));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_DOMAINS));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_KEYSTORE));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_TRUSTSTORE));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_EXTENSIONS));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_PROPERTIES));
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_ALERS));

        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_ACCESS_TOKEN));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_CERTIFICATES));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_MY_ALERTS));

        //Validate admin role menu
        homePage.logout();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SEARCH_DNS_TOOLS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SEARCH_RESOURCES));

        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_DOMAINS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_GROUPS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_EDIT_RESOURCES));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.ADMINISTRATION_REVIEW_TASKS));

        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_USERS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_DOMAINS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_KEYSTORE));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_TRUSTSTORE));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_EXTENSIONS));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_PROPERTIES));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.SYSTEM_SETTINGS_ALERS));


        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_ACCESS_TOKEN));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_CERTIFICATES));
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_MY_ALERTS));

        soft.assertAll();
    }
}
