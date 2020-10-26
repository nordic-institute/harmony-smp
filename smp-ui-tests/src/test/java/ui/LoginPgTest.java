package ui;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.messageArea.AlertMessage;
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

		log.info("deleting cookies");
		driver.manage().deleteAllCookies();

		try {
			log.info("clearing localstorage");
			((JavascriptExecutor) driver).executeScript("localStorage.clear();");
		} catch (Exception e) {
			log.info("clearing localcstorage failed");
		}


		SMPPage page = new SMPPage(driver);
		log.info("refreshing page to close all popups");
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

		soft.assertTrue(loginPage.sidebar.isSearchLnkVisible(), "Search link is visible");

		soft.assertFalse(loginPage.sidebar.isEditLnkVisible(), "Edit link is not visible");
		soft.assertFalse(loginPage.sidebar.isDomainLnkVisible(), "Domain link is not visible");
		soft.assertFalse(loginPage.sidebar.isUsersLnkVisible(), "Users link is not visible");

		soft.assertAll();
	}

	@Test(description = "LGN-30")
	public void successfulLogin() {
		SoftAssert soft = new SoftAssert();

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SYSTEM_ADMIN");
		log.info("created user " + username);

		SMPPage page = new SMPPage(driver);
		logger.info("Going to login page");
		page.pageHeader.goToLogin();

		log.info("trying to login with " + username);
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


		soft.assertTrue(loginPage.sidebar.isSearchLnkVisible(), "Search link is still available in the sidebar");
		soft.assertFalse(loginPage.sidebar.isEditLnkVisible(), "Edit link is NOT available in the sidebar");

		soft.assertFalse(loginPage.sidebar.isDomainLnkVisible(), "Domain link is NOT available in the sidebar");
		soft.assertFalse(loginPage.sidebar.isUsersLnkVisible(), "Users link is NOT available in the sidebar");


		soft.assertAll();
	}

	//	This will serve as a reminder to check this message manually
	@Test(description = "LGN-50")
	public void SMPNotRunningTest() {
		throw new SkipException("This test will be executed manually !!!");
	}


}
