package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.edit.EditPage;
import pages.service_groups.edit.ServiceGroupPopup;
import pages.login.LoginPage;
import pages.service_groups.search.SearchPage;
import utils.Generator;
import utils.rest.SMPRestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PrivilegesTests extends BaseTest {


	@AfterMethod
	public void logoutAndReset(){
		genericLogoutProcedure();
	}

	@Test(description = "RGT-0")
	public void anonymousUserRights(){
		logger.info("Checking rights for anonymous user");
		SoftAssert soft = new SoftAssert();

		SearchPage page = new SearchPage(driver);

		soft.assertTrue(page.sidebar.isSearchLnkEnabled(), "Search link is visible on Search page");
		soft.assertFalse(page.sidebar.isEditLnkEnabled(), "Edit link is NOT visible on Search page");
		soft.assertFalse(page.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible on Search page");
		soft.assertFalse(page.sidebar.isUsersLnkEnabled(), "Users link is NOT visible on Search page");

		logger.info("Going to the login page");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible on Login page");
		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled(), "Edit link is NOT visible on Login page");
		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible on Login page");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT visible on Login page");

		soft.assertAll();
	}


	@Test(description = "RGT-10")
	public void sg_adminRights(){

		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		SMPRestClient.createServiceGroup(pi, ps,
				new ArrayList<>(Arrays.asList("user")),
				new ArrayList<>(Arrays.asList(createdDomains.get(0)))
		);

		logger.info("Checking rights for SG_ADMIN user");
		SoftAssert soft = new SoftAssert();

		SMPPage page = new SMPPage(driver);
		logger.info("going to login");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		HashMap<String, String> user = testDataProvider.getUserWithRole("SG_ADMIN");

		logger.info("Logging in with user " + user.get("username"));
		loginPage.login(user.get("username"), user.get("password"));

		soft.assertTrue(loginPage.pageHeader.sandwichMenu.isLoggedIn(), "Check that the user is logged in");

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible after login for SG_ADMIN");
		soft.assertTrue(loginPage.sidebar.isEditLnkEnabled(), "Edit link is visible after login for SG_ADMIN");

		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible after login for SG_ADMIN");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT visible after login for SG_ADMIN");

//		going to check privileges on Edit page for SG_ADMIN
		logger.info("Going to edit page");
		EditPage editPage = loginPage.sidebar.goToPage(EditPage.class);
		soft.assertFalse(editPage.isNewButtonPresent(), "New button should not be present for SG_ADMIN");
		soft.assertFalse(editPage.isDeleteButtonPresent(), "Delete button should not be present for SG_ADMIN");

		logger.info("opening service group popup");
		editPage.getGrid().doubleClickRow(0);
		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertFalse(popup.isDomainsPanelEnabled(), "SG_ADMIN cannot edit a service groups DOMAINS");
		soft.assertFalse(popup.isOwnersPanelPresent(), "SG_ADMIN cannot edit a service groups OWNERS");
		popup.clickCancel();

		logger.info("LOGOUT");
		loginPage.pageHeader.sandwichMenu.logout();

		soft.assertFalse(loginPage.pageHeader.sandwichMenu.isLoggedIn(), "Check that the user is logged out");

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible after logout");
		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled(), "Edit link is NOT visible after logout");

		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible after logout");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT visible after logout");

		SMPRestClient.deleteSG(pi);

		soft.assertAll();
	}


	@Test(description = "RGT-20")
	public void sys_adminRights(){
		SoftAssert soft = new SoftAssert();

		SMPPage page = new SMPPage(driver);
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		HashMap<String, String> user = testDataProvider.getUserWithRole("SYS_ADMIN");
		loginPage.login(user.get("username"), user.get("password"));

		soft.assertTrue(loginPage.pageHeader.sandwichMenu.isLoggedIn());

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled());
		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled());

		soft.assertTrue(loginPage.sidebar.isDomainLnkEnabled());
		soft.assertTrue(loginPage.sidebar.isUsersLnkEnabled());


		soft.assertAll();
	}


	@Test(description = "RGT-30")
	public void smp_adminRights(){
		logger.info("Checking rights for SMP_ADMIN user");
		SoftAssert soft = new SoftAssert();

		SMPPage page = new SMPPage(driver);
		logger.info("going to login");
		page.pageHeader.goToLogin();

		LoginPage loginPage = new LoginPage(driver);
		HashMap<String, String> user = testDataProvider.getUserWithRole("SMP_ADMIN");

		logger.info("Logging in with user " + user.get("username"));
		loginPage.login(user.get("username"), user.get("password"));

		soft.assertTrue(loginPage.pageHeader.sandwichMenu.isLoggedIn(), "Check that the user is logged in");

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible after login for SMP_ADMIN");
		soft.assertTrue(loginPage.sidebar.isEditLnkEnabled(), "Edit link is visible after login for SMP_ADMIN");

		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible after login for SMP_ADMIN");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT visible after login for SMP_ADMIN");

//		going to check privileges on Edit page for SMP_ADMIN
		logger.info("Going to edit page");
		EditPage editPage = loginPage.sidebar.goToPage(EditPage.class);
		soft.assertTrue(editPage.isNewButtonPresent(), "New button should be present for SMP_ADMIN");
		soft.assertTrue(editPage.isDeleteButtonPresent(), "Delete button should be present for SMP_ADMIN");

		logger.info("opening service group popup");
		editPage.getGrid().doubleClickRow(0);
		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertTrue(popup.isDomainsPanelEnabled(), "SMP_ADMIN should be able to edit a service groups DOMAINS");
		soft.assertTrue(popup.isOwnersPanelEnabled(), "SMP_ADMIN should be able to edit a service groups OWNERS");
		popup.clickCancel();

		logger.info("LOGOUT");
		loginPage.pageHeader.sandwichMenu.logout();

		soft.assertFalse(loginPage.pageHeader.sandwichMenu.isLoggedIn(), "Check that the user is logged out");

		soft.assertTrue(loginPage.sidebar.isSearchLnkEnabled(), "Search link is visible after logout");
		soft.assertFalse(loginPage.sidebar.isEditLnkEnabled(), "Edit link is NOT visible after logout");

		soft.assertFalse(loginPage.sidebar.isDomainLnkEnabled(), "Domain link is NOT visible after logout");
		soft.assertFalse(loginPage.sidebar.isUsersLnkEnabled(), "Users link is NOT visible after logout");

		soft.assertAll();
	}


}
