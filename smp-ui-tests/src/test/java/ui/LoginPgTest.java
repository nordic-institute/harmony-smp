package ui;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.messageArea.AlertMessage;
import pages.password.PasswordChangepopup;
import pages.service_groups.search.SearchPage;
import pages.components.baseComponents.SMPPage;
import pages.login.LoginPage;
import pages.users.UserPopup;
import pages.users.UsersPage;
import utils.Generator;
import utils.enums.SMPMessages;
import utils.rest.SMPRestClient;

import java.util.HashMap;

public class LoginPgTest extends BaseTest {


	@AfterMethod
	public void logoutAndReset() {

		logger.info("deleting cookies");
		driver.manage().deleteAllCookies();

		try {
			logger.info("clearing localstorage");
			((JavascriptExecutor) driver).executeScript("localStorage.clear();");
		} catch (Exception e) {
			logger.info("clearing localcstorage failed");
		}


		SMPPage page = new SMPPage(driver);
		logger.info("refreshing page to close all popups");
		page.refreshPage();

		try {
			if (page.pageHeader.sandwichMenu.isLoggedIn()) {
				logger.info("Logout!!");
				page.pageHeader.sandwichMenu.logout();
			}
			logger.info("Going to Search page");
			page.sidebar.goToPage(SearchPage.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(description = "LGN-0")
	public void loginPageNavigation() {

		SoftAssert soft = new SoftAssert();

		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();
		LoginPage loginPage = new LoginPage(driver);

		logger.info("Checking that login page is loaded correctly!");
		soft.assertTrue(loginPage.isLoaded(), "Login page elements are loaded!");

		soft.assertAll();
	}


	@Test(description = "LGN-10")
	public void loginPageBuildNumberTest() {
		SoftAssert soft = new SoftAssert();

		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		logger.info("Check that the area for the build version is not empty");
		soft.assertFalse(loginPage.getListedSMPVersion().isEmpty(), "Check that there is something in the build number area");

		soft.assertAll();
	}


	@Test(description = "LGN-20")
	public void loginPageDisplayTest() {
		SoftAssert soft = new SoftAssert();

		SearchPage page = new SearchPage(driver);

		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);

		soft.assertTrue(loginPage.isLoaded());

		soft.assertTrue(loginPage.getTextInPasswordInput().isEmpty(), "User input is empty by default");
		soft.assertTrue(loginPage.getTextInUsernameInput().isEmpty(), "Password input is empty by default");

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible");

		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled(), "Edit link is not visible");
		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is not visible");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is not visible");

		soft.assertAll();
	}

	@Test(description = "LGN-30")
	public void successfulLogin() {
		SoftAssert soft = new SoftAssert();

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SYSTEM_ADMIN");
		logger.info("created user " + username);

		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		logger.info("trying to login with " + username);
		LoginPage loginPage = new LoginPage(driver);
		SearchPage searchPage = loginPage.login(username, "QW!@qw12");

		soft.assertTrue(searchPage.pageHeader.sandwichMenu.isLoggedIn(), "User is logged in");
		soft.assertTrue(searchPage.isLoaded(), "Search page is loaded");

		soft.assertAll();
	}


	//	Tests that using invalid credentials leads to proper error message
	@Test(description = "LGN-40")
	public void unsuccessfulLogin() {
		SoftAssert soft = new SoftAssert();

		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);

		loginPage.invalidLogin("invalidUsername", "nonexistentPassword");

		AlertMessage message = loginPage.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Check message is error message");
		soft.assertEquals(message.getMessage(), SMPMessages.MSG_1, "Check the error message content");


		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is still available in the sidebar");
		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled(), "Edit link is NOT available in the sidebar");

		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT available in the sidebar");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT available in the sidebar");


		soft.assertAll();
	}

	@Test(description = "LGN-60")
	public void loginButtonDisableVerification() {
		SoftAssert soft = new SoftAssert();
		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		LoginPage loginPage = page.pageHeader.goToLogin();
		loginPage.loginWithoutUserAndPassword();
		soft.assertTrue(loginPage.isLoginButtonEnable(), "login button is enabled");
		soft.assertAll();
	}

	@Test(description = "LGN-70")
	public void verifyMenuButtonMsg() {
		SoftAssert soft = new SoftAssert();
		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		LoginPage loginPage = page.pageHeader.goToLogin();
		soft.assertFalse(loginPage.pageHeader.sandwichMenu.isLoggedIn(), "Menu does not contain the message 'Not logged in'");
		soft.assertAll();
	}

	@Test(description = "LGN-80")
	public void verifyLoginButtonEnable()
	{
		SoftAssert soft = new SoftAssert();
		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		LoginPage loginPage = page.pageHeader.goToLogin();
		HashMap<String, String> user = testDataProvider.getUserWithRole("SYS_ADMIN");
		loginPage.fillLoginInput(user.get("username"), user.get("password"));
		soft.assertFalse(loginPage.isLoginButtonEnable(), "Login Button Is Disabled");

		soft.assertAll();
	}

	@Test(description = "LGN-90")
	public void verifyRoleAfterLogin()
	{
		SoftAssert soft = new SoftAssert();

		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		HashMap<String, String> user = testDataProvider.getUserWithRole("SYS_ADMIN");
		SearchPage searchPage = loginPage.login(user.get("username"), user.get("password"));
		soft.assertTrue(searchPage.pageHeader.sandwichMenu.isLoggedIn(), "User is logged in");
		String roleName = page.pageHeader.getRoleName();
		soft.assertEquals(roleName , "System administrator" , "the role doesn't contain System administrator");

		soft.assertAll();
	}

	@Test(description = "LGN-100")
	public void loggedUserPasswordDialogView()
	{
		SoftAssert soft = new SoftAssert();


		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SYSTEM_ADMIN");
		logger.info("created user " + username);

		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		logger.info("trying to login with " + username);
		LoginPage loginPage = new LoginPage(driver);
		SearchPage searchPage = loginPage.login(username, "QW!@qw12");

		soft.assertTrue(searchPage.pageHeader.sandwichMenu.isLoggedIn(), "User is not logged in");
		soft.assertTrue(searchPage.isLoaded(), "Search page is not loaded");

		PasswordChangepopup passPopup = searchPage.pageHeader.sandwichMenu.clickChangePasswordOption();
		soft.assertTrue(passPopup.isCurrentPasswordInputEnable(),"Current password input is not enable in the password dialog for logged user");
		soft.assertTrue(passPopup.isNewPasswordInputEnable(),"New password input is not enable in the password dialog for logged user");
		soft.assertTrue(passPopup.isConfirmPasswordInputEnable(),"Confirm password input is not enable in the password dialog for logged user");

		searchPage = passPopup.clickClosePasswordDialog();

		soft.assertAll();

	}


	@Test(description = "LGN-100")
	public void passwordChangeForLoggedUser()
	{
		SoftAssert soft = new SoftAssert();
		String userName = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";

		SMPPage page = genericLoginProcedure("SYS_ADMIN");
		SMPRestClient.createUser(userName,"SMP_ADMIN");
		logger.info("created user " + userName);
		page.pageHeader.sandwichMenu.logout();
		page.pageHeader.goToLogin();
		LoginPage loginPage = new LoginPage(driver);
		SearchPage searchPage = loginPage.login(userName, "QW!@qw12");
		PasswordChangepopup passDialog = searchPage.pageHeader.sandwichMenu.clickChangePasswordOption();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		passDialog.fillDataForLoggedUser("QW!@qw12", validPass, validPass);
		passDialog.clickChangedPassword();
		 searchPage = passDialog.clickCloseAfterChangedPassForLoggedUser();
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
		}
		soft.assertTrue(searchPage.isLoaded(),"After changing the password for a logged user the page is not redirecting to searchpage");
		SMPPage page1 = genericLoginProcedure("SYS_ADMIN");
		logger.info("Going to Users page");
		page1.sidebar.goToPage(UsersPage.class);
		UsersPage usersPage = new UsersPage(driver);
		soft.assertTrue(usersPage.grid().isUserListed(userName), "User is not present in the page after changing the password");

		soft.assertAll();

	}
}