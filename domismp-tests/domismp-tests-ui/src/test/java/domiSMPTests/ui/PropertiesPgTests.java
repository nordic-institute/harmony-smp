package domiSMPTests.ui;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import domiSMPTests.SeleniumTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.systemSettings.propertiesPage.PropertiesPage;
import pages.systemSettings.propertiesPage.PropertyPopup;
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
    public void adminDoubleClicksOrSelectsAPropertyAndClicksEditButtonandEditPropertyDialogIsOpened()  {

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

    @Test(description = "PRP-3 - Edit property dialog lists property name and description which are not editable and value which can be set by the user.")
    public void editPropertyDialogListsPropertyNameAndDescriptionWhichAreNotEditableAndValueWhichCanBeSetByTheUser() throws Exception {

        String property = "bdmsl.integration.tls.disableCNCheck";

        propertiesPage.propertySearch(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);
        soft.assertEquals(propertyEditPoup.getPropertyName(), property, "Wrong property name!");
        propertyEditPoup.getPropertyNameExpandBtn().click();
        soft.assertEquals(propertyEditPoup.getPropertyDescription(), "If SML Url is HTTPs - Disable CN check if needed.", "Wrong property description!");

        propertyEditPoup.getPropertyCheckbox().check();
        propertyEditPoup.clickOK();

        propertiesPage.openEditPropertyPopupup(property);
        soft.assertTrue(propertyEditPoup.getPropertyCheckbox().isChecked(), "Property value did not change!");

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (URL)", priority = 4)
    public void propertyValueURLIsValidatedAccordingToExpectedFormat() {

        String property = "bdmsl.integration.url";
        String wrongValue1 = Generator.randomAlphaNumericValue(6);
        String wrongValue2 = wrongValue1 + ".com";
        String wrongValue3 = "www." + wrongValue1 + ".com";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue1 + "] is not valid URL!]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue2 + "] is not valid URL!]!");


        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue3 + "] is not valid URL!]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (email)")
    public void propertyValueEmailIsValidatedAccordingToExpectedFormat() {

        String property = "smp.alert.mail.from";
        String wrongValue1 = Generator.randomAlphaNumericValue(6);
        String wrongValue2 = wrongValue1 + "@yahoo";
        String wrongValue3 = wrongValue1 + ".com";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue1 + "] is not valid Email address type!]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue2 + "] is not valid Email address type!]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue3 + "] is not valid Email address type!]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (cron expression)")
    public void propertyValueCRONexpressionIsValidatedAccordingToExpectedFormat() {

        String property = "smp.alert.credentials.cronJobExpression";
        String wrongValue1 = Generator.randomAlphaNumericValue(6);
        String wrongValue2 = "0 0/1 * * * * *";
        String wrongValue3 = "0 A * * * * ";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue1 + "] is not valid Cron Expression type!]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue2 + "] is not valid Cron Expression type!]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue3 + "] is not valid Cron Expression type!]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-5 Value is validated according to expected format (numeric)")
    public void propertyValueNumericIsValidatedAccordingToExpectedFormat() {

        String property = "smp.ui.session.idle_timeout.user";
        String wrongValue1 = Generator.randomAlphaNumericValue(6);
        String wrongValue2 = "333333333333333333333333333333333333333333333333333333";
        String wrongValue3 = "0 A * * * * ";

        propertiesPage.propertySearch(property);
        String currentValue = propertiesPage.getPropertyValue(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);


        propertyEditPoup.editInputField(wrongValue1);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue1 + "] is not valid Integer!]!");


        propertyEditPoup.editInputField(wrongValue2);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue2 + "] is not valid Integer!]!");

        propertyEditPoup.editInputField(wrongValue3);
        propertyEditPoup.clickOK();
        error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value: [" + wrongValue3 + "] is not valid Integer!]!");

        //Check if property value hasn't changed.
        propertyEditPoup.clickCancel();
        propertiesPage.refreshPage();

        propertiesPage.propertySearch(property);
        String valueAfterEdit = propertiesPage.getPropertyValue(property);
        soft.assertEquals(valueAfterEdit, currentValue);

        soft.assertAll();
    }

    @Test(description = "PRP-6 - Value is validated when user presses OK and if it’s invalid error is shown:")
    public void valueIsValidatedWhenUserPressesOkAndIfItsInvalidErrorIsShown() {

        String property = "identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage";
        String longValue = Generator.randomAlphaNumericValue(2001);

        propertiesPage.propertySearch(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);
        propertyEditPoup.editInputField(longValue);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [" + property + "] has error: [Property value must be less than 2000 characters!]!");

        soft.assertAll();
    }

    @Test(description = "PRP-9 - Cancel button cancels all changes")
    public void cancelButtonCancelsAllChanges() {

        String property = "identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage";
        String longValue = Generator.randomAlphaNumericValue(2001);

        propertiesPage.propertySearch(property);
        PropertyPopup propertyEditPoup = propertiesPage.openEditPropertyPopupup(property);
        propertyEditPoup.editInputField(longValue);
        propertyEditPoup.clickOK();
        String error = propertyEditPoup.getErrorMessage();
        soft.assertEquals(error, "Configuration property [identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage] has error: [Property value must be less than 2000 characters!]!");

        soft.assertAll();
    }


}
