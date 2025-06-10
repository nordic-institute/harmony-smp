package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.userSettings.certificatesPage.accessTokensPage.CertificatesPage;
import pages.userSettings.certificatesPage.accessTokensPage.ImportNewCertificatesDialog;
import rest.models.UserModel;
import utils.FileUtils;
import utils.Generator;

import java.util.HashMap;


public class CertificatesPgTests extends SeleniumTest {

    SoftAssert soft;
    DomiSMPPage homePage;
    LoginPage loginPage;
    CertificatesPage certificatePage;
    UserModel normalUser;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        soft = new SoftAssert();
        homePage = new DomiSMPPage(driver);
        loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));
        certificatePage = homePage.getSidebar().navigateTo(Pages.USER_SETTINGS_CERTIFICATES);

        normalUser = UserModel.generateUserWithUSERrole();
        rest.users().createUser(normalUser);

    }

    @Test(description = "CERT-01 Users are able to import valid certificate, CERT-07 User is able to show Certificate details")
    public void usersAreAbleToImportValidCertificate() throws Exception {
        String description = Generator.randomAlphaNumericValue(10);
        String path = FileUtils.getAbsoluteTruststorePath("validCertificate.cer");
        String certificateId = "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8";
        //Delete certificate if exists
        try {
            certificatePage.deleteCertificate(certificateId);
        } catch (Exception ignored) {

        }
        //Import new certificate
        ImportNewCertificatesDialog importNewCertificatesDialog = certificatePage.clickOnImportNewCertificate();
        importNewCertificatesDialog.getDescriptionInput().fill(description);

        importNewCertificatesDialog.importCertificate(path);
        importNewCertificatesDialog.getSaveCertificateBtn().click();
        certificatePage.getAlertArea().closeAlert();
        soft.assertTrue(certificatePage.isCertificatePresent(certificateId));

        HashMap<String, String> certificateInfo = certificatePage.getCertificateInfo(certificateId);
        soft.assertEquals(certificateInfo.get("Description"), description, "Access Token description is not correct");
        soft.assertEquals(certificateInfo.get("Active"), "true", "Access Token active status is not correct");
        soft.assertEquals(certificateInfo.get("StartDate"), "3/23/2023", "Access Token start date is not correct");
        soft.assertEquals(certificateInfo.get("EndDate"), "3/22/2033", "Access Token end date is not correct");
        //Validate certificate details
        HashMap<String, String> certificateIDetailsnfo = certificatePage.getCertificateDetailsInfo(certificateId);
        soft.assertEquals(certificateIDetailsnfo.get("SmpCertificateId"), "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8", "Certificate id is not correct");
        soft.assertEquals(certificateIDetailsnfo.get("SubjectName"), "C=BE,O=eDelivery,CN=red_gw", "Certificate subject is not correct");
        soft.assertTrue(certificateIDetailsnfo.get("ValidFromDate").startsWith("3/23/23"), "Certificate start date is not correct");
        soft.assertTrue(certificateIDetailsnfo.get("ValidToDate").startsWith("3/22/33"), "Certificate end date is not correct");
        soft.assertEquals(certificateIDetailsnfo.get("Issuer"), "C=BE,O=eDelivery,CN=red_gw", "Certificate issuer is not correct");
        soft.assertEquals(certificateIDetailsnfo.get("SerialNumber"), "110fa0d8", "Certificate serial number is not correct");
        soft.assertAll();
    }

    @Test(description = "CERT-03 User is not able to add duplicated certificate")
    public void userIsNotAbleToAddDuplicatedCertificate() {

        String description = Generator.randomAlphaNumericValue(10);
        String path = FileUtils.getAbsoluteTruststorePath("validCertificate.cer");
        String certificateId = "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8";
        //Delete certificate if exists
        try {
            certificatePage.deleteCertificate(certificateId);
        } catch (Exception ignored) {

        }
        //Import new certificate
        ImportNewCertificatesDialog importNewCertificatesDialog = certificatePage.clickOnImportNewCertificate();
        importNewCertificatesDialog.getDescriptionInput().fill(description);

        importNewCertificatesDialog.importCertificate(path);
        importNewCertificatesDialog.getSaveCertificateBtn().click();
        soft.assertTrue(certificatePage.isCertificatePresent(certificateId));

        //Try to import duplicated certificate
        //Import new certificate
        importNewCertificatesDialog = certificatePage.clickOnImportNewCertificate();
        importNewCertificatesDialog.getDescriptionInput().fill(description);

        importNewCertificatesDialog.importCertificate(path);
        soft.assertEquals(importNewCertificatesDialog.getAlertMessage(), "Certificate with the same Subject is already registered!");
        soft.assertFalse(importNewCertificatesDialog.getSaveCertificateBtn().isEnabled());

        soft.assertAll();
    }

    @Test(description = "CERT-04 User is able to delete certificate")
    public void userIsAbleToDeleteCertificate() {

        String description = Generator.randomAlphaNumericValue(10);
        String path = FileUtils.getAbsoluteTruststorePath("validCertificate.cer");
        String certificateId = "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8";
        //Delete certificate if exists
        try {
            certificatePage.deleteCertificate(certificateId);
            certificatePage.getAlertArea().closeAlert();
        } catch (Exception ignored) {

        }
        //Import new certificate
        ImportNewCertificatesDialog importNewCertificatesDialog = certificatePage.clickOnImportNewCertificate();
        importNewCertificatesDialog.getDescriptionInput().fill(description);

        importNewCertificatesDialog.importCertificate(path);
        importNewCertificatesDialog.getSaveCertificateBtn().click();
        certificatePage.getAlertArea().closeAlert();
        soft.assertTrue(certificatePage.isCertificatePresent(certificateId));
        String deleteAlert = certificatePage.deleteCertificate(certificateId);
        soft.assertEquals(deleteAlert, "Certificate \"" + certificateId + "\" has been deleted!", "Certificate has not been deleted");
        soft.assertFalse(certificatePage.isCertificatePresent(certificateId));

        soft.assertAll();
    }

    @Test(description = "CERT-05 User is to modify existing Certificate")
    public void userIsModifyExistingCertificate() throws Exception {

        String description = Generator.randomAlphaNumericValue(10);
        String path = FileUtils.getAbsoluteTruststorePath("validCertificate.cer");
        String certificateId = "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8";
        //Delete certificate if exists
        try {
            certificatePage.deleteCertificate(certificateId);
            certificatePage.getAlertArea().closeAlert();
        } catch (Exception ignored) {

        }
        //Import new certificate
        ImportNewCertificatesDialog importNewCertificatesDialog = certificatePage.clickOnImportNewCertificate();
        importNewCertificatesDialog.getDescriptionInput().fill(description);

        importNewCertificatesDialog.importCertificate(path);
        importNewCertificatesDialog.getSaveCertificateBtn().click();
        certificatePage.getAlertArea().closeAlert();
        certificatePage.getDescriptionInput(certificateId).fill("test value");
        certificatePage.getActiveCheckbox(certificateId).uncheck();
        soft.assertTrue(certificatePage.getSaveBtn(certificateId).isEnabled(), "Save button is not enabled");
        String alertmessage = certificatePage.saveChanges(certificateId);
        soft.assertEquals(alertmessage, "Certificate \"CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8\" has been updated!", "Alert message is wrong!");
        soft.assertEquals(certificatePage.getCertificateInfo(certificateId).get("Description"), "test value");
        soft.assertFalse(certificatePage.getActiveCheckbox(certificateId).isChecked());



        soft.assertAll();
    }


}
