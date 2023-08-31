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

    //TODO work in progress - wait for input elements to get text

    @Test(description = "TRST-01 System admin is able to import certificates")
    public void SystemAdminIsAbleToImportCertificates() throws Exception {

        SoftAssert soft = new SoftAssert();
        DomiSMPPage homePage = new DomiSMPPage(driver);

        LoginPage loginPage = homePage.goToLoginPage();
        loginPage.login(data.getAdminUser().get("username"), data.getAdminUser().get("password"));

        TruststorePage truststorepage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_TRUSTSTORE);
        String path = FileUtils.getAbsolutePath("./src/main/resources/truststore/test.cer");

        String certificateALias = truststorepage.addCertificateAndReturnAlias(path);
        soft.assertNotNull(certificateALias);
        //  soft.assertEquals(truststorepage.getPublicKeyTypeLbl(), "RSA");
        // soft.assertEquals(truststorepage.getAliasIdLbl(), "smp_domain_02");


        soft.assertAll();


    }

}
