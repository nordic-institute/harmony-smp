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
    public void SystemAdminIsAbleToImportCertificates() throws Exception {

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
        soft.assertEquals(truststorepage.getValidFromValue(), "23/3/2023, 10:49:22");
        soft.assertEquals(truststorepage.getValidToValue(), "22/3/2033, 10:49:22");
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

        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", certificateALias));
        soft.assertNotNull(certificateALias);

        String duplicatedCertificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertNotNull(duplicatedCertificateALias);
        soft.assertTrue(truststorepage.getLeftSideGrid().isValuePresentInColumn("Alias", duplicatedCertificateALias));
        soft.assertAll();

    }

}
