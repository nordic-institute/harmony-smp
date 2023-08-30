package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.propertiesPage.PropertiesPage;
import pages.propertiesPage.PropertyPopup;
import rest.models.UserModel;
import utils.Generator;

public class PropertiesPgTests extends SeleniumTest {

    DomiSMPPage homePage;
    UserModel adminUser;
    PropertiesPage propertiesPage;
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
        propertiesPage = homePage.getSidebar().navigateTo(Pages.SYSTEM_SETTINGS_PROPERTIES);
    }

    @Test(description = "PRP-2 - Admin doubleclicks or selects a property and clicks edit button and Edit property dialog is opened")
    public void AdminDoubleClicksOrSelectsAPropertyAndClicksEditButtonandEditPropertyDialogIsOpened() throws Exception {

        String property = "bdmsl.integration.url";
        propertiesPage.propertySearch(property);

        //Check if popup opens when double click property
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);
        propertyEditPoup.clickCancel();
        soft.assertNotNull(propertyEditPoup);

        //Check if popup opens when clicking EDIT button
        propertyEditPoup = null;
        propertyEditPoup = propertiesPage.clickEdit();
        soft.assertNotNull(propertyEditPoup);
        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (URL")
    public void PropertyValueURLIsValidatedAccordingToExpectedFormat() throws Exception {

        String property = "bdmsl.integration.url";
        String wrongValue1 = Generator.randomAlphaNumeric(6);
        String wrongValue2 = wrongValue1 + ".com";
        String wrongValue3 = "www." + wrongValue1 + ".com";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid URL address: [" + wrongValue1 + "]. Error:MalformedURLException: no protocol: " + wrongValue1 + "]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid URL address: [" + wrongValue2 + "]. Error:MalformedURLException: no protocol: " + wrongValue2 + "]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid URL address: [" + wrongValue3 + "]. Error:MalformedURLException: no protocol: " + wrongValue3 + "]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (email)")
    public void PropertyValueEmailIsValidatedAccordingToExpectedFormat() throws Exception {

        String property = "smp.alert.mail.from";
        String wrongValue1 = Generator.randomAlphaNumeric(6);
        String wrongValue2 = wrongValue1 + "@yahoo";
        String wrongValue3 = wrongValue1 + ".com";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid email address: [" + wrongValue1 + "].]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid email address: [" + wrongValue2 + "].]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid email address: [" + wrongValue3 + "].]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (cron expression)")
    public void PropertyValueCRONexpressionIsValidatedAccordingToExpectedFormat() throws Exception {

        String property = "smp.alert.credentials.cronJobExpression";
        String wrongValue1 = Generator.randomAlphaNumeric(6);
        String wrongValue2 = "0 0/1 * * * * *";
        String wrongValue3 = "0 A * * * * ";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [cron expression: [" + wrongValue1 + "]. Error:IllegalArgumentException: Cron expression must consist of 6 fields (found 1 in \"" + wrongValue1 + "\")]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [cron expression: [" + wrongValue2 + "]. Error:IllegalArgumentException: Cron expression must consist of 6 fields (found 7 in \"" + wrongValue2 + "\")]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [cron expression: [" + wrongValue3 + "]. Error:NumberFormatException: For input string: \"A\"]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (numeric)")
    public void PropertyValueNumericIsValidatedAccordingToExpectedFormat() throws Exception {

        String property = "smp.ui.session.idle_timeout.user";
        String wrongValue1 = Generator.randomAlphaNumeric(6);
        String wrongValue2 = "333333333333333333333333333333333333333333333333333333";
        String wrongValue3 = "0 A * * * * ";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid integer: [" + wrongValue1 + "]. Error:NumberFormatException: For input string: \"" + wrongValue1 + "\"]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid integer: [" + wrongValue2 + "]. Error:NumberFormatException: For input string: \"" + wrongValue2 + "\"]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration error: [Invalid integer: [" + wrongValue3 + "]. Error:NumberFormatException: For input string: \"" + wrongValue3 + "\"]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

}
