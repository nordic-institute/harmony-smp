package domiSMPTests.ui;

import ddsl.dcomponents.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import rest.models.CreateUserModel;


public class ProfilePgTests extends SeleniumTest {
    @Test(description = "PROF-01")
    public void AllUsersAreAbleToSeeProfilePage() throws Exception {
        CreateUserModel normalUser = CreateUserModel.createUserWithUSERrole();

        rest.users().createUser(normalUser);

        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());

        //Check if menu is available
        Assert.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));

        //Navigate to page
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);

        homePage.logout();

        //Check if page is avaiable for Admin users
        CreateUserModel adminUser = CreateUserModel.createUserWithUSERrole();
        rest.users().createUser(adminUser);

        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        //Check if menu is available
        Assert.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));

        //Navigate to page
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);

        //Check if ProfilePage is not available for anonymous users
        homePage.logout();
        Assert.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));
    }
}
