package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.UsersPage;
import rest.models.UserModel;

public class UsersPgTests extends SeleniumTest {
    SoftAssert soft;
    DomiSMPPage homePage;
    LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
    }
    @Test(description = "USR-01 System admin is able to create new users")
    public void SystemAdminIsAbleToCreateNewUsers() throws Exception {

        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        usersPage.fillNewUserDataAndSave(adminNewUserData);

        usersPage.refreshPage();
        // usersPage.filter(adminNewUserData.getUsername());
        WebElement newUser = usersPage.getDataPanelGrid().searchAndGetElementInColumn("Username", adminNewUserData.getUsername());
        soft.assertNotNull(newUser);
        newUser.click();

        soft.assertEquals(usersPage.getApplicationRoleValue(), adminNewUserData.getRole());
        soft.assertEquals(usersPage.getFullNameValue(), adminNewUserData.getFullName());
        soft.assertTrue(usersPage.isSelectedUserActive(), "User active status is true");

        soft.assertEquals(usersPage.getEmailValue(), adminNewUserData.getEmailAddress());
        soft.assertEquals(usersPage.getSelectedThemeValue(), adminNewUserData.getSmpTheme());
        soft.assertEquals(usersPage.getSelectedLocaleValue(), adminNewUserData.getSmpLocale());

        soft.assertAll();


    }

    @Test(description = "USR-02 USR-02 System admin is not able to create duplicated user")
    public void SystemAdminIsNotAbleToCreateDuplicatedUser() throws Exception {

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
}