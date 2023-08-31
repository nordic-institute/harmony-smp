package domiSMPTests.ui;

import ddsl.DomiSMPPage;
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

    //TODO: wait until the mat-select for certificate type is changed to select
    @Test(description = "KEYS-02 System admin is able to import PKCS 12 Keystore")
    public void SystemAdminIsAbleToImportPKCS12() throws Exception {
        String path = FileUtils.getAbsolutePath("./src/main/resources/keystore/expired_keystore_JKS.jks");

        KeyStoreImportDialog keyStoreImportDialog = keystorePage.clickImportkeyStoreBtn();
        //keyStoreImportDialog.addCertificate(path, KeyStoreTypes.JKS, "test123");
        //keyStoreImportDialog.clickImport();


    }

}
