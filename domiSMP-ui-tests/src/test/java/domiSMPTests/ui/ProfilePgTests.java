package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.SetChangePasswordDialog;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.propertiesPage.PropertiesPage;
import pages.systemSettings.propertiesPage.PropertyPopup;
import pages.userSettings.ProfilePage;
import rest.models.UserModel;
import utils.Generator;

import java.util.List;


public class ProfilePgTests extends SeleniumTest {

    /**
     * This class has the tests against Profile Page
     */
    SoftAssert soft = new SoftAssert();
    DomiSMPPage homePage;
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest(){
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
    }
    @Test(description = "PROF-01 All logged users are able to view the Profile Page")
    public void allLoggedUsersShouldAbleToSeeProfilePage() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(normalUser);

        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        //Check if menu is available
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));

        //Navigate to page
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);

        homePage.logout();

        //Check if page is avaiable for Admin users
        UserModel adminUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(adminUser);

        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        //Check if menu is available
        soft.assertTrue(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));

        //Navigate to page
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);

        //Check if ProfilePage is not available for anonymous users
        homePage.logout();
        soft.assertFalse(homePage.getSidebar().isMenuAvailable(Pages.USER_SETTINGS_PROFILE));
        soft.assertAll();
    }

    @Test(description = "PROF-02 All loggedin users are able to update profile data")
    public void allLoggedUsersShouldAbleToUpdateProfilePage() throws Exception {
        UserModel normalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(normalUser);

        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        //Navigate to page
        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel userNewProfileData = UserModel.generateUserProfileData();
        profilePage.profileData.fillUserProfileData(userNewProfileData.getEmailAddress(), userNewProfileData.getFullName(), userNewProfileData.getSmpTheme(), userNewProfileData.getSmpLocale());
        profilePage.refreshPage();
        //Verify if data is changed

        soft.assertEquals(profilePage.profileData.getEmailAddress(), userNewProfileData.getEmailAddress(), "Email value is different");
        soft.assertEquals(profilePage.profileData.getFullName(), userNewProfileData.getFullName(), "Full name value is different");
        soft.assertEquals(profilePage.profileData.getSelectedTheme(), userNewProfileData.getSmpTheme(), "Selected theme value is different");
        soft.assertEquals(profilePage.profileData.getSelectedLocale(), userNewProfileData.getSmpLocale(), "Locale value is different");

        homePage.logout();

        UserModel adminUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(adminUser);

        loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        //Navigate to page
        profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        UserModel adminNewProfileData = UserModel.generateUserProfileData();
        profilePage.profileData.fillUserProfileData(adminNewProfileData.getEmailAddress(), adminNewProfileData.getFullName(), adminNewProfileData.getSmpTheme(), adminNewProfileData.getSmpLocale());

        profilePage.refreshPage();

        //Verify if data is changed
        soft.assertEquals(profilePage.profileData.getEmailAddress(), adminNewProfileData.getEmailAddress());
        soft.assertEquals(profilePage.profileData.getFullName(), adminNewProfileData.getFullName());
        soft.assertEquals(profilePage.profileData.getSelectedTheme(), adminNewProfileData.getSmpTheme());
        soft.assertEquals(profilePage.profileData.getSelectedLocale(), adminNewProfileData.getSmpLocale());
        soft.assertAll();

    }

    @Test(description = "PROF-03 Password validation is accord to the smp propeties values")
    public void passwordValidationsShouldBeAccordingToPropertiesValue() throws Exception {
        String propertyName = "smp.passwordPolicy.validationRegex";
        String newPropertyValue = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&+=\\-_<>.,?:;*/()|\\[\\]{}'\"\\\\]).{16,40}$";
        String new40CharactersPasswordValue = "Edeltest!23456789Edeltest!234567890sssf";

        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);

        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        PropertiesPage propertiesPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_PROPERTIES);
        propertiesPage.propertySearch(propertyName);
        if (!propertiesPage.getPropertyValue(propertyName).equals(newPropertyValue)) {
            PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(propertyName);
            propertyEditPoup.editInputField(newPropertyValue);
            propertyEditPoup.clickOK();
            propertiesPage.save();
        }

        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        SetChangePasswordDialog setChangePasswordDialog = profilePage.profileData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getNewPassword(), new40CharactersPasswordValue);
        List<String> errors = setChangePasswordDialog.getFieldErrorMessage();
        DomiSMPPage homepage = setChangePasswordDialog.TryClickOnChangePassword();
        soft.assertEquals(errors.size(), 0, "Could not change the password of the user");
        soft.assertNotNull(homepage, "Could not change the password of the user");
        soft.assertAll();

    }

    @Test(description = "PROF-04 User should be able to change his password")
    public void userShouldBeAbleToChangeHisPassword() throws Exception {
        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);

        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        ProfilePage profilePage = loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        String oldLastSet = profilePage.profileData.getLastSetValue();
        String oldPasswordExpiresOn = profilePage.profileData.getPasswordExpiresOnValue();

        //profilePage.profileData.setChangePasswordBtn.click();
        String newPass = "Edeltest!23456789Edelt" + Generator.randomAlphaNumericValue(4);
        SetChangePasswordDialog setChangePasswordDialog = profilePage.profileData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getNewPassword(), newPass);
        homePage = setChangePasswordDialog.TryClickOnChangePassword();

        soft.assertNotNull(homePage, "Could not change the password of the user");

        homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), newPass);
        profilePage = loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        Assert.assertNotSame(profilePage.profileData.getLastSetValue(), oldLastSet, "Last set value is not reseted");
        Assert.assertNotSame(profilePage.profileData.getPasswordExpiresOnValue(), oldPasswordExpiresOn, "Password expires on value is not reseted");
    }
}
