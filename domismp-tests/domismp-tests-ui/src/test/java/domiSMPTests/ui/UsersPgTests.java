package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.SetChangePasswordDialog;
import ddsl.enums.ApplicationRoles;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.UsersPage;
import pages.userSettings.ProfilePage;
import rest.models.UserModel;
import utils.Utils;

import java.util.List;

public class UsersPgTests extends SeleniumTest {
    SoftAssert soft;
    DomiSMPPage homePage;
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest(){
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
    }

    @Test(description = "USR-01 System admin is able to create new users")
    public void systemAdminIsAbleToCreateNewUsers() throws Exception {

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        usersPage.fillNewUserDataAndSave(adminNewUserData);

        usersPage.refreshPage();
        usersPage.filterAndSelectUsername(adminNewUserData.getUsername());

        soft.assertEquals(usersPage.getApplicationRoleValue(), adminNewUserData.getRole());
        soft.assertEquals(usersPage.getFullNameValue(), adminNewUserData.getFullName());
        soft.assertTrue(usersPage.isSelectedUserActive(), "User active status is true");

        soft.assertEquals(usersPage.getEmailValue(), adminNewUserData.getEmailAddress());
        soft.assertEquals(usersPage.getSelectedThemeValue(), adminNewUserData.getSmpTheme());
        soft.assertEquals(usersPage.getSelectedLocaleValue(), "en");

        soft.assertAll();
    }


    @Test(description = "USR-02 System admin is not able to create duplicated user")
    public void systemAdminIsNotAbleToCreateDuplicatedUser() throws Exception {

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        usersPage.fillNewUserDataAndSave(adminNewUserData);

        usersPage.refreshPage();
        usersPage.getCreateUserBtn().click();
        String alertMessage = usersPage.fillNewUserDataAndSave(adminNewUserData);
        soft.assertEquals(alertMessage, "Invalid request [CreateUser]. Error: User with username [" + adminNewUserData.getUsername() + "] already exists!!");
        soft.assertAll();
    }

    @Test(description = "USR-03 System admin is able to delete user")
    public void systemAdminIsAbleToDeleteUser() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        String deleteAlert = usersPage.deleteAndConfirm();
        soft.assertEquals(deleteAlert, "User [" + newNormalUser.getUsername() + "] has been deleted!", "Delete user alert message is wrong");
        soft.assertFalse(usersPage.IsUsernamePresentInGrid(newNormalUser.getUsername()));
        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), data.getNewPassword());
        soft.assertEquals(loginPage.getAlertArea().getAlertMessage(), "Login failed; Invalid userID or password!", "Login failed alert message is not correct");
        soft.assertAll();
    }

    @Test(description = "USR-04 System admin is able to change the password of selected user")
    public void systemAdminIsAbleToChangePasswordOfSelectedUser() throws Exception {
        String newPassword = "@#$#asdddersPasswordValue12";
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(newNormalUser).getString("userId");

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());

        SetChangePasswordDialog setChangePasswordDialog = usersPage.userData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getAdminUser().get("password"), newPassword);
        setChangePasswordDialog.tryClickOnChangePassword();

        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), newPassword);
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        soft.assertEquals(homePage.getBreadcrump().getCurrentPage(), "Profile");
        soft.assertAll();
    }

    @Test(description = "USR-05 System admin can modify user's data")
    public void systemAdminCanModifyUserData() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        String newEmail = "newemail@email.com";
        String newFullname = "AUT_NewFullName";
        String newTheme = "Blue theme";
        String newLocale = "Romanian";
        usersPage.userData.fillUserProfileDataAndSave(newEmail, newFullname, newTheme, newLocale);

        homePage.logout();
        loginPage.login(newNormalUser.getUsername(), data.getNewPassword());
        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        soft.assertEquals(profilePage.profileData.getEmailAddress(), newEmail, "Email is not updated!");
        soft.assertEquals(profilePage.profileData.getFullName(), newFullname, "Fullname is not updated!");
        soft.assertEquals(profilePage.profileData.getSelectedTheme(), newTheme, "Theme is not updated!");
        soft.assertEquals(profilePage.profileData.getSelectedLocale(), "ro", "Locale is not updated!");
        soft.assertAll();
    }

    @Test(description = "USR-06 System admin is able to change the role of an user")
    public void systemAdminIsAbleToChangeTheRoleOfUsers() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        usersPage.changeApplicationRole(ApplicationRoles.SYSTEM_ADMIN);

        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), data.getNewPassword());
        homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_DOMAINS);
        soft.assertEquals(homePage.getBreadcrump().getCurrentPage(), "Domain");
        soft.assertAll();
    }

    @Test(description = "USR-07 System admin is able to active/deactivate users")
    public void systemAdminIsAbleToActivatDeactivateUser() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());
        //Deactivate user
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        usersPage.modifyIsActiveForUser(false);

        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), data.getNewPassword());
        soft.assertEquals(loginPage.getAlertArea().getAlertMessage(), "Login failed; Invalid userID or password!", "Login failed alert message is not correct");
        //Activate user

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        usersPage.modifyIsActiveForUser(true);

        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), data.getNewPassword());
        homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        soft.assertEquals(homePage.getBreadcrump().getCurrentPage(), "Profile");
        soft.assertAll();
    }


    @Test(description = "USR-08 Check if accounts are suspended")
    public void checkIfAccountsAreSuspended() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();
        String currentDate = Utils.getCurrentDate("M/d/YY");
        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());
        //Suspend user

        loginPage.login(newNormalUser.getUsername(), "123123123");
        loginPage.login(newNormalUser.getUsername(), "123123123");
        loginPage.login(newNormalUser.getUsername(), "123123123");
        loginPage.login(newNormalUser.getUsername(), "123123123");
        loginPage.login(newNormalUser.getUsername(), "123123123");
        soft.assertEquals(loginPage.getAlertArea().getAlertMessage(), "The user credential is suspended. Please try again later or contact your administrator.");

        //Validate if suspended info is correct
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());

        soft.assertEquals(usersPage.userData.getSequenceFailedAttempts(), "5", "Number of attempts is wrong");
        soft.assertTrue(usersPage.userData.getlastFailedAttempt().startsWith(currentDate), "Last failed attempt date is wrong Actual date: " + usersPage.userData.getlastFailedAttempt());
        soft.assertTrue(usersPage.userData.getsuspendedUntil().startsWith(currentDate), "Suspended until is wrong. Actual date: " + usersPage.userData.getsuspendedUntil());

        String newPass = "Edeltest!234123@#$";
        SetChangePasswordDialog setChangePasswordDialog = usersPage.userData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getAdminUser().get("password"),
                newPass);
        setChangePasswordDialog.tryClickOnChangePassword();
        usersPage.getAlertArea().closeAlert();
        //Login with new password
        loginPage.logout();
        loginPage.login(newNormalUser.getUsername(), newPass);
        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        soft.assertEquals(homePage.getBreadcrump().getCurrentPage(), "Profile");

        soft.assertEquals(profilePage.profileData.getSequenceFailedAttempts(), "---", "Number of attempts is wrong.");
        soft.assertEquals(profilePage.profileData.getlastFailedAttempt(), "---", "Last failed attempt date is wrong.");
        soft.assertEquals(profilePage.profileData.getsuspendedUntil(), "---", "Suspended until is wrong.");

        soft.assertAll();
    }

    @Test(description = "USR-12 - Admin creates user with invalid email - In create user dialog the OK button remains disabled until valid data is provided")
    public void adminCreatesUserWithInvalidEmail() throws Exception {

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        adminNewUserData.setEmailAddress("wrongEmail.com");
        usersPage.fillNewUserData(adminNewUserData);
        soft.assertEquals(usersPage.userData.getEmailValidationMessage(), "Email is invalid!");
        soft.assertTrue(usersPage.getSaveBtn().isDisabled(), "Save button is enabled when email validation appears!");

        adminNewUserData.setEmailAddress("wrong@Email");
        usersPage.fillNewUserData(adminNewUserData);
        soft.assertEquals(usersPage.userData.getEmailValidationMessage(), "Email is invalid!");
        soft.assertTrue(usersPage.getSaveBtn().isDisabled(), "Save button is enabled when email validation appears!");


        soft.assertAll();
    }


    @Test(description = "USR-14 - Admin cannot edit a user and provides invalid email")
    public void adminCannotEditAUserAndProvideInvalidEmail() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());

        String wrongValue1 = "newemail@email";

        usersPage.userData.getEmailInput().fill(wrongValue1);
        soft.assertEquals(usersPage.userData.getEmailValidationMessage(), "Email is invalid!", "Wrong/invalid email field validation");
        soft.assertTrue(usersPage.userData.getSaveBtn().isDisabled(), "Save button is not disabled");

        usersPage.userData.getEmailInput().clear();
        String wrongValue2 = "newemailemail.com";
        usersPage.userData.getEmailInput().fill(wrongValue2);
        soft.assertEquals(usersPage.userData.getEmailValidationMessage(), "Email is invalid!", "Wrong/invalid email field validation");
        soft.assertTrue(usersPage.userData.getSaveBtn().isDisabled(), "Save button is not disabled");

        usersPage.userData.getEmailInput().clear();
        String correctValue = "newemail@email.com";
        usersPage.userData.getEmailInput().fill(correctValue);
        soft.assertTrue(usersPage.userData.getSaveBtn().isEnabled(), "Save button is disabled");

        soft.assertAll();
    }

    @Test(description = "USR-15 - Admin wants to edit a users username - username field is disabled when editing a user")
    public void adminWantsToEditAUsersUsername() throws Exception {
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());
        soft.assertTrue(usersPage.getUsernameInput().isDisabled(), "Username input it is not disabled!");
        soft.assertAll();
    }


    @Test(description = "USR-18 - Admin sets password for new user and user uses it to login - user receives warning to change password, in User details dialog the valid until field is still empty")
    public void adminSetsPasswordForNewUseAndUserUsesItToLoginAndReceivesWarningToChangePassword() throws Exception {
        String newPassword = "@#$#asdddersPasswordValue12";

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        usersPage.fillNewUserDataAndSave(adminNewUserData);
        usersPage.filterAndSelectUsername(adminNewUserData.getUsername());

        SetChangePasswordDialog setChangePasswordDialog = usersPage.userData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getAdminUser().get("password"), newPassword);
        setChangePasswordDialog.tryClickOnChangePassword();


        loginPage.logout();
        loginPage.simpleLogin(adminNewUserData.getUsername(), newPassword);
        soft.assertTrue(homePage.getExpiredDialoginbutton().isPresent(), "Change password is not appearing.");
        homePage.getExpiredDialoginbutton().click();
        ProfilePage profilePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_PROFILE);
        soft.assertEquals(profilePage.profileData.getPasswordExpiresOnValue(), "---");

        soft.assertAll();
    }


    @Test(description = "USR-21 - Admin set password for user but new password and confirmation pasword don’t match - validation message shown and Set/change password button is disabled")
    public void adminSetPasswordForUserButNewPasswordAndConfirmationPasswordDontMatch() throws Exception {
        String newPassword = "@#$#asdddersPasswordValue12";
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(newNormalUser).getString("userId");

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.filterAndSelectUsername(newNormalUser.getUsername());

        SetChangePasswordDialog setChangePasswordDialog = usersPage.userData.clickOnChangePassword();
        setChangePasswordDialog.fillChangePassword(data.getAdminUser().get("password"), newPassword, "wrongPass");
        soft.assertTrue(setChangePasswordDialog.getSetPasswordBtn().isDisabled());
        List<String> errors = setChangePasswordDialog.getFieldErrorMessage();
        soft.assertEquals(errors.get(0), "Confirm password value does not match new password!", "Confirmation mismatch field validation is not wrong/not present");

        soft.assertAll();
    }


}