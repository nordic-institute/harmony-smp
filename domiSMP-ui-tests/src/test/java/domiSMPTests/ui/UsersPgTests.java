package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.UsersPage;
import rest.models.UserModel;

public class UsersPgTests extends SeleniumTest {
    @Test(description = "USR-01 System admin is able to create new users")
    public void SystemAdminIsAbleToCreateNewUsers() throws Exception {
        DomiSMPPage homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        UsersPage usersPage = (UsersPage) homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_USERS);
        usersPage.getCreateUserBtn().click();
        UserModel adminNewUserData = UserModel.generateUserWithADMINrole();
        usersPage.fillNewUserDataAndSave(adminNewUserData);

        usersPage.refreshPage();
        usersPage.filter(adminNewUserData.getUsername());
        WebElement newUser = usersPage.getGrid().searchValueInColumn("Username", adminNewUserData.getUsername());
        Assert.assertNotNull(newUser);
        newUser.click();
        Assert.assertEquals(usersPage.getApplicationRole(), adminNewUserData.getRole());
        Assert.assertEquals(usersPage.getFullName(), adminNewUserData.getFullName());
        Assert.assertTrue(usersPage.isSelectedUserActive());


    }
}