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

	//	This will serve as a reminder to check this message manually
	@Test(description = "LGN-50")
	public void SMPNotRunningTest() {
		throw new SkipException("This test will be executed manually !!!");
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
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";
		SMPRestClient.createUser(username,"SYSTEM_ADMIN");
		logger.info("created user " + username);
		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();
		LoginPage loginPage = new LoginPage(driver);
		SearchPage searchPage = loginPage.login(username, "QW!@qw12");

		soft.assertTrue(searchPage.pageHeader.sandwichMenu.isLoggedIn(), "User is logged in");
		soft.assertTrue(searchPage.isLoaded(), "Search page is loaded");

		PasswordChangepopup passDialog = searchPage.pageHeader.sandwichMenu.clickChangePasswordOption();
		passDialog.fillDataForLoggedUser("QW!@qw12",validPass,validPass);

		passDialog.clickChangedPassword();
		/*SearchPage page = passDialog.clickCloseAfterChangedPassForLoggedUser();
		soft.assertEquals(page.getTitle(),"Search");*/
		//passDialog.clickOK();
		//soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");

		soft.assertAll();

	}
}
	/*SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
				page.pageHeader.goToLogin();

				LoginPage loginPage = new LoginPage(driver);
				HashMap<String, String> user = testDataProvider.getUserWithRole("SYS_ADMIN");
		SearchPage searchPage = loginPage.login(user.get("username"), user.get("password"));
		soft.assertTrue(searchPage.pageHeader.sandwichMenu.isLoggedIn(), "User is logged in");*/