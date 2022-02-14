package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.components.messageArea.AlertMessage;
import pages.domain.DomainGrid;
import pages.domain.DomainPage;
import pages.domain.DomainPopup;
import pages.domain.DomainRow;
import utils.Generator;
import utils.enums.SMPMessages;
import utils.rest.SMPRestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DomainPgTest extends BaseTest {
	
	@AfterMethod
	public void logoutAndReset(){
		genericLogoutProcedure();
	}

	
	@BeforeMethod
	public void loginAndGoToDomainPage(){
		
		SMPPage page = genericLoginProcedure("SYS_ADMIN");

		logger.info("Going to Domain page");
		page.sidebar.goToPage(DomainPage.class);
	}
	
	@Test(description = "DMN-0")
	public void openDomainPage(){
		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);

		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
		DomainGrid grid = page.grid();
		DomainRow row0 = grid.getRowsInfo().get(0);
		grid.doubleClickRow(0);
		
		DomainPopup popup = new DomainPopup(driver);

		soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");

		soft.assertTrue(!popup.isDomainCodeInputEnabled(), "On double click Domain Code input is disabled");
		soft.assertTrue(!popup.isSMLDomainInputEnabled(), "On double click SML Domain input is disabled");

		popup.clickCancel();
		
		soft.assertEquals(row0, page.grid().getRowsInfo().get(0), "Row is unchanged");
		soft.assertTrue(!page.isSaveButtonEnabled(), "Save button is not enabled");

		soft.assertAll();
	}

	@Test(description = "DMN-10")
	public void editDomain(){

		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);

		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
		DomainGrid grid = page.grid();
		DomainRow row0 = grid.getRowsInfo().get(0);
		grid.doubleClickRow(0);

		DomainPopup popup = new DomainPopup(driver);

		soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");

		soft.assertTrue(!popup.isDomainCodeInputEnabled(), "On double click Domain Code input is disabled");
		soft.assertTrue(!popup.isSMLDomainInputEnabled(), "On double click SML Domain input is disabled");

		String rndString = Generator.randomAlphaNumeric(10);
		popup.fillSMLSMPIdInput(rndString);
		popup.clickCancel();

		soft.assertEquals(row0, page.grid().getRowsInfo().get(0), "Row 0 is not changed");

		page.grid().doubleClickRow(0);
		popup = new DomainPopup(driver);
		popup.fillSMLSMPIdInput(rndString);
		popup.clickOK();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
		soft.assertNotEquals(row0, page.grid().getRowsInfo().get(0), "Row 0 is changed");

		page.clickSave().confirm();

		DomainRow newRow0 = page.grid().getRowsInfo().get(0);
		soft.assertNotEquals(row0, newRow0, "Row 0 is changed after save");
		soft.assertEquals(newRow0.getSmlSmpID(), rndString, "SML SMP ID is changed to the desired string");

		soft.assertEquals(page.alertArea.getAlertMessage().getMessage(), SMPMessages.MSG_18, "Proper message displayed");

		soft.assertAll();
	}


	@Test(description = "DMN-20")
	public void newDomain(){
		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);

		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");

		DomainPopup popup = page.clickNew();
		soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");

		soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
		soft.assertTrue(popup.isSMLDomainInputEnabled(), "When defining new domain -SML Domain input is disabled");

		String rndString = Generator.randomAlphaNumeric(10);


		popup.fillDataForNewDomain(rndString, rndString, rndString, rndString);
		popup.clickCancel();

		soft.assertTrue(!page.isSaveButtonEnabled(), "Save button is NOT enabled");

		popup = page.clickNew();
		popup.fillDataForNewDomain(rndString, rndString, rndString, rndString);
		popup.clickOK();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");

		page.clickSave().confirm();

		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18),
				"Success message is as expected");

		List<DomainRow> rows = page.grid().getRowsInfo();
		while (page.pagination.hasNextPage()){
			page.pagination.goToNextPage();
			rows.addAll(page.grid().getRowsInfo());
		}

		boolean found = false;
		for (DomainRow row : rows) {
			if(row.getDomainCode().equalsIgnoreCase(rndString)){
				found = true;
				break;
			}
		}

		soft.assertTrue(found, "Found new domain in the list of domains");

		soft.assertAll();
	}

	@Test(description = "DMN-30")
	public void cancelNewDomainCreation(){
		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);

		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");

		DomainPopup popup = page.clickNew();
		soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");

		soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
		soft.assertTrue(popup.isSMLDomainInputEnabled(), "When defining new domain -SML Domain input is disabled");

		String rndString = Generator.randomAlphaNumeric(10);


		popup.fillDataForNewDomain(rndString, rndString, rndString, rndString);
		popup.clickOK();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();

		List<DomainRow> rows = page.grid().getRowsInfo();
		while (page.pagination.hasNextPage()){
			page.pagination.goToNextPage();
			rows.addAll(page.grid().getRowsInfo());
		}

		boolean found = false;
		for (DomainRow row : rows) {
			if(row.getDomainCode().equalsIgnoreCase(rndString)){
				found = true;
				break;
			}
		}

		soft.assertTrue(!found, "New domain NOT in the list of domains");

		soft.assertAll();
	}

	@Test(description = "DMN-40")
	public void deleteDomain(){
		String rndStr = Generator.randomAlphaNumeric(10);
		SMPRestClient.createDomain(rndStr);

		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);

		page.refreshPage();

		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled");

        int index = page.grid().scrollToDomain(rndStr);

		page.grid().selectRow(index);

		soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row selected");

		page.clickDelete();
		soft.assertTrue(!page.isDeleteButtonEnabled(), "Delete button is not enabled (2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is enabled");
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();

        soft.assertTrue(page.grid().isDomainStillPresent(rndStr), "Row is still present");

        index = page.grid().scrollToDomain(rndStr);
        page.grid().selectRow(index);
        page.clickDelete();
        page.clickSave().confirm();

        soft.assertTrue(!page.grid().isDomainStillPresent(rndStr), "Row is still NOT present after delete");


		soft.assertAll();
	}

	@Test(description = "DMN-50")
	public void deleteDomainWithSG(){

		String domainName = Generator.randomAlphaNumeric(10);
		String pi = Generator.randomAlphaNumeric(10);
		String ps = Generator.randomAlphaNumeric(10);

		String expectedErrorMess = String.format("Delete validation error Could not delete domains used by Service groups! Domain: %s (%s ) uses by:1 SG.", domainName, domainName);

		SMPRestClient.createDomain(domainName);
		SMPRestClient.createServiceGroup(pi, ps, new ArrayList<>(Arrays.asList("smp")),new ArrayList<>(Arrays.asList(domainName)));

		SoftAssert soft = new SoftAssert();

		DomainPage page = new DomainPage(driver);
		page.refreshPage();

        int index = page.grid().scrollToDomain(domainName);
        page.grid().selectRow(index);
        soft.assertTrue(page.isDeleteButtonEnabled(), "Delete button is enabled after row select");

		page.clickDelete();
		AlertMessage message = page.alertArea.getAlertMessage();
		soft.assertTrue(message.isError(), "Page shows error message when deleting domain with SG");
		soft.assertTrue(message.getMessage().equalsIgnoreCase(expectedErrorMess), "Desired message appears");

		SMPRestClient.deleteSG(pi);
		SMPRestClient.deleteDomain(domainName);

		soft.assertAll();
	}

    @Test(description = "DMN-60")
    public void duplicateDomainCreation() {
        SoftAssert soft = new SoftAssert();
        DomainPage page = new DomainPage(driver);
        String errorMsg = "The Domain code already exists!";
        soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
        String rndString = Generator.randomAlphaNumeric(10);
        DomainPopup popup = page.clickNew();
        soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");
        soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
        soft.assertTrue(popup.isSMLDomainInputEnabled(), "When defining new domain -SML Domain input is disabled");
        popup.fillDataForNewDomain(rndString, rndString, rndString, rndString);
        popup.clickOK();
        soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
        page.clickSave().confirm();
        page.clickNew();
        soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");
        soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
        soft.assertTrue(popup.isSMLDomainInputEnabled(), "When defining new domain -SML Domain input is disabled");
        popup.fillDataForNewDomain(rndString, rndString, rndString, rndString);
        soft.assertEquals(popup.getDuplicateDomainErrorMsgText(), errorMsg, "The message is not matching with our expected error message");
        soft.assertFalse(popup.isEnableOkButton(), "Ok button is enable");
        soft.assertTrue(popup.isEnableCancelButton(), "Cancel button is disabled");
        popup.clickCancel();
        soft.assertAll();
    }

    @Test(description = "DMN-70")
    public void onlyDomainCodeSavingMsgVerify() {
        SoftAssert soft = new SoftAssert();
        DomainPage page = new DomainPage(driver);
//        String errorMsg = "The domain should have a defined signature CertAlias.";
        soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
        int index = page.grid().scrollToSmlDomain("");
        if (index >= 0) {
            try {
                page.grid().selectRow(index);
                page.clickDelete();
                page.clickSave().confirm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String rndString = Generator.randomAlphaNumeric(10);
        DomainPopup popup = page.clickNew();
        soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");
        soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
        popup.clearAndFillDomainCodeInput(rndString);
        soft.assertTrue(popup.isEnableOkButton(), "Ok button is disabled");
        popup.clickOK();
        soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
        page.clickSave().confirm();
        soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18),
                "Success message is as expected");
        index = page.grid().scrollToSmlDomain("");
        if (index >= 0) {
            page.grid().scrollRow(index);
        }
        int rowNumber = index + 1;
        page.grid().mouseHoverOnDomainCode(rowNumber);
//        WebElement text = driver.findElement(By.xpath("//*[text()='The domain should have a defined signature CertAlias.']"));
//        soft.assertEquals(text.getText(),errorMsg, "the message 'The domain should have a defined signature CertAlias.' is not displayed");
        soft.assertAll();
    }

	@Test(description = "DMN-80")
	public void onlyDomainCodeAndSMLDomainSavingMsgVerify() {
		SoftAssert soft = new SoftAssert();
		DomainPage page = new DomainPage(driver);
//        String errorMsg = "The domain should have a defined signature CertAlias.";
		soft.assertTrue(page.isLoaded(), "Check that the page is loaded");
		String rndString = Generator.randomAlphaNumeric(10);
		DomainPopup popup = page.clickNew();
		soft.assertTrue(popup.isLoaded(), "Domain popup is loaded");
		soft.assertTrue(popup.isDomainCodeInputEnabled(), "When defining new domain - Domain Code input is disabled");
		popup.clearAndFillDomainCodeInput(rndString);
		popup.clearAndFillSMLDomainInput(rndString);
		soft.assertTrue(popup.isEnableOkButton(), "Ok button is disabled");
		popup.clickOK();
		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is enabled");
		page.clickSave().confirm();
		soft.assertTrue(page.alertArea.getAlertMessage().getMessage().equalsIgnoreCase(SMPMessages.MSG_18),
				"Success message is as expected");
		int index = page.grid().scrollToSmlDomain(rndString);
		if (index >= 0) {
			page.grid().scrollRow(index);
		}
		int rowNumber = index + 1;
		page.grid().mouseHoverOnDomainCode(rowNumber);
//        WebElement text = driver.findElement(By.xpath("//*[text()='The domain should have a defined signature CertAlias.']"));
//        soft.assertEquals(text.getText(),errorMsg, "the message 'The domain should have a defined signature CertAlias.' is not displayed");
		soft.assertAll();
	}


}
