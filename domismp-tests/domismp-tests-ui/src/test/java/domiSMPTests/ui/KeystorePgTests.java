package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.KeyStoreTypes;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.keyStorePage.KeyStoreImportDialog;
import pages.systemSettings.keyStorePage.KeystorePage;
import rest.models.UserModel;
import utils.FileUtils;
import utils.Utils;

public class KeystorePgTests extends SeleniumTest {

    DomiSMPPage homePage;
    UserModel adminUser;
    KeystorePage keystorePage;
    SoftAssert soft;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        adminUser = UserModel.generateUserWithADMINrole();
        rest.users().createUser(adminUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(adminUser.getUsername(), data.getNewPassword());
        keystorePage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_KEYSTORE);
    }


    @Test(description = "KEYS-02 System admin is able to import JKS Keystore")
    public void systemAdminIsAbleToImportJKS() {
        String path = FileUtils.getAbsoluteKeystorePath("valid_keystore.jks");

        if(keystorePage.getLeftSideGrid().isValuePresentInColumn("Alias", "blue_gw")){
            keystorePage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "blue_gw");
            keystorePage.deleteandConfirm();
            keystorePage.getAlertMessageAndClose();
        }
        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "test123");
        keyStoreImportDialog.clickImport();
        String value = keystorePage.getAlertArea().getAlertMessage();
        String alias = Utils.getAliasFromMessage(value);
        keystorePage.getLeftSideGrid().searchAndClickElementInColumn("Alias", alias);
        soft.assertEquals(keystorePage.getPublicKeyTypeValue(), "RSA");
        soft.assertEquals(keystorePage.getSmpCertificateIdValue(), "CN=blue_gw,O=eDelivery,C=BE:e07b6b956330a19a");
        soft.assertEquals(keystorePage.getSubjectNameValue(), "C=BE,O=eDelivery,CN=blue_gw");
        // TODO:
        // soft.assertEquals(keystorePage.getValidFromValue(), "9/14/2017, 10:27:39 AM");
        // soft.assertEquals(keystorePage.getValidToValue(), "12/1/2025, 9:27:39 AM");
        soft.assertEquals(keystorePage.getIssuerValue(), "C=BE,O=eDelivery,CN=blue_gw");
        soft.assertEquals(keystorePage.getSerialNumberValue(), "e07b6b956330a19a");
        sofAssertThatContains("Certificates added [blue_gw", value);

        soft.assertAll();
    }

    @Test(description = "KEYS-03 System admin is NOT able to import keystore with wrong password")
    public void systemAdminIsNotAbleToImportWithWrongPassword() {
        String path = FileUtils.getAbsoluteKeystorePath("valid_keystore.jks");

        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "wrongPassword");
        keyStoreImportDialog.clickImport();

        String value = keystorePage.getAlertArea().getAlertMessage();
        sofAssertThatContains("Error occurred while importing keystore", value);
        soft.assertAll();
    }

    @Test(description = "KEYS-04 SSystem admin is NOT able to import duplicated keystore", priority = 1)
    public void systemAdminIsNOTAbleToImportDuplicatedKeyStores(){
        String path = FileUtils.getAbsoluteKeystorePath("valid_keystore.jks");
        try{
            keystorePage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "blue_gw");
            keystorePage.deleteandConfirm();
        } catch (Exception e) {

        }


        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "test123");
        keyStoreImportDialog.clickImport();
        String value = keystorePage.getAlertArea().getAlertMessage();
        String alias = Utils.getAliasFromMessage(value);
        soft.assertTrue(keystorePage.getLeftSideGrid().isValuePresentInColumn("Alias", alias));

        keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "test123");
        keyStoreImportDialog.clickImport();

        String duplicatedAlertMessage = keystorePage.getAlertArea().getAlertMessage();
        soft.assertTrue(duplicatedAlertMessage.contains("The following aliases have been ignored because they were already present in the current keystore:") );

        soft.assertAll();
    }

    private void sofAssertThatContains(String contains, String value) {
        soft.assertTrue(value.contains(contains), "Expected to contain: ["+contains+"] but the value was: ["+value+"]");
    }
}
