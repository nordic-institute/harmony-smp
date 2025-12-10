package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.TruststorePage;
import utils.FileUtils;

public class TrustorePgTests extends SeleniumTest {


    @Test(description = "TRST-01 System admin is able to import certificates")
    public void systemAdminIsAbleToImportCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/validCertificate.cer");

        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertNotNull(certificateALias);
        soft.assertEquals(truststorepage.getPublicKeyTypeValue(), "RSA");
        soft.assertEquals(truststorepage.getAliasIdValue(), certificateALias);
        soft.assertEquals(truststorepage.getSmpCertificateIdValue(), "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8");
        soft.assertEquals(truststorepage.getSubjectNameValue(), "C=BE,O=eDelivery,CN=red_gw");
        // TODO: set date validation Locale independent. Currently it fails when CEST and CET changes
        //soft.assertEquals(truststorepage.getValidFromValue(), "23/3/2023, 10:49:22");
        //soft.assertEquals(truststorepage.getValidToValue(), "22/3/2033, 10:49:22");
        soft.assertEquals(truststorepage.getIssuerValue(), "C=BE,O=eDelivery,CN=red_gw");
        soft.assertEquals(truststorepage.getSerialNumberValue(), "110fa0d8");
        soft.assertAll();
    }

    @Test(description = "TRST-02 System admin is able to import certificates")
    public void systemAdminIsAbleToImportDuplicatedCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/validCertificate.cer");
        try {
            truststorepage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "red_gw");
            truststorepage.deleteandConfirm();
            truststorepage.getAlertMessageAndClose();

        } catch (Exception e) {
            LOG.debug("Certificate was not present. Continue with the test");

        }
        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", certificateALias), "Added certificate is present in the grid.");
        soft.assertNotNull(certificateALias, "Certificate alias is not null");

        String duplicatedCertificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertNotNull(duplicatedCertificateALias, "Alias for duplicated certificate is not null");
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", duplicatedCertificateALias), "Added duplicated certificate is present in the grid.");
        soft.assertAll();

    }

    @Test(description = "TRST-03 System admin is NOT able to import invalid certificates")
    public void systemAdminIsNotAbleToImportInvalidCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/invalidCertificate.cer");
        String invalidCertificateErrorMessage = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertEquals(invalidCertificateErrorMessage, "Error: Error occurred while parsing certificate. Is certificate valid!");
        soft.assertAll();
    }

    @Test(description = "TRST-04 System admin is able to delete certificates")
    public void systemAdminIsAbleToDeleteCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/validCertificate.cer");
        try {
            truststorepage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "red_gw");
            truststorepage.deleteandConfirm();
            truststorepage.getAlertMessageAndClose();
        } catch (Exception e) {
            LOG.debug("Certificate was not present. Continue with the test");
        }
        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", certificateALias));
        soft.assertNotNull(certificateALias);

        truststorepage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "red_gw");
        truststorepage.deleteandConfirm();
        String deleteCertificateMessage = truststorepage.getAlertMessageAndClose();
        soft.assertEquals(deleteCertificateMessage, String.format("Certificate: [CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8] with alias [%s] is removed!", certificateALias));
        soft.assertFalse(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", certificateALias));
        soft.assertAll();
    }

    @Test(description = "TRST-05 System admin is able to import expired certificates")
    public void systemAdminItAbleToImportExpiredCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/expiredCertificate.cer");
        try {
            truststorepage.getLeftSideGrid().searchAndClickElementInColumn("Alias", "te");
            truststorepage.deleteandConfirm();
            truststorepage.getAlertMessageAndClose();

        } catch (Exception e) {
            LOG.debug("Certificate was not present. Continue with the test");

        }
        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", certificateALias));
        soft.assertNotNull(certificateALias);

        truststorepage.getLeftSideGrid().searchAndGetElementInColumn("Alias", certificateALias).click();
        soft.assertEquals(truststorepage.getExpiredWarningValue(), "Invalid certificate: Certificate is expired!", "Expired warning is not appearing");


        soft.assertAll();
    }


}
