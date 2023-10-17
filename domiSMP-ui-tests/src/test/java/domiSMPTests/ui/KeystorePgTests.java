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


    @Test(description = "KEYS-02 System admin is able to import JKS Keystore", priority = 0)
    public void systemAdminIsAbleToImportJKS() throws Exception {
        String path = FileUtils.getAbsoluteKeystorePath("expired_keystore_JKS.jks");

        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "test1234");
        keyStoreImportDialog.clickImport();
        String value =  keystorePage.getAlertArea().getAlertMessage();
        String alias = Utils.getAliasFromMessage(value);
        keystorePage.getGrid().searchAndClickElementInColumn("Alias", alias);
        soft.assertEquals(keystorePage.getPublicKeyTypeValue(),"RSA");
        soft.assertEquals(keystorePage.getSmpCertificateIdValue(), "CN=blue_gw,O=edelivery,C=BE:00000000645901cb");
        soft.assertEquals(keystorePage.getSubjectNameValue(),"CN=blue_gw,O=edelivery,C=BE" );
        soft.assertEquals(keystorePage.getValidFromValue(),"5/8/2023, 5:06:03 PM");
        soft.assertEquals(keystorePage.getValidToValue(), "5/1/2023, 5:06:03 PM");
        soft.assertEquals(keystorePage.getIssuerValue(), "CN=blue_gw,O=edelivery,C=BE");
        soft.assertEquals(keystorePage.getSerialNumberValue(),"645901cb");
        sofAssertThatContains("Certificates added [blue_gw", value);

        soft.assertAll();
    }

    @Test(description = "KEYS-xx Wrong keystore type")
    public void systemAdminImportFailedWithWrongKeystoreType() throws Exception {
        String path = FileUtils.getAbsoluteKeystorePath("expired_keystore_JKS.jks");

        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.PKCS12, "test1234");
        keyStoreImportDialog.clickImport();

        String value =  keystorePage.getAlertArea().getAlertMessage();
        sofAssertThatContains("Error occurred while importing keystore", value);
        soft.assertAll();
    }

    @Test(description = "KEYS-xx Wrong keystore password")
    public void systemAdminImportFailedWithWrongPassword() throws Exception {
        String path = FileUtils.getAbsoluteKeystorePath("expired_keystore_JKS.jks");

        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "wrongPassword");
        keyStoreImportDialog.clickImport();

        String value =  keystorePage.getAlertArea().getAlertMessage();
        sofAssertThatContains("Error occurred while importing keystore", value);
        soft.assertAll();
    }

    private void sofAssertThatContains(String contains, String value) {
        soft.assertTrue(value.contains(contains), "Expected to contain: ["+contains+"] but the value was: ["+value+"]");
    }
}
