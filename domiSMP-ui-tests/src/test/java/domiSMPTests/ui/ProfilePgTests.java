package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProfilePage.ProfilePage;
import pages.PropertiesPage.PropertiesPage;
import rest.models.UserModel;
import utils.Generator;


public class ProfilePgTests extends SeleniumTest {

    /**
     * This class has the tests against Profile Page
     */
    @Test(description = "PROF-01 All logged users are able to view the Profile Page")
    public void AllLoggedUsersShouldAbleToSeeProfilePage() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();

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
        UserModel adminUser = UserModel.generateUserWithUSERrole();
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

    @Test(description = "PROF-02 All loggedin users are able to update profile data")
    public void AllLoggedUsersShouldAbleToUpdateProfilePage() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();

        rest.users().createUser(normalUser);

        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());

        //Navigate to page
        ProfilePage profilePage = (ProfilePage) homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel userNewProfileData = UserModel.generateUserProfileData();
        profilePage.userData.fillUserProfileData(userNewProfileData.getEmailAddress(), userNewProfileData.getFullName(), userNewProfileData.getSmpTheme(), userNewProfileData.getSmpLocale());

        profilePage.refreshPage();

        //Verify if data is changed

        Assert.assertEquals(profilePage.userData.getEmailAddress(), userNewProfileData.getEmailAddress());
        Assert.assertEquals(profilePage.userData.getFullName(), userNewProfileData.getFullName());
        Assert.assertEquals(profilePage.userData.getSelectedTheme(), userNewProfileData.getSmpTheme());
        Assert.assertEquals(profilePage.userData.getSelectedLocale(), userNewProfileData.getSmpLocale());

        homePage.logout();


        UserModel adminUser = UserModel.generateUserWithUSERrole();

        rest.users().createUser(adminUser);

        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        //Navigate to page
        profilePage = (ProfilePage) homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel adminNewProfileData = UserModel.generateUserProfileData();
        profilePage.userData.fillUserProfileData(adminNewProfileData.getEmailAddress(), adminNewProfileData.getFullName(), adminNewProfileData.getSmpTheme(), adminNewProfileData.getSmpLocale());

        profilePage.refreshPage();

        //Verify if data is changed
        Assert.assertEquals(profilePage.userData.getEmailAddress(), adminNewProfileData.getEmailAddress());
        Assert.assertEquals(profilePage.userData.getFullName(), adminNewProfileData.getFullName());
        Assert.assertEquals(profilePage.userData.getSelectedTheme(), adminNewProfileData.getSmpTheme());
        Assert.assertEquals(profilePage.userData.getSelectedLocale(), adminNewProfileData.getSmpLocale());


    }

    @Test(description = "PROF-03 Password validation is accord to the smp propeties values")
    public void PasswordValidationsShouldBeAccordingToPropertiesValue() throws Exception {
        String propertyValue = "smp.passwordPolicy.validationRegex";
        String newPropertyValue = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,35}$";

        UserModel adminUser = UserModel.generateUserWithADMINrole();

        rest.users().createUser(adminUser);

        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        PropertiesPage propertiesPage = (PropertiesPage) homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_PROPERTIES);
        propertiesPage.propertySearch(propertyValue);
        if (!propertiesPage.getPropertyValue(propertyValue).equals(newPropertyValue)) {
            propertiesPage.setPropertyValue("smp.passwordPolicy.validationRegex", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,35}$");
            propertiesPage.save();
        }

        ProfilePage profilePage = (ProfilePage) propertiesPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        profilePage.userData.setChangePasswordBtn.click();
        Assert.assertEquals(0, profilePage.userData.setChangePasswordDialog().setNewPassword(data.getNewPassword(), "Edeltest!23456789Edeltest!234567890").size(), "Could not change the password of the user");

    }


    @Test(description = "PROF-04 User should be able to change his password")
    public void UserShouldBeAbleToChangeHisPassword() throws Exception {
        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        ProfilePage profilePage = (ProfilePage) loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        String oldLastSet = profilePage.userData.getLastSetValue();
        String oldPasswordExpiresOn = profilePage.userData.getPasswordExpiresOnValue();

        profilePage.userData.setChangePasswordBtn.click();
        String newPass = "Edeltest!23456789Edelt" + Generator.randomAlphaNumeric(4);

        Assert.assertEquals(profilePage.userData.setChangePasswordDialog().setNewPassword(data.getNewPassword(), newPass).size(), 0, "Could not change the password of the user");

        loginPage.login(adminUser.getUsername(), newPass);
        ProfilePage profilePage2 = (ProfilePage) loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);

        // Assert.assertNotSame(profilePage2.getLastSetValue(), oldLastSet, "Last set value is not reseted");
        //Assert.assertNotSame(profilePage2.getPasswordExpiresOnValue(), oldPasswordExpiresOn, "Password expires on value is not reseted");


    }
}
