package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.openqa.selenium.WebElement;
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
        soft.assertEquals(truststorepage.getPublicKeyTypeLbl(), "RSA");
        soft.assertEquals(truststorepage.getAliasIdLbl(), certificateALias);
        soft.assertEquals(truststorepage.getSmpCertificateIdLbl(), "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8");
        soft.assertEquals(truststorepage.getSubjectNameLbl(), "C=BE,O=eDelivery,CN=red_gw");
        soft.assertEquals(truststorepage.getValidFromLbl(), "23-3-2023, 10:49:22");
        soft.assertEquals(truststorepage.getValidToLbl(), "22-3-2033, 10:49:22");
        soft.assertEquals(truststorepage.getIssuerLbl(), "C=BE,O=eDelivery,CN=red_gw");
        soft.assertEquals(truststorepage.getSerialNumberLbl(), "110fa0d8");
        soft.assertAll();

    }

    @Test(description = "TRST-02 System admin is able to import duplicated certificates")
    public void SystemAdminIsAbleToImportDuplicatedCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/validCertificate.cer");

        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        String duplicatedCertificateALias = truststorepage.addCertificateAndReturnAlias(path);
        WebElement certificate = truststorepage.getCertificateGrid().searchAndGetElementInColumn("Alias", certificateALias);
        soft.assertNotNull(certificate);
        truststorepage.getLeftSideGrid().searchAndGetElementInColumn("Alias", duplicatedCertificateALias);

        soft.assertNotNull(duplicatedCertificateALias);
        soft.assertEquals(truststorepage.getPublicKeyTypeLbl(), "RSA");
        soft.assertEquals(truststorepage.getAliasIdLbl(), duplicatedCertificateALias);
        soft.assertEquals(truststorepage.getSmpCertificateIdLbl(), "CN=red_gw,O=eDelivery,C=BE:00000000110fa0d8");
        soft.assertEquals(truststorepage.getSubjectNameLbl(), "C=BE,O=eDelivery,CN=red_gw");
        soft.assertEquals(truststorepage.getValidFromLbl(), "23-3-2023, 10:49:22");
        soft.assertEquals(truststorepage.getValidToLbl(), "22-3-2033, 10:49:22");
        soft.assertEquals(truststorepage.getIssuerLbl(), "C=BE,O=eDelivery,CN=red_gw");
        soft.assertEquals(truststorepage.getSerialNumberLbl(), "110fa0d8");
        soft.assertAll();

    }

}
