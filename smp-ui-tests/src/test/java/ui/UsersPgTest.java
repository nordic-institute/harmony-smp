package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.components.messageArea.AlertMessage;
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

	@Test(description = "USR-10")
	public void newUser() {
		String username = Generator.randomAlphaNumeric(10);
		String validPass = "Aabcdefghijklm1@";

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);

//		soft.assertTrue(usersPage.isNewButtonEnabled(), "New button should be enabled");

		UserPopup popup = usersPage.clickNew();
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup");
		popup.fillDetailsForm(username);
		popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();

	    //popup.clickUserDetailsToggle();
		//popup.fillDetailsForm(username, validPass, validPass);


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
		try{
			Thread.sleep(10000);
		}
        catch(Exception e){
			e.printStackTrace();
		}
		popup.clickChangePassword();
	    popup.setPassword(adminPass,validPass,validPass);
		popup.clickChangedPassword().cancel();
		//popup.clickCloseAfterChangedPass();
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
		popup.fillDetailsForm("tst");
		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");

		//popup.clickUserDetailsToggle();

		//popup.fillDetailsForm("tst");
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(2)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(2)");
		popup.fillDetailsForm("#$^&*^%&$#@%@$#%$");
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(3)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(3)");
		//noinspection SpellCheckingInspection
		popup.fillDetailsForm("QWERQWERQWERQWERQWERQWERQWERQWE33");
		soft.assertTrue(!popup.isOKButtonActive(), "OK button should be disabled until valid data is filled in the popup(4)");
		soft.assertEquals(popup.getUsernameValidationError(), SMPMessages.USERNAME_VALIDATION_MESSAGE, "Validation error message is displayed(4)");


		soft.assertAll();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test(description = "USR-30")
	public void passwordValidation() {
		String username = Generator.randomAlphaNumeric(10);
		ArrayList<String> passToValidate = new ArrayList<>(Arrays.asList("qwqw",
				"QWERQWERQWERQWERQWERQWERQWERQWE33",
//				"QWERTYUIOP",
//				"qwertyuiop",
//				"321654987",
//				"~!@#$%^&*()_",
//				"~1Aa#",
				"~1a#2d2dds"));

		SoftAssert soft = new SoftAssert();

		UsersPage usersPage = new UsersPage(driver);
		usersPage.clickVoidSpace();

		UserPopup popup = usersPage.clickNew();
		popup.fillDetailsForm("test11");
		popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
		popup.clickOK();
		usersPage.clickSave().confirm();
		soft.assertTrue(usersPage.grid().isUserListed(username), "User present in the page");
		int index = usersPage.grid().scrollToUser(username);
		usersPage.grid().selectRow(index);
		String adminPass = "123456";
		for (String pass : passToValidate) {
//			usersPage.refreshPage();
			/*usersPage.clickVoidSpace();

			UserPopup popup = usersPage.clickNew();
			popup.fillDetailsForm("test11");
			popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
			popup.clickOK();

			usersPage.clickSave().confirm();
			soft.assertTrue(usersPage.grid().isUserListed("test11"), "User present in the page");*/

			popup = usersPage.clickEdit();

			popup.clickChangePassword();
			popup.setPassword(adminPass,pass,pass);
			popup.clickClosePasswordDialog();
			popup.clickCancel();
			//popup.clickChangedPassword();
			//popup.clickCloseAfterChangedPass();
			//popup.clickCancel();


			//popup.clickUserDetailsToggle();

			//popup.fillDetailsForm("test11", pass, pass);
			soft.assertTrue(!popup.isChangePasswordActive(), String.format("ChangePassword button should be disabled until valid data is filled in the popup - %s ", pass));
			soft.assertEquals(popup.getPassValidationError(), SMPMessages.PASS_POLICY_MESSAGE, String.format("Pass policy message is displayed - %s", pass));
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

        int index =page.grid(). scrollToUser(username);
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

		logger.info("Created username "+ username);

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

    @Test(description = "USR-121")
    public void duplicateUserCreation() {
        SoftAssert soft = new SoftAssert();
        String userName = Generator.randomAlphaNumeric(10);
        String validPass = "Aabcdefghijklm1@";
        UsersPage page = new UsersPage(driver);

        soft.assertTrue(page.isNewButtonEnabled(), "New button should be enabled");

        UserPopup popup = page.clickNew();
        soft.assertTrue(!popup.isOKButtonActive(), "OK button is enable before valid data is filled in the popup");
		popup.fillDetailsForm(userName);
        popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
		popup.clickOK();
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
		page.clickSave().confirm();
		String adminPass = "123456";
		int index = page.grid().scrollToUser(userName);

		page.grid().selectRow(index);
		popup = page.clickEdit();

		popup.clickChangePassword();
		popup.setPassword(adminPass,validPass,validPass);
		popup.clickChangedPassword();
		popup.clickCloseAfterChangedPass();
		popup.clickCancel();
		soft.assertTrue(page.grid().isUserListed(userName), "User present in the page");

		//popup.clickUserDetailsToggle();
        //popup.fillDetailsForm(userName,validPass,validPass);


        page.clickNew();

		popup.fillDetailsForm(userName);
        popup.rolesSelect.selectOptionWithText("SYSTEM_ADMIN");
       // popup.clickUserDetailsToggle();
        //popup.fillDetailsForm(userName);
        soft.assertFalse(popup.isOKButtonActive(), "OK button is enable after duplicate user name is filled in the popup");

        soft.assertTrue(popup.isDuplicateUserNameErrorMsgDisPlayed(), "The user page is not containing the expected error message");
		popup.clickCancel();
        soft.assertAll();
    }

    @Test(description = "USR-122")
    public void verifyPasswordDoNotMatch() {
        String username = Generator.randomAlphaNumeric(10);
        String validPass = "Aabcdefghijklm1@";
        String confirmPass = "AS@!gh12fxghfnh43546";
        String errorMsg = "Passwords do not match";
        SoftAssert soft = new SoftAssert();

        UsersPage usersPage = new UsersPage(driver);
        UserPopup popup = usersPage.clickNew();
        soft.assertTrue(!popup.isOKButtonActive(), "OK button is enable before valid data is filled in the popup");
		popup.fillDetailsForm(username);
        popup.rolesSelect.selectOptionWithText("SMP_ADMIN");
		popup.clickOK();
		String adminPass = "123456";
		int index = usersPage.grid().scrollToUser(username);

		usersPage.grid().selectRow(index);
		popup = usersPage.clickEdit();

		popup.clickChangePassword();
		popup.setPassword(adminPass,validPass,confirmPass);
       // popup.clickUserDetailsToggle();
		// popup.fillDetailsForm(username,validPass,confirmPass);
        soft.assertTrue(!popup.isChangePasswordButtonActive(), "password change button is enabled before valid data is filled in the popup(2)");
       // soft.assertEquals(popup.getPassDontMatchValidationMsg(), errorMsg, "confirmation input does not contain the message 'Passwords do not match' .");
        soft.assertAll();
    }

}
