package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.components.messageArea.AlertMessage;
import pages.users.UserPopup;
import pages.users.UserRowInfo;
import pages.users.UsersPage;
import utils.Generator;
import utils.TestDataProvider;
import utils.enums.SMPMessages;
import utils.rest.SMPRestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersPgTest extends BaseTest {
	
	
	@AfterMethod
	public void logoutAndReset(){
		SMPPage page = new SMPPage(driver);
		page.refreshPage();
		
		if(page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Logout!!");
			page.pageHeader.sandwichMenu.logout();
		}
	}
	
	
	@BeforeMethod
	public void loginAndGoToUsersPage(){
		
		SMPPage page = new SMPPage(driver);

		if(page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Logout!!");
			page.pageHeader.sandwichMenu.logout();
		}
		
		if(!page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Login!!");
			page.pageHeader.goToLogin().login("SYS_ADMIN");
		}
		
		logger.info("Going to Users page");
		page.sidebar.goToPage(UsersPage.class);
	}
	
	@Test(description = "USR-10")
	public void newUser(){
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "QW!@qw12";
		
		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		
//		soft.assertTrue(usersPage.isNewButtonEnabled(), "New button should be enabled");
		
		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		
		popup.clickUserDetailsToggle();

		popup.fillDetailsForm(username, validPass, validPass);
		popup.clickOK();

		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled");

		usersPage.clickSave().confirm();

		soft.assertTrue(!usersPage.alertArea.getAlertMessage().isError(), "Message listed is success");
		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");

		soft.assertTrue(isUserListed(username), "User present in the page");

		soft.assertAll();
	}


	@Test(description = "USR-20")
	public void usernameValidation(){
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "QW!@qw12";

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");

		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");

		popup.clickUserDetailsToggle();

		popup.fillDetailsForm("tst", validPass, validPass);
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(2)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(2)");
		popup.fillDetailsForm("#$^&*^%&$#@%@$#%$", validPass, validPass);
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(3)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(3)");
		//noinspection SpellCheckingInspection
		popup.fillDetailsForm("QWERQWERQWERQWERQWERQWERQWERQWE33", validPass, validPass);
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(4)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(4)");


		soft.assertAll();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test(description = "USR-30")
	public void passwordValidation(){

		ArrayList<String> passToValidate = new ArrayList<>(Arrays.asList("qwqw",
//				"QWERQWERQWERQWERQWERQWERQWERQWE33",
//				"QWERTYUIOP",
//				"qwertyuiop",
//				"321654987",
//				"~!@#$%^&*()_",
//				"~1Aa#",
				"~1a#2d2dds"));

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);

		for (String pass : passToValidate) {
			usersPage.refreshPage();
//			usersPage.clickVoidSpace();
//			usersPage.waitForXMillis(5000);
			UserPopup popup = usersPage.clickNew();
			popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
			popup.clickUserDetailsToggle();

			popup.fillDetailsForm("test11", pass, pass);
			soft.assertTrue(!popup.isOKButtonActive(), String.format("OK button should be disabled until valid data is filled in the popup - %s ", pass));
			soft.assertEquals(popup.getPassValidationError(), SMPMessages.PASS_POLICY_MESSAGE, String.format("Pass policy message is displayed - %s", pass));
		}

		soft.assertAll();
	}

	@Test(description = "USR-40")
	public void listedRoles(){

		ArrayList<String> expectedRoleValues = new ArrayList<>(Arrays.asList("SYSTEM_ADMIN", "SMP_ADMIN", "SERVICE_GROUP_ADMIN"));

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		List<String> listedRoles = popup.rolesSelect.getOptionTexts();

		soft.assertTrue(expectedRoleValues.size() == listedRoles.size(), "Number of roles is the same as expected");

		for (String expected : expectedRoleValues) {
			boolean found = false;
			for (String listedRole : listedRoles) {
				if(listedRole.equalsIgnoreCase(expected)){
					found = true;
				}
			}
			soft.assertTrue(found, "Role found in page " + expected);
		}

		soft.assertAll();
	}

	@Test(description = "USR-50")
	public void deleteSYS_ADMIN(){

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SYSTEM_ADMIN");
		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		soft.assertTrue(isUserListed(username), "After canceling delete user is still listed");


		index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");
		soft.assertTrue(!isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-60")
	public void changeRoleSYS_ADMIN(){

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		int index = scrollToUserWithRole("SYSTEM_ADMIN");

		page.grid().selectRow(index);
		UserPopup popup = page.clickEdit();
		List<String> options = popup.rolesSelect.getOptionTexts();
		soft.assertTrue(options.size() == 1, "Role dropdown has only one value");
		soft.assertTrue(options.get(0).equalsIgnoreCase("SYSTEM_ADMIN"), "Role dropdown has only one value and that is \"SYSTEM_ADMIN\"");

		soft.assertAll();
	}

	@Test(description = "USR-70")
	public void changeRoleNON_SYS_ADMIN(){

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		int index = scrollToUserWithRole("SMP_ADMIN");

		page.grid().selectRow(index);
		UserPopup popup = page.clickEdit();

		List<String> options = popup.rolesSelect.getOptionTexts();
		soft.assertTrue(options.size() == 2, "Role dropdown has only two values");
		soft.assertTrue(options.get(0).equalsIgnoreCase("SMP_ADMIN"), "Role dropdown has value \"SMP_ADMIN\"");
		soft.assertTrue(options.get(1).equalsIgnoreCase("SERVICE_GROUP_ADMIN"), "Role dropdown has value \"SERVICE_GROUP_ADMIN\"");

		soft.assertAll();
	}

	@Test(description = "USR-80")
	public void deleteOWNUserRecord(){

		String username = new TestDataProvider().getUserWithRole("SYS_ADMIN").get("username");

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		AlertMessage message = page.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Listed message is error");
		soft.assertTrue(message.getMessage().equalsIgnoreCase(SMPMessages.USER_OWN_DELETE_ERR), "Listed message has appropriate text");

		soft.assertAll();
	}

	@Test(description = "USR-90")
	public void deleteSMP_ADMIN(){

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SMP_ADMIN");
		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		soft.assertTrue(isUserListed(username), "After canceling delete user is still listed");


		index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		page.waitForXMillis(200);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-100")
	public void deleteSERVICE_GROUP_ADMIN(){

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SERVICE_GROUP_ADMIN");
		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		soft.assertTrue(isUserListed(username), "After canceling delete user is still listed");


		index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-110")
	public void deleteSG_ADMINWithSG(){

		String username = Generator.randomAlphaNumeric(10);
		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		String expectedErrorMess = String.format("Delete validation error Could not delete user with ownerships! User: %s owns SG count: 1.", username);

		SMPRestClient.createUser(username, "SERVICE_GROUP_ADMIN");
		SMPRestClient.createServiceGroup(pi, ps,
				new ArrayList<>(Arrays.asList(username)),
				new ArrayList<>(Arrays.asList(createdDomains.get(0)))
		);

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		AlertMessage message = page.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Page shows error message when deleting user with SG");
		soft.assertTrue(message.getMessage().equalsIgnoreCase(expectedErrorMess), "Desired message appears");

		SMPRestClient.deleteSG(pi);
		SMPRestClient.deleteUser(username);

		soft.assertAll();
	}

	@Test(description = "USR-120")
	public void deleteSMP_ADMINWithSG(){

		String username = Generator.randomAlphaNumeric(10);
		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		String expectedErrorMess = String.format("Delete validation error Could not delete user with ownerships! User: %s owns SG count: 1.", username);

		SMPRestClient.createUser(username, "SMP_ADMIN");
		SMPRestClient.createServiceGroup(pi, ps,
				new ArrayList<>(Arrays.asList(username)),
				new ArrayList<>(Arrays.asList("domainNoble", "domainEPREL"))
		);

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);

		int index = scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		page.waitForXMillis(500);
		AlertMessage message = page.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Page shows error message when deleting user with SG");
		soft.assertTrue(message.getMessage().equalsIgnoreCase(expectedErrorMess), "Desired message appears");

		SMPRestClient.deleteSG(pi);
		SMPRestClient.deleteUser(username);

		soft.assertAll();
	}




	private boolean isUserListed(String username){
		boolean end = false;

		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		while (!end) {
			page = new UsersPage(driver);
			List<UserRowInfo> rows = page.grid().getRows();

			for (UserRowInfo row : rows) {
				if(row.getUsername().equalsIgnoreCase(username)){
					return true;
				}
			}

			if(page.pagination.hasNextPage()){
				page.pagination.goToNextPage();
			}else{end = true;}
		}

		return false;
	}

	private int scrollToUser(String username){
		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			page = new UsersPage(driver);

			List<UserRowInfo> rows = page.grid().getRows();
			for (int i = 0; i < rows.size(); i++) {
				if(rows.get(i).getUsername().equalsIgnoreCase(username)){
					return i;
				}
			}

			if(page.pagination.hasNextPage()){
				page.pagination.goToNextPage();
			}else{end = true;}
		}

		return -1;
	}

	private int scrollToUserWithRole(String role){
		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			page = new UsersPage(driver);

			List<UserRowInfo> rows = page.grid().getRows();
			for (int i = 0; i < rows.size(); i++) {
				if(rows.get(i).getRole().equalsIgnoreCase(role)){
					return i;
				}
			}

			if(page.pagination.hasNextPage()){
				page.pagination.goToNextPage();
			}else{end = true;}
		}

		return -1;
	}



}
