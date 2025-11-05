package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.dobjects.DWait;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ResetCredentialsPage;
import rest.InbucketRestClient;
import rest.models.UserModel;

import java.util.List;

public class LoginPgTests extends SeleniumTest {

    SoftAssert soft = new SoftAssert();
    DomiSMPPage homePage;
    LoginPage loginPage;
    InbucketRestClient restClient = new InbucketRestClient();

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

    @Test(description = "LGN-32 - User is able to reset password")
    public void userIsAbleToResetPassword() throws Exception {

        UserModel newAdminUser = UserModel.generateUserWithADMINrole();
        UserModel newNormalUser = UserModel.generateUserWithUSERrole();

        String adminUserId = rest.users().createUser(newAdminUser).getString("userId");
        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");

        rest.users().changePassword(adminUserId, data.getNewPassword());
        rest.users().changePassword(normalUserId, data.getNewPassword());

        loginPage.login(newAdminUser.getUsername(), data.getNewPassword());
        try {
            homePage.logout();

        } catch (Exception e) {
            soft.assertTrue(false, "User is not logged in!");
        }
        //Reset admin password
        String message = loginPage.resetPassword(newAdminUser.getUsername());
        soft.assertEquals(message, "A confirmation email has been sent to your registered email address for user [" + newAdminUser.getUsername() + "]. Please follow the instructions in the email to complete the account reset process. If you did not receive mail try later or contact administrator");
        String emailUsername = newAdminUser.getEmailAddress().substring(0, 14);

        //Retrieve reset URL
        String resetURL = restClient.getResetPasswordTokenFromLastEmailOfUser(emailUsername);
        driver.get(resetURL);

        //Reset password for Admin
        ResetCredentialsPage resetCredentialsPage = new ResetCredentialsPage(driver);
        String newPasswordAfterReset = "Qwe!@#123412341234";
        resetCredentialsPage.fillChangePasswordFields(newAdminUser.getUsername(), newPasswordAfterReset, newPasswordAfterReset);
        resetCredentialsPage.clickSetChangePasswordButton();

        //Login with new password for Admin
        soft.assertTrue(loginPage.getAlertArea().getAlertMessage().contains("Password has been reset successfully. Please login with new password"), "Reset password message didn't appear");
        loginPage.login(newAdminUser.getUsername(), newPasswordAfterReset);
        try {
            homePage.logout();

        } catch (Exception e) {
            soft.assertTrue(false, "User is not logged in!");
        }

        //Reset password User Role password
        message = loginPage.resetPassword(newNormalUser.getUsername());
        soft.assertEquals(message, "A confirmation email has been sent to your registered email address for user [" + newNormalUser.getUsername() + "]. Please follow the instructions in the email to complete the account reset process. If you did not receive mail try later or contact administrator");
        String emailUserRoleUsername = newNormalUser.getEmailAddress().substring(0, 14);

        //Retrieve reset URL
        String resetURLUserRole = restClient.getResetPasswordTokenFromLastEmailOfUser(emailUserRoleUsername);
        driver.get(resetURLUserRole);

        //Reset password for User
        resetCredentialsPage = new ResetCredentialsPage(driver);
        newPasswordAfterReset = "Qwe!@#123412341234";
        resetCredentialsPage.fillChangePasswordFields(newNormalUser.getUsername(), newPasswordAfterReset, newPasswordAfterReset);
        resetCredentialsPage.clickSetChangePasswordButton();

        //Login with new password for User
        soft.assertTrue(loginPage.getAlertArea().getAlertMessage().contains("Password has been reset successfully. Please login with new password"), "Reset password message didn't appear");
        loginPage.login(newNormalUser.getUsername(), newPasswordAfterReset);
        try {
            homePage.logout();

        } catch (Exception e) {
            soft.assertTrue(false, "User is not logged in!");
        }
        soft.assertAll();
    }

    @Test(description = "LGN-34 - Creating a new reset password token invalids previous tokens")
    public void creatingANewResetPasswordTokenInvalidatesPreviousTokens() throws Exception {

        UserModel newNormalUser = UserModel.generateUserWithUSERrole();
        String normalUserId = rest.users().createUser(newNormalUser).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());


        //Reset password User Role password
        String message = loginPage.resetPassword(newNormalUser.getUsername());
        soft.assertEquals(message, "A confirmation email has been sent to your registered email address for user [" + newNormalUser.getUsername() + "]. Please follow the instructions in the email to complete the account reset process. If you did not receive mail try later or contact administrator");
        String emailUserRoleUsername = newNormalUser.getEmailAddress().substring(0, 14);

        //Retrieve reset URL
        String firstResetURL = restClient.getResetPasswordTokenFromLastEmailOfUser(emailUserRoleUsername);

        //Reset password again
        loginPage.resetPassword(newNormalUser.getUsername());
        String secondResetUrl = restClient.getResetPasswordTokenFromLastEmailOfUser(emailUserRoleUsername);

        //Check if 1st token is invalid
        driver.get(firstResetURL);
        soft.assertEquals(loginPage.getAlertArea().getAlertMessage(), "The reset token it is invalid or not active any more. Please try to reset your password again.", "Invalid token error message was not found");
        soft.assertEquals(loginPage.getBreadcrump().getCurrentPage(), "Login");

        //Check if 2nd token is invalid

        driver.get(secondResetUrl);

        ResetCredentialsPage resetCredentialsPage = new ResetCredentialsPage(driver);
        String newPasswordAfterReset = "Qwe!@#123412341234";
        resetCredentialsPage.fillChangePasswordFields(newNormalUser.getUsername(), newPasswordAfterReset, newPasswordAfterReset);
        resetCredentialsPage.clickSetChangePasswordButton();

        //Login with new password for User
        new DWait(driver).equals(loginPage.getAlertArea());
        soft.assertTrue(loginPage.getAlertArea().getAlertMessage().contains("Password has been reset successfully. Please login with new password"), "Reset password message didn't appear");
        loginPage.login(newNormalUser.getUsername(), newPasswordAfterReset);
        try {
            homePage.logout();

        } catch (Exception e) {
            soft.assertTrue(false, "User is not logged in!");
        }
        soft.assertAll();
    }

    @Test(description = "LGN-35 - Reset password screen applies password complexity")
    public void resetPasswordScreenAppliesPasswordComplexity() {

        UserModel user = UserModel.generateUserWithUSERrole();
        String normalUserId = rest.users().createUser(user).getString("userId");
        rest.users().changePassword(normalUserId, data.getNewPassword());


        //Reset password User Role password
        String message = loginPage.resetPassword(user.getUsername());
        soft.assertEquals(message, "A confirmation email has been sent to your registered email address for user [" + user.getUsername() + "]. Please follow the instructions in the email to complete the account reset process. If you did not receive mail try later or contact administrator");
        String emailUserRoleUsername = user.getEmailAddress().substring(0, 14);

        //Retrieve reset URL
        String resetUrl = restClient.getResetPasswordTokenFromLastEmailOfUser(emailUserRoleUsername);
        driver.get(resetUrl);

        ResetCredentialsPage resetCredentialsPage = new ResetCredentialsPage(driver);

        //User is not able to set the same password again
        resetCredentialsPage.fillChangePasswordFields(user.getUsername(), data.getNewPassword(), data.getNewPassword());
        resetCredentialsPage.clickSetChangePasswordButton();
        soft.assertEquals(loginPage.getAlertArea().getAlertMessage(), "Password change failed. Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check minim length of password
        driver.navigate().refresh();
        String minLengthPassword = "!234sdfg*&&^";
        resetCredentialsPage.fillChangePasswordFields(user.getUsername(), minLengthPassword, minLengthPassword);
        List<String> errors = resetCredentialsPage.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1);
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check special character of password
        driver.navigate().refresh();
        String specialCharacterPassword = "QWSQWWqw12qw1212";
        errors.clear();
        resetCredentialsPage.fillChangePasswordFields(user.getUsername(), specialCharacterPassword, specialCharacterPassword);
        errors = resetCredentialsPage.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Special character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        //Check lower character of password
        driver.navigate().refresh();
        String lowerCharacterPassword = "QA!@QA!@QW12QW12";
        errors.clear();
        resetCredentialsPage.fillChangePasswordFields(user.getUsername(), lowerCharacterPassword, lowerCharacterPassword);
        errors = resetCredentialsPage.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Lower character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");


        //Check upper character of password
        driver.navigate().refresh();
        String upperCharacterPassword = "qw!@qw!@qw12qw12";
        errors.clear();
        resetCredentialsPage.fillChangePasswordFields(user.getUsername(), upperCharacterPassword, upperCharacterPassword);
        errors = resetCredentialsPage.getFieldErrorMessage();
        soft.assertEquals(errors.size(), 1, "Upper character validation does not appear");
        soft.assertEquals(errors.get(0), "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character;Must not be same as existing password");

        soft.assertAll();
    }

}
