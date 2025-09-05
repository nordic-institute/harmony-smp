package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.commonPages.commonAlertPage.CommonAlertDetailDialog;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.alertsPage.AlertsPage;
import rest.models.AlertModel;
import rest.models.UserModel;

import java.util.List;
import java.util.Optional;

public class AlertsPgTests extends SeleniumTest {
    DomiSMPPage homePage;
    UserModel adminUser;
    AlertsPage alertsPage;
    SoftAssert soft;
    LoginPage loginPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();

    }

    @Test(description = "ALR-1 - Page lists failed alerts related to login attempts, user suspended events and credentials expiration events")
    public void pageListsFailedAlertsRelatedToLoginAttemptsUserEventsCredentialExpirationEvents() throws Exception {
        UserModel userToTest = UserModel.generateUserWithADMINrole();
        rest.users().createUser(userToTest);

        loginPage.login(userToTest.getUsername(), "123123123");
        loginPage.login(userToTest.getUsername(), "123123123");
        loginPage.login(userToTest.getUsername(), "123123123");
        loginPage.login(userToTest.getUsername(), "123123123");
        loginPage.login(userToTest.getUsername(), "123123123");
        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        alertsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_ALERS);

        List<AlertModel> alertList = alertsPage.getAlertGrid().getAllAlertsByUsername(userToTest.getUsername());
        //Correct number of alert is shown when user is created and password is changed
        soft.assertEquals(alertList.stream().filter(alert -> alert.getAlertType().equals("USER_CREATED")).count(), 1, "User created alert is not present or wrong number of events are created!");
        soft.assertEquals(alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_CHANGED")).count(), 1, "Credential changed alert is not present or wrong number of events are created!");
        //Correct number of alert is shown when wrong credentials are entered and user is suspended
        soft.assertEquals(alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_VERIFICATION_FAILED")).count(), 4, "Credential verification failed alert is not present or wrong number of events are created!");
        soft.assertEquals(alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_SUSPENDED")).count(), 1, "Credential suspended is not present or wrong number of events are created!");

        //Verify grid is populated correctly
        Optional<AlertModel> credentialVerificationAlert = alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_VERIFICATION_FAILED")).findFirst();
        soft.assertEquals(credentialVerificationAlert.get().getAlertLevel(), "LOW", "Wrong Alert Level found in grid!");
        soft.assertEquals(credentialVerificationAlert.get().getForUser(), userToTest.getUsername(), "Wrong Username found in grid!");
        soft.assertEquals(credentialVerificationAlert.get().getCredentialType(), "USERNAME_PASSWORD", "Wrong Credential type found in grid!");
        soft.assertEquals(credentialVerificationAlert.get().getAlertType(), "CREDENTIAL_VERIFICATION_FAILED", "Wrong Alert Type found in grid!");
        soft.assertEquals(credentialVerificationAlert.get().getAlertStatus(), "SUCCESS", "Wrong Alert Status found in grid!");

        Optional<AlertModel> credentialSuspendedAlert = alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_SUSPENDED")).findFirst();
        soft.assertEquals(credentialSuspendedAlert.get().getAlertLevel(), "HIGH", "Wrong Alert Level found in Alert details dialog!");
        soft.assertEquals(credentialSuspendedAlert.get().getForUser(), userToTest.getUsername(), "Wrong For user found in Alert details dialog!");
        soft.assertEquals(credentialSuspendedAlert.get().getCredentialType(), "USERNAME_PASSWORD", "Wrong Credential type found in Alert details dialog!");
        soft.assertEquals(credentialSuspendedAlert.get().getAlertType(), "CREDENTIAL_SUSPENDED", "Wrong Alert type found in Alert details dialog!");
        soft.assertEquals(credentialSuspendedAlert.get().getAlertStatus(), "SUCCESS", "Wrong Alert Status found in Alert details dialog!");

        //Verify Alert details are populated corectly;
        CommonAlertDetailDialog alertDetailDialog = alertsPage.getAlertGrid().doubleClickMostRecentAlertByAlertType(userToTest.getUsername(), "CREDENTIAL_VERIFICATION_FAILED");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertLevel(), "LOW", "Wrong Alert Level found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getForUser(), userToTest.getUsername(), "Wrong For user found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getCredentialType(), "USERNAME_PASSWORD", "Wrong Credential type found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertType(), "CREDENTIAL_VERIFICATION_FAILED", "Wrong Alert type found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertStatus(), "SUCCESS", "Wrong Alert Status found in Alert details dialog!");
        alertDetailDialog.getCloseBtn().click();

        alertDetailDialog = alertsPage.getAlertGrid().doubleClickMostRecentAlertByAlertType(userToTest.getUsername(), "CREDENTIAL_SUSPENDED");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertLevel(), "HIGH", "Wrong Alert Level found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getForUser(), userToTest.getUsername(), "Wrong For user found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getCredentialType(), "USERNAME_PASSWORD", "Wrong Credential type found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertType(), "CREDENTIAL_SUSPENDED", "Wrong Alert type found in Alert details dialog!");
        soft.assertEquals(alertDetailDialog.getAlertDetails().getAlertStatus(), "SUCCESS", "Wrong Alert Status found in Alert details dialog!");
        alertDetailDialog.getCloseBtn().click();

        soft.assertAll();
    }

    @Test(description = "ALR-13 - Login failure alerts are generated based on property smp.alert.user.login_failure.enabled")
    public void loginFailureAlertsAreGeneratedBasedOnProperty() throws Exception {
        UserModel userToTest = UserModel.generateUserWithADMINrole();
        rest.users().createUser(userToTest);
        //Disable property and alerts are not generating
        JSONObject propertyRegex = rest.propertiesClient().getProperty("smp.alert.user.login");
        propertyRegex.put("value", "false");
        rest.propertiesClient().setPropertyValue(propertyRegex);
        loginPage.login(userToTest.getUsername(), "123123123");

        //Enable property and alerts are generating
        propertyRegex.put("value", "true");
        rest.propertiesClient().setPropertyValue(propertyRegex);
        loginPage.login(userToTest.getUsername(), "123123123");

        loginPage.login(adminUser.getUsername(), data.getNewPassword());

        alertsPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_ALERS);

        List<AlertModel> alertList = alertsPage.getAlertGrid().getAllAlertsByUsername(userToTest.getUsername());
        soft.assertEquals(alertList.stream().filter(alert -> alert.getAlertType().equals("CREDENTIAL_VERIFICATION_FAILED")).count(), 1, "Credential verification failed alert is not present or wrong number of events are created!");

        soft.assertAll();
    }
}

