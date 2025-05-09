package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.SetChangePasswordDialog;
import ddsl.enums.Messages;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.UsersPage;
import pages.systemSettings.propertiesPage.PropertiesPage;
import pages.systemSettings.propertiesPage.PropertyPopup;
import pages.userSettings.ProfilePage;
import rest.models.UserModel;
import utils.Generator;
import utils.TestRunData;

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
        profilePage.profileData.fillUserProfileDataAndSave(userNewProfileData.getEmailAddress(),
                userNewProfileData.getFullName(), userNewProfileData.getSmpTheme(),
                userNewProfileData.getSmpLocale());
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
        profilePage.profileData.fillUserProfileDataAndSave(adminNewProfileData.getEmailAddress(), adminNewProfileData.getFullName(), adminNewProfileData.getSmpTheme(), adminNewProfileData.getSmpLocale());

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
        DomiSMPPage homepage = setChangePasswordDialog.tryClickOnChangePassword();
        String sucesfullMessage = homepage.getAlertArea().getAlertMessage();
        soft.assertEquals(sucesfullMessage, Messages.PASSWORD_SUCCESSFULL_PASSWORD_CHANGED);
        soft.assertEquals(errors.size(), 0, "Could not change the password of the user");
        soft.assertNotNull(homepage, "Homepage is not loaded. Could not change the password of the user");
        soft.assertAll();

    }

    @Test(description = "PROF-04 User should be able to change his password")
    public void
    userShouldBeAbleToChangeHisPassword() throws Exception {
        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);

        loginPage.login(adminUser.getUsername(), TestRunData.getInstance().getNewPassword());

        ProfilePage profilePage = loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        String oldLastSet = profilePage.profileData.getLastSetValue();
        String oldPasswordExpiresOn = profilePage.profileData.getPasswordExpiresOnValue();

        String newPass = "Edeltest!23456789Edelt" + Generator.randomAlphaNumericValue(4);
        SetChangePasswordDialog setChangePasswordDialog = profilePage.profileData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(),
                newPass);
        homePage = setChangePasswordDialog.tryClickOnChangePassword();
        String sucesfullMessage = homePage.getAlertArea().getAlertMessage();
        soft.assertEquals(sucesfullMessage, Messages.PASSWORD_SUCCESSFULL_PASSWORD_CHANGED);

        homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), newPass);
        profilePage = loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        Assert.assertNotSame(profilePage.profileData.getLastSetValue(), oldLastSet, "Last set value is not reseted");
        Assert.assertNotSame(profilePage.profileData.getPasswordExpiresOnValue(), oldPasswordExpiresOn, "Password expires on value is not reseted");
    }

    @Test(description = "PROF-06 User loggins attempts are reset after a successfull login")
    public void userLogginsAttemptsAreShowInProfilePage() throws Exception {
        UserModel adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);
        loginPage.login(adminUser.getUsername(), "wrongpassword");
        loginPage.getAlertArea().closeAlert();
        loginPage.login(adminUser.getUsername(), "wrongpassword");

        loginPage.login(TestRunData.getInstance().getAdminUsername(), TestRunData.getInstance().getAdminPassword());

        UsersPage usersPage = loginPage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getLeftSideGrid().searchAndClickElementInColumn("Username", adminUser.getUsername());
        String seqAttempts = usersPage.userData.getSequenceFailedAttempts();
        soft.assertEquals(Integer.parseInt(seqAttempts), 2, "Wrong number of attempts shown in the Users page!");

        loginPage.logout();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        ProfilePage profilePage = loginPage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        String resetSeqAttempts = profilePage.profileData.getSequenceFailedAttempts();
        soft.assertEquals(resetSeqAttempts, "---", "Seq failed attempts is not reset after a successfull login!");
        soft.assertAll();
    }

    @Test(description = "PROF-07 Check if password is according to password validity")
    public void checkIfPasswordIsAccordingToPasswordPolicy() throws Exception {

        UserModel user = UserModel.generateUserWithADMINrole();
        String normalUserId = rest.users().createUser(user).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(user.getUsername(), TestRunData.getInstance().getNewPassword());
        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        SetChangePasswordDialog setChangePasswordDialog = profilePage.profileData.clickOnChangePassword();

        //Check minim length of password
        String minLengthPassword = "!234sdfg*&&^";
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(), minLengthPassword);
        List<String> errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1);
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check special character of password
        String specialCharacterPassword = "QWSQWWqw12qw1212";
        errors.clear();
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(), specialCharacterPassword);
        errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Special character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check lower character of password
        String lowerCharacterPassword = "QA!@QA!@QW12QW12";
        errors.clear();
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(), lowerCharacterPassword);
        errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Lower character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");


        //Check upper character of password
        String upperCharacterPassword = "qw!@qw!@qw12qw12";
        errors.clear();
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(), upperCharacterPassword);
        errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Upper character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check upper character of password
        String validValue = "Qw!@qw!@qw12qw12";
        errors.clear();
        setChangePasswordDialog.fillChangePassword(TestRunData.getInstance().getNewPassword(), validValue);
        errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 0, "Validation appears for correct value");
        soft.assertTrue(setChangePasswordDialog.getSetPasswordBtn().isEnabled(), "Set password button is disabled!");

        soft.assertAll();
    }
}


