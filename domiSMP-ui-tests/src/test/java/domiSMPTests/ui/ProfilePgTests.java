package domiSMPTests.ui;

import ddsl.dcomponents.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProfilePage;
import rest.models.UserModel;


public class ProfilePgTests extends SeleniumTest {

    /**
     * This class has the tests against Profile Page
     */
    @Test(description = "PROF-01")
    public void AllLoggedUsersAreAbleToSeeProfilePage() throws Exception {
        UserModel normalUser = UserModel.createUserWithUSERrole();

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
        UserModel adminUser = UserModel.createUserWithUSERrole();
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

    @Test(description = "PROF-02")
    public void AllLoggedUsersAreAbleToUpdateProfilePage() throws Exception {
        UserModel normalUser = UserModel.createUserWithUSERrole();

        rest.users().createUser(normalUser);

        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());

        //Navigate to page
        ProfilePage profilePage = (ProfilePage) homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel userNewProfileData = UserModel.generateUserProfileData();
        profilePage.changeUserProfileData(userNewProfileData.getEmailAddress(), userNewProfileData.getFullName(), userNewProfileData.getSmpTheme(), userNewProfileData.getSmpLocale());

        profilePage.refreshPage();

        //Verify if data is changed

        Assert.assertEquals(profilePage.getEmailAddress(), userNewProfileData.getEmailAddress());
        Assert.assertEquals(profilePage.getFullName(), userNewProfileData.getFullName());
        Assert.assertEquals(profilePage.getSelectedTheme(), userNewProfileData.getSmpTheme());
        Assert.assertEquals(profilePage.getSelectedLocale(), userNewProfileData.getSmpLocale());

        homePage.logout();


        UserModel adminUser = UserModel.createUserWithUSERrole();

        rest.users().createUser(adminUser);

        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        //Navigate to page
        profilePage = (ProfilePage) homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel adminNewProfileData = UserModel.generateUserProfileData();
        profilePage.changeUserProfileData(adminNewProfileData.getEmailAddress(), adminNewProfileData.getFullName(), adminNewProfileData.getSmpTheme(), adminNewProfileData.getSmpLocale());

        profilePage.refreshPage();

        //Verify if data is changed
        Assert.assertEquals(profilePage.getEmailAddress(), adminNewProfileData.getEmailAddress());
        Assert.assertEquals(profilePage.getFullName(), adminNewProfileData.getFullName());
        Assert.assertEquals(profilePage.getSelectedTheme(), adminNewProfileData.getSmpTheme());
        Assert.assertEquals(profilePage.getSelectedLocale(), adminNewProfileData.getSmpLocale());


    }
}
