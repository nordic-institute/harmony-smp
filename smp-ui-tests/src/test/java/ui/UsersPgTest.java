package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.components.messageArea.AlertMessage;
import pages.login.LoginPage;
import pages.service_groups.search.SearchPage;
import pages.users.UserPopup;
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
	public void logoutAndReset() {
		genericLogoutProcedure();
	}


	@BeforeMethod
	public void loginAndGoToUsersPage() {

		SMPPage page = genericLoginProcedure("SYS_ADMIN");

		logger.info("Going to Users page");
		page.sidebar.goToPage(UsersPage.class);
	}

	@Test(description = "USR-0")
	public void existingUserPasswordDialogView() {
		SoftAssert soft = new SoftAssert();
		String username = Generator.randomAlphaNumeric(10);
		String email = "system@gmail.com";
		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();
		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled");

		usersPage.clickSave().confirm();

		soft.assertTrue(!usersPage.alertArea.getAlertMessage().isError(), "Message listed is success");
		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");

		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");

		int index = usersPage.grid().scrollToUser(username);

		usersPage.grid().selectRow(index);
		popup = usersPage.clickEdit();

		popup.clickSetOrChangePassword();
		soft.assertTrue(popup.isAdminPasswordInputEnable(), "Admin password field input is not enabled after open password change popup for an existing user");
		soft.assertTrue(popup.isNewPasswordInputEnable(), "New password field input is not enabled after open password change popup for an existing user");
		soft.assertTrue(popup.isConfirmPasswordInputEnable(), "Confirm password field input is not enabled after open password change popup for an existing user");

		popup.clickClosePasswordDialog();
		popup.clickOK();
		usersPage.clickDelete();
		usersPage.waitForXMillis(200);
		soft.assertTrue(!usersPage.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		usersPage.clickSave().confirm();

		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!usersPage.grid().isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();

	}


	@Test(description = "USR-10")
	public void newUser() {
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";
		String email = "system@gmail.com";

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);


		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();


		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled");

		usersPage.clickSave().confirm();

		soft.assertTrue(!usersPage.alertArea.getAlertMessage().isError(), "Message listed is success");
		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");

		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");
		String adminPass = "123456";
		int index = usersPage.grid().scrollToUser(username);

		usersPage.grid().selectRow(index);
		popup = usersPage.clickEdit();
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		popup.clickSetOrChangePassword();
		popup.setOrChangePassword(adminPass, validPass, validPass);
		popup.clickChangedPassword();
		popup.clickCloseAfterChangedPass();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		popup.clickOK();
		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");

		soft.assertAll();
	}


	@Test(description = "USR-20")
	public void usernameValidation() {
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "QW!@qw12";

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm("tst", "smp@gmail.com");
		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");

		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(2)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(2)");
		popup.fillDetailsForm("#$^&*^%&$#@%@$#%$", "system@gmail.com");
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(3)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(3)");
		//noinspection SpellCheckingInspection
		popup.fillDetailsForm("QWERQWERQWERQWERQWERQWERQWERQWE33", "system@gmail.com");
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(4)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(4)");


		soft.assertAll();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test(description = "USR-30")
	public void passwordValidation() {
		String username = Generator.randomAlphaNumeric(10);
		String email = "system@gmail.com";
		ArrayList<String> passToValidate = new ArrayList<>(Arrays.asList("qwqw",
				"QWERQWERQWERQWERQWERQWERQWERQWE33"));

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		usersPage.clickVoidSpace();

		UserPopup popup = usersPage.clickNew();
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
		popup.clickOK();
		usersPage.clickSave().confirm();
		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");
		int index = usersPage.grid().scrollToUser(username);
		usersPage.grid().selectRow(index);
		String adminPass = "123456";
		for (String pass : passToValidate) {

			popup = usersPage.clickEdit();

			popup.clickSetOrChangePassword();
			popup.setOrChangePassword(adminPass, pass, pass);
			popup.clickClosePasswordDialog();
			popup.clickCancel();

			soft.assertTrue(!popup.isChangedPasswordActive(), String.format("ChangePassword button should be disabled until valid data is filled in the popup - %s ", pass));
		}

		soft.assertAll();
	}

	@Test(description = "USR-40")
	public void listedRoles() {

		ArrayList<String> expectedRoleValues = new ArrayList<>(Arrays.asList("SYSTEM_ADMIN", "SMP_ADMIN", "SERVICE_GROUP_ADMIN"));

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		List<String> listedRoles = popup.rolesSelect.getOptionTexts();

		soft.assertTrue(expectedRoleValues.size() == listedRoles.size(), "Number of roles is the same as expected");

		for (String expected : expectedRoleValues) {
			boolean found = false;
			for (String listedRole : listedRoles) {
				if (listedRole.equalsIgnoreCase(expected)) {
					found = true;
				}
			}
			soft.assertTrue(found, "Role found in page " + expected);
		}

		soft.assertAll();
	}

	@Test(description = "USR-50")
	public void deleteSYS_ADMIN() {

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SYSTEM_ADMIN");
		SoftAssert soft = new SoftAssert();

		logger.info("created user " + username);
		UsersPage page = new UsersPage(driver);
		page.refreshPage();

		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		soft.assertTrue(page.grid().isUserListed(username), "After canceling delete user is still listed");


		index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");
		soft.assertTrue(!page.grid().isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-60")
	public void changeRoleSYS_ADMIN() {

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		int index = page.grid().scrollToUserWithRole("SYSTEM_ADMIN");

		page.grid().selectRow(index);
		UserPopup popup = page.clickEdit();
		List<String> options = popup.rolesSelect.getOptionTexts();
		soft.assertTrue(options.size() == 1, "Role dropdown has only one value");
		soft.assertTrue(options.get(0).equalsIgnoreCase("SYSTEM_ADMIN"), "Role dropdown has only one value and that is \"SYSTEM_ADMIN\"");

		soft.assertAll();
	}

	@Test(description = "USR-70")
	public void changeRoleNON_SYS_ADMIN() {

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		int index = page.grid().scrollToUserWithRole("SMP_ADMIN");

		page.grid().selectRow(index);
		UserPopup popup = page.clickEdit();

		List<String> options = popup.rolesSelect.getOptionTexts();
		soft.assertTrue(options.size() == 2, "Role dropdown has only two values");
		soft.assertTrue(options.get(0).equalsIgnoreCase("SMP_ADMIN"), "Role dropdown has value \"SMP_ADMIN\"");
		soft.assertTrue(options.get(1).equalsIgnoreCase("SERVICE_GROUP_ADMIN"), "Role dropdown has value \"SERVICE_GROUP_ADMIN\"");
		page.refreshPage();

		int index2 = page.grid().scrollToUserWithRole("SERVICE_GROUP_ADMIN");

		page.grid().selectRow(index2);
		popup = page.clickEdit();

		options = popup.rolesSelect.getOptionTexts();
		soft.assertTrue(options.size() == 2, "Role dropdown has only two values");
		soft.assertTrue(options.get(0).equalsIgnoreCase("SMP_ADMIN"), "Role dropdown has value \"SMP_ADMIN\"");
		soft.assertTrue(options.get(1).equalsIgnoreCase("SERVICE_GROUP_ADMIN"), "Role dropdown has value \"SERVICE_GROUP_ADMIN\"");

		soft.assertAll();
	}

	@Test(description = "USR-80")
	public void deleteOWNUserRecord() {

		String username = new TestDataProvider().getUserWithRole("SYS_ADMIN").get("username");

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		AlertMessage message = page.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Listed message is error");
		soft.assertTrue(message.getMessage().equalsIgnoreCase(SMPMessages.USER_OWN_DELETE_ERR), "Listed message has appropriate text");

		soft.assertAll();
	}

	@Test(description = "USR-90")
	public void deleteSMP_ADMIN() {

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SMP_ADMIN");
		SoftAssert soft = new SoftAssert();

		logger.info("Created username " + username);


		UsersPage page = new UsersPage(driver);
		page.refreshPage();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		soft.assertTrue(page.grid().isUserListed(username), "After canceling delete user is still listed");


		index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		page.waitForXMillis(200);
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!page.grid().isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-100")
	public void deleteSERVICE_GROUP_ADMIN() {

		String username = Generator.randomAlphaNumeric(10);
		SMPRestClient.createUser(username, "SERVICE_GROUP_ADMIN");
		logger.info("Created username" + username);
		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		page.refreshPage();
		page.waitForRowsToLoad();

		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

		int index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		page.waitForRowsToLoad();

		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();
		page.waitForRowsToLoad();
		soft.assertTrue(page.grid().isUserListed(username), "After canceling delete user is still listed");


		index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);

		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		page.clickDelete();
		page.waitForRowsToLoad();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!page.grid().isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-110")
	public void deleteSG_ADMINWithSG() {

		String username = Generator.randomAlphaNumeric(10);
		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		String expectedErrorMess = String.format("Delete validation error Could not delete user with ownerships! User: %s owns SG count: 1.", username);

		SMPRestClient.createUser(username, "SERVICE_GROUP_ADMIN");
		SMPRestClient.createServiceGroup(pi, ps,
				new ArrayList<>(Arrays.asList(username)),
				new ArrayList<>(Arrays.asList(createdDomains.get(0)))
		);

		logger.info("Created username " + username);
		logger.info("Created service group " + pi);

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		page.refreshPage();

		int index = page.grid().scrollToUser(username);
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
	public void deleteSMP_ADMINWithSG() {

		String username = Generator.randomAlphaNumeric(10);
		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		String expectedErrorMess = String.format("Delete validation error Could not delete user with ownerships! User: %s owns SG count: 1.", username);

		SMPRestClient.createUser(username, "SMP_ADMIN");
		SMPRestClient.createServiceGroup(pi, ps,
				new ArrayList<>(Arrays.asList(username)),
				new ArrayList<>(Arrays.asList(createdDomains.get(0)))
		);

		logger.info("Created username " + username);

		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		page.refreshPage();

		int index = page.grid().scrollToUser(username);
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

	@Test(description = "USR-130")
	public void duplicateUserCreation() {
		SoftAssert soft = new SoftAssert();
		String userName = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";
		String email = "system@gmail.com";
		UsersPage page = new UsersPage(driver);

		soft.assertTrue(page.isNewButtonEnabled(), "New button should be enabled");

		UserPopup popup = page.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button is enabled before valid data is filled in the popup");
		popup.fillDetailsForm(userName, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
		page.clickSave().confirm();
		String adminPass = "123456";
		int index = page.grid().scrollToUser(userName);

		page.grid().selectRow(index);
		popup = page.clickEdit();
		popup.clickSetOrChangePassword();
		popup.setOrChangePassword(adminPass, validPass, validPass);
		popup.clickChangedPassword();
		popup.clickCloseAfterChangedPass();
		popup.clickCancel();
		soft.assertTrue(page.grid().isUserListed(userName), "User present in the page");

		page.clickNew();

		popup.fillDetailsForm(userName, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");

		soft.assertFalse(popup.isOKButtonActive(), "OK button is enabled after duplicate user name is filled in the popup");

		soft.assertTrue(popup.isDuplicateUserNameErrorMsgDisPlayed(), "The user page is not containing the expected error message");
		popup.clickCancel();
		soft.assertAll();
	}

	@Test(description = "USR-140")
	public void verifyPasswordDoNotMatch() {
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";
		String confirmPass = "AS@!gh12fxghfnh43546";
		String email = "system@gmail.com";
		String errorMsg = "Confirm valued does not match new password!";
		String adminPass = "123456";
		SoftAssert soft = new SoftAssert();
		UsersPage usersPage = new UsersPage(driver);
		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button is enabled before valid data is filled in the popup");
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
		popup.clickOK();
		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled");
		usersPage.clickSave().confirm();
		int index = usersPage.grid().scrollToUser(username);
		usersPage.grid().selectRow(index);
		popup = usersPage.clickEdit();
		popup.clickSetOrChangePassword();
		popup.setOrChangePassword(adminPass, validPass, confirmPass);
		soft.assertTrue(!popup.isChangePasswordButtonActive(), "password change button is enabled before valid data is filled in the popup(2)");
		soft.assertTrue(!popup.isPopupChangedPasswordEnabled(), "Change password option is not disable after giving the wrong data in cofirmation password");
		soft.assertAll();
	}

	@Test(description = "USR-150")
	public void verifySuspendedUserwithoutPassword() {
		String username = Generator.randomAlphaNumeric(10);
		String password = "Aabcdefghijklm1@";
		String email = "system@gmail.com";

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);

		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();

		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled");

		usersPage.clickSave().confirm();

		soft.assertTrue(!usersPage.alertArea.getAlertMessage().isError(), "Message listed is success");
		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");

		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");

		usersPage.pageHeader.sandwichMenu.logout();
		SearchPage page = new SearchPage(driver);
		logger.info("Going to login page");
		LoginPage loginPage = page.pageHeader.goToLogin();
		for (int i = 0; i < 5; i++) {
			loginPage.invalidLogin(username, password);
			loginPage.waitForXMillis(2000);
		}
		soft.assertTrue(loginPage.isLoginButtonEnable(), "Login Button Is Disabled");
		logger.info(loginPage.alertArea.getAlertMessage().getMessage());
		soft.assertTrue(loginPage.alertArea.getAlertMessage().getMessage().contains(SMPMessages.MSG_22), "Message listed is as expected");

		SMPPage smpPage = genericLoginProcedure("SYS_ADMIN");
		logger.info("Going to User page");
		smpPage.sidebar.goToPage(UsersPage.class);
		usersPage = new UsersPage(driver);
		int index = usersPage.grid().scrollToUser(username);
		usersPage.grid().selectRow(index);
		soft.assertTrue(usersPage.isDeleteButtonEnabled(), "Delete button is enabled after row select(2)");

		usersPage.clickDelete();
		usersPage.waitForXMillis(200);
		soft.assertTrue(!usersPage.isDeleteButtonEnabled(), "Delete button is not enabled after user is deleted(2)");
		soft.assertTrue(usersPage.isSaveButtonEnabled(), "Save button is enabled after user is deleted(2)");
		soft.assertTrue(usersPage.isCancelButtonEnabled(), "Cancel button is enabled after user is deleted(2)");

		usersPage.clickSave().confirm();

		soft.assertTrue(usersPage.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message is as expected");
		soft.assertTrue(!usersPage.grid().isUserListed(username), "After saving deleted user is not listed");

		soft.assertAll();
	}

	@Test(description = "USR-160")
	public void userPageUIAppearanceAndBehaviour() {
		SoftAssert soft = new SoftAssert();

		UsersPage page = new UsersPage(driver);
		soft.assertTrue(page.isLoaded());
		soft.assertTrue(page.isNewButtonEnabled(), "New button is not enabled");
		soft.assertTrue(page.isEditTruststoreButtonEnabled(), "EditTruststore Button is not enabled");
		soft.assertTrue(page.isDeleteButtonVisible(), "Delete button is not visible");
		soft.assertTrue(page.isEditButtonVisible(), "Edit button is not visible");
		soft.assertTrue(page.isSaveButtonVisible(), "Save button is not visible");
		soft.assertTrue(page.isCancelButtonVisible(), "Cancel button is not visible");
		soft.assertTrue(!page.isEditButtonEnabled(), "Edit button is  enabled");
		soft.assertTrue(!page.isSaveButtonEnabled(), "Save button is  enabled");
		soft.assertTrue(!page.isCancelButtonEnabled(), "Cancel button is  enabled");
		int index = 0;
		page.grid().selectRow(index);
		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is not enabled after row select(2)");
		soft.assertTrue(page.isEditButtonEnabled(), "Edit button is not enabled after row select(2)");


		soft.assertAll();
	}

	@Test(description = "USR-170")
	public void editUserUIAppearanceAndBehaviour() {
		SoftAssert soft = new SoftAssert();
		UsersPage page = new UsersPage(driver);
		soft.assertTrue(page.isLoaded());
		String username = Generator.randomAlphaNumeric(10);
		String email = "system@gmail.com";
		UserPopup popup = page.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm(username, email);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled");

		page.clickSave().confirm();

		soft.assertTrue(!page.alertArea.getAlertMessage().isError(), "Message listed is success");
		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18), "Message listed is as expected");

		int index = page.grid().scrollToUser(username);
		page.grid().selectRow(index);
		popup = page.clickEdit();
		soft.assertTrue(!popup.isUsernameFieldEnabled(), "Username field is enabled for an existing user");
		soft.assertTrue(popup.isEmailFieldEnabled(), "Email field is not enabled for an existing user");
		soft.assertTrue(popup.isRoleSelectFieldEnabled(), "Role field is not enabled for an existing user");
		soft.assertTrue(popup.isSetOrChangePassOptionBtnEnabled(), "SetOrChangePassOptionBtn field is not enabled for an existing user");
		soft.assertTrue(popup.isRegenerateAccesstokenBtnEnabled(), "Email field is not enabled for an existing user");
		soft.assertTrue(popup.isOKButtonActive(), "Ok btn is not enabled for an existing user in user popup");
		soft.assertTrue(popup.isCancelButtonActive(), "Cancel btn is not enabled for an existing user in user popup");
		soft.assertTrue(popup.isImportButtonActive(), "Import btn is not enabled for an existing user in user popup");
		if (page.grid().userHasCertificate(index)) {
			soft.assertTrue(popup.isShowDetailsButtonActive(), "ShowDetails btn is not enabled for an existing user in user popup");
			soft.assertTrue(popup.isClearButtonActive(), "Clear btn is not enabled for an existing user in user popup");
		} else {
			soft.assertTrue(!popup.isShowDetailsButtonActive(), "ShowDetails btn is enabled for an existing user in user popup");
			soft.assertTrue(!popup.isClearButtonActive(), "Clear btn is enabled for an existing user in user popup");

		}

		soft.assertAll();


	}

	@Test(description = "USR-180")
	public void userEmailValidation() {
		SoftAssert soft = new SoftAssert();
		UsersPage page = new UsersPage(driver);
		soft.assertTrue(page.isLoaded());
		String username = Generator.randomAlphaNumeric(10);
		ArrayList<String> email = new ArrayList<>(Arrays.asList("sdfdgfdghxvfv@gmail.c",
				"sdfdgfdghxvfv$gmail.com",
				"hdbvsdkvdsvf"));
		UserPopup popup = page.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		for (String emailId : email) {
			popup.fillDetailsForm(username, emailId);
			popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
			soft.assertTrue(!popup.isOKButtonActive(), "OK button is active after sending invalid email");
			soft.assertEquals(popup.userEmailValidationGetErrMsg(), SMPMessages.USER_EMAIL_VALIDATION_MESSAGE, "Message is not in the list");
		}
		soft.assertAll();
	}


}
