package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.userSettings.accessTokensPage.AccessTokensPage;
import pages.userSettings.accessTokensPage.CreateNewAccessTokenDialog;
import rest.models.UserModel;
import utils.Generator;
import utils.Utils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.HashMap;


public class AccessTokensPgTests extends SeleniumTest {

    SoftAssert soft;
    DomiSMPPage homePage;
    LoginPage loginPage;
    AccessTokensPage accessTokensPage;
    UserModel normalUser;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        accessTokensPage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_ACCESS_TOKEN);

        normalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(normalUser);

    }

    @Test(description = "ACCTOK-01 User is able to create valid Access Token")
    public void userIsAbleToCreateValidAccessToken() throws Exception {
        String currentDate = Utils.getCurrentDate("MM/dd/yyyy");
        String description = Generator.randomAlphaNumericValue(10);
        //Create new token
        CreateNewAccessTokenDialog createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();
        String tokenID = createNewAccessTokenDialog.getTokenIdAndCloseDialog();

        String currentDateUiFormat = Utils.getCurrentDate("M/d/yyyy");
        soft.assertTrue(accessTokensPage.isAccessTokenPresent(tokenID), "Access Token ID is not correct");
        HashMap<String, String> accessTokenInfo = accessTokensPage.getAccessTokenInfo(tokenID);
        soft.assertEquals(accessTokenInfo.get("Description"), description, "Access Token description is not correct");
        soft.assertEquals(accessTokenInfo.get("Active"), "true", "Access Token active status is not correct");
        soft.assertEquals(accessTokenInfo.get("StartDate"), currentDateUiFormat, "Access Token start date is not correct");
        soft.assertEquals(accessTokenInfo.get("EndDate"), currentDateUiFormat, "Access Token end date is not correct");
        soft.assertEquals(accessTokenInfo.get("SequenceFailedAttempts"), "0", "Access Token SequenceFailedAttempts is not correct");
        soft.assertEquals(accessTokenInfo.get("LastFailedAttempts"), "---", "Access Token LastFailedAttempts is not correct");
        soft.assertEquals(accessTokenInfo.get("SuspendedUntil"), "---", "Access Token SuspendedUntil is not correct");

        //Check if user role can create access tokens
        accessTokensPage.logout();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        accessTokensPage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_ACCESS_TOKEN);
        description = Generator.randomAlphaNumericValue(10);
        //Create new token
        createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();
        tokenID = createNewAccessTokenDialog.getTokenIdAndCloseDialog();


        soft.assertTrue(accessTokensPage.isAccessTokenPresent(tokenID), "Access Token ID is not correct");
        accessTokenInfo = accessTokensPage.getAccessTokenInfo(tokenID);
        soft.assertEquals(accessTokenInfo.get("Description"), description, "Access Token description is not correct");
        soft.assertEquals(accessTokenInfo.get("Active"), "true", "Access Token active status is not correct");
        soft.assertEquals(accessTokenInfo.get("StartDate"), currentDateUiFormat, "Access Token start date is not correct");
        soft.assertEquals(accessTokenInfo.get("EndDate"), currentDateUiFormat, "Access Token end date is not correct");
        soft.assertEquals(accessTokenInfo.get("SequenceFailedAttempts"), "0", "Access Token SequenceFailedAttempts is not correct");
        soft.assertEquals(accessTokenInfo.get("LastFailedAttempts"), "---", "Access Token LastFailedAttempts is not correct");
        soft.assertEquals(accessTokenInfo.get("SuspendedUntil"), "---", "Access Token SuspendedUntil is not correct");


        soft.assertAll();
    }

    @Test(description = "ACCTOK-04 User is able to Delete access token")
    public void userIsAbleToDeleteAccessToken() {
        String currentDate = Utils.getCurrentDate("MM/dd/yyyy");
        String description = Generator.randomAlphaNumericValue(10);
        //Create new token
        CreateNewAccessTokenDialog createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();
        String tokenID = createNewAccessTokenDialog.getTokenIdAndCloseDialog();
        String alertMessage = accessTokensPage.deleteAccessToken(tokenID);

        soft.assertEquals(alertMessage, "Access token \"" + tokenID + "\" has been deleted!", "Access Token ID is not correct");
        soft.assertFalse(accessTokensPage.isAccessTokenPresent(tokenID));
        soft.assertAll();
    }

    @Test(description = "ACCTOK-04 User is able to activate/inactivate existing access token")
    public void userIsAbleToActivateDeactivateExistingAccessToken() throws Exception {
        String currentDate = Utils.getCurrentDate("MM/dd/yyyy");
        String description = Generator.randomAlphaNumericValue(10);
        //Create new token
        CreateNewAccessTokenDialog createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();

        String tokenID = createNewAccessTokenDialog.getTokenIdAndCloseDialog();
        String alertMessage = accessTokensPage.modifyIsActiveForAccessToken(tokenID, false);
        //Set accesss token to inactive
        soft.assertEquals(alertMessage, "Access token \"" + tokenID + "\" has been updated!", "Access Token ID is not correct");
        HashMap<String, String> accessTokenInfo = accessTokensPage.getAccessTokenInfo(tokenID);
        soft.assertEquals(accessTokenInfo.get("Active"), "false", "Access Token active status is not correct");

        //Set accesss token to active

        accessTokensPage.modifyIsActiveForAccessToken(tokenID, true);
        accessTokenInfo = accessTokensPage.getAccessTokenInfo(tokenID);
        soft.assertEquals(accessTokenInfo.get("Active"), "true", "Access Token active status is not correct");

        //Check if user role can create inactivate tokens

        accessTokensPage.logout();
        loginPage.login(normalUser.getUsername(), data.getNewPassword());
        accessTokensPage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_ACCESS_TOKEN);

        description = Generator.randomAlphaNumericValue(10);
        //Create new token
        createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();

        tokenID = createNewAccessTokenDialog.getTokenIdAndCloseDialog();
        alertMessage = accessTokensPage.modifyIsActiveForAccessToken(tokenID, false);
        //Set accesss token to inactive
        soft.assertEquals(alertMessage, "Access token \"" + tokenID + "\" has been updated!", "Access Token ID is not correct");
        accessTokenInfo = accessTokensPage.getAccessTokenInfo(tokenID);
        soft.assertEquals(accessTokenInfo.get("Active"), "false", "Access Token active status is not correct");

        soft.assertAll();
    }


    @Test(description = "ACCTOK-07 User is able to press on Copy access token value after generating a new token")
    public void userIsAbleToPressOnCopyTokenValueAfterGeneratingANewToken() throws Exception {
        String currentDate = Utils.getCurrentDate("MM/dd/yyyy");
        String description = Generator.randomAlphaNumericValue(10);
        //Create new token
        CreateNewAccessTokenDialog createNewAccessTokenDialog = accessTokensPage.clickCreateAccessTokenBtn();
        createNewAccessTokenDialog.getDescriptionInput().fill(description);
        createNewAccessTokenDialog.getStartDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getEndDateInput().fill(currentDate, true);
        createNewAccessTokenDialog.getCreateNewTokenBtn().click();
        createNewAccessTokenDialog.getCopyAccessTokenValueBtn().click();
        String tokenID = createNewAccessTokenDialog.getTokenValueAndCloseDialog();
        //Get token value from clipboard
        String accessTokenClipboardValue = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        soft.assertEquals(accessTokenClipboardValue, tokenID, "Token value from clipboard is different than the token value from UI!");

        soft.assertAll();
    }

}
