package domiSMPTests.ui;

import ddsl.dcomponents.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.UsersPage;
import rest.models.UserModel;

public class UsersPgTests extends SeleniumTest {
    @Test(description = "USR-01 System admin is able to create new users")
    public void SystemAdminIsAbleToCreateNewUsers() throws Exception {
        UserModel adminUser = UserModel.createUserWithADMINrole();
        rest.users().createUser(adminUser);
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        UsersPage usersPage = (UsersPage) homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);


    }
}