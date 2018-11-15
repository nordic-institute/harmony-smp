package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.ServiceGroupGrid;
import pages.service_groups.edit.EditPage;
import pages.service_groups.edit.ServiceGroupPopup;
import pages.service_groups.edit.ServiceGroupRowE;
import pages.service_groups.edit.ServiceMetadataPopup;
import utils.Generator;
import utils.rest.SMPRestClient;

import java.util.List;

public class EditPgTest extends BaseTest{

	@AfterMethod
	public void logoutAndReset(){
		SMPPage page = new SMPPage(driver);
		page.refreshPage();

		if(page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Logout!!");
			page.pageHeader.sandwichMenu.logout();
		}
		page.waitForXMillis(100);
	}


	@BeforeMethod
	public void loginAndGoToEditPage(){

		SMPPage page = new SMPPage(driver);

		if(!page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Login!!");
			page.pageHeader.goToLogin().login("SMP_ADMIN");
		}

		logger.info("Going to Edit page");
		page.sidebar.goToPage(EditPage.class);
	}


	@Test(description = "EDT-10")
	public void testFilters(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupRowE row0 = page.getGrid().getRowsAs(ServiceGroupRowE.class).get(0);
		String pi = row0.getParticipantIdentifier();
		String ps = row0.getParticipantScheme();

		page.filterArea.filter(pi, ps, "");

		List<ServiceGroupRowE> results = page.getGrid().getRowsAs(ServiceGroupRowE.class);
		for (ServiceGroupRowE result : results) {
			soft.assertTrue(result.getParticipantIdentifier().contains(pi), "Row matches searched participant identifier");
			soft.assertTrue(result.getParticipantScheme().contains(ps), "Row matches searched participant scheme");
		}

		page.filterArea.filter(pi.substring(2), ps.substring(2), "");

		results = page.getGrid().getRowsAs(ServiceGroupRowE.class);
		for (ServiceGroupRowE result : results) {
			soft.assertTrue(result.getParticipantIdentifier().contains(pi.substring(2)), "Row matches searched participant identifier stub");
			soft.assertTrue(result.getParticipantScheme().contains(ps.substring(2)), "Row matches searched participant scheme stub");
		}

		soft.assertAll();
	}

	@Test(description = "EDT-20")
	public void doubleclickRow(){
		String extensionData = "<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ExtensionID>df</ExtensionID><ExtensionName>sdxf</ExtensionName><!-- Custom element is mandatory by OASIS SMP schema. Replace following element with your XML structure. --><ext:example xmlns:ext=\"http://my.namespace.eu\">my mandatory content</ext:example></Extension>";


		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);
		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for apropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		popup.enterDataInExtensionTextArea("kjsfdfjfhskdjfhkjdhfksdjhfjksdhfjksd");
		popup.clickOK();
		soft.assertTrue(!popup.getErrorMessage().isEmpty(), "When entering wrong data you get an error message on save");

		popup.enterDataInExtensionTextArea(extensionData);
		popup.clickOK();

		page.saveChanges();

		page.getGrid().doubleClickRow(index);
		ServiceGroupPopup popup2 = new ServiceGroupPopup(driver);
		soft.assertEquals(popup2.getExtansionAreaContent(), extensionData, "Extension data is saved properly");

		popup2.enterDataInExtensionTextArea("");
		popup2.clickCancel();
//TODO: refactor this assert bellow
//		page.getGrid().doubleClickRow(0);
//		ServiceGroupPopup popup3 = new ServiceGroupPopup(driver);
//		soft.assertTrue(!popup3.getExtansionAreaContent().isEmpty(), "Extension data is NOT saved empty as expected");


		soft.assertAll();
	}

	@Test(description = "EDT-30")
	public void editActionButtonOnRow(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceGroupPopup popup = row0.clickEdit();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for apropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertAll();
	}

	@Test(description = "EDT-40")
	public void editButtonOnPage(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		soft.assertTrue(!page.isEditButtonEnabled(), "Edit button is not enabled before row is selected");

		grid.selectRow(index);

		soft.assertTrue(page.isEditButtonEnabled(), "Edit button is active after ro is selected");

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceGroupPopup popup = page.clickEdit();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for apropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertAll();
	}

	@Test(description = "EDT-50")
	public void serviceGroupPopupUICheck(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for apropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertTrue(!popup.isParticipantIdentifierInputEnabled(), "Participant Identifier field is disabled");
		soft.assertTrue(!popup.isParticipantSchemeInputEnabled(), "Participant Scheme field is disabled");
		soft.assertTrue(popup.isOwnersPanelEnabled(), "Owners panel is enabled");
		soft.assertTrue(popup.isDomainsPanelEnabled(), "Domain panel is enabled");

		soft.assertAll();
	}

	@Test(description = "EDT-60")
	public void newMetadataIcon(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceMetadataPopup popup = row0.clickAddMetadata();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for apropriate service group");

		soft.assertTrue(popup.isDocumentIdentifierEnabled());
		soft.assertTrue(popup.isDocumentSchemeEnabled());

		soft.assertAll();
	}


//	Cannot identify the cause of failure so move on and hope for the best

	@Test(description = "EDT-70")
	public void noSYSADMINOwners(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		popup.waitForXMillis(5000);

		List<String> listedOptions = popup.ownersPanel.getOptions();
		List<String> sysadmins = SMPRestClient.getSysAdmins();

		for (String sysadmin : sysadmins) {
			logger.info("Checking sysadmin " + sysadmin);
			for (String listedOption : listedOptions) {
				if(listedOption.equalsIgnoreCase(sysadmin)){
					soft.fail("Found sysadmin between options for SG owners - " + sysadmin);
				}
			}
		}
		soft.assertAll();

	}

	@Test(description = "EDT-80")
	public void allDomainsInDomainsAccordionSection(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0 ;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);
		popup.domainsPanel.expandSection();

		List<String> listedOptions = popup.domainsPanel.getOptions();
		List<String> domains = SMPRestClient.getDomainAndSubdomain();

		for (String domain : domains) {
			boolean found = false;
			logger.info("Checking domain " + domain);
			for (String listedOption : listedOptions) {
				if(listedOption.equalsIgnoreCase(domain)){
					found= true;
				}
			}
			soft.assertTrue(found, "Domain found in options - " + domain);
		}


		soft.assertAll();

	}

	@Test(description = "EDT-90")
	public void extensionValidatedOnOK(){
		String identifier = Generator.randomAlphaNumeric(7);
		String scheme = Generator.randomAlphaNumeric(7);

		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupPopup popup = page.clickNew();
		popup.fillParticipanIdentifier(identifier);
		popup.fillParticipanScheme(scheme);
		popup.chooseFirstOwner();
		popup.chooseFirstDomain();
		popup.fillExtensionArea("invalid XML text");
		popup.clickOK();

		soft.assertTrue(!popup.getErrorMessage().isEmpty(), "Erorr message displayed when entering invalid xml in extension area");

		popup.generateRndExtension();
		popup.clickOK();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is now active");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is now active");

		page.clickSave().confirm();

		soft.assertTrue(!page.isSaveButtonEnabled(), "Save button is now inactive after save");
		soft.assertTrue(!page.isCancelButtonEnabled(), "Cancel button is now inactive after save");

		page.filterArea.filter(identifier, scheme, "");
		soft.assertTrue(page.getGrid().getRows().get(0).getParticipantIdentifier().equalsIgnoreCase(identifier)
				, "Service group was saved and is visible in search");


		identifier = Generator.randomAlphaNumeric(10);
		scheme = Generator.randomAlphaNumeric(10);

		popup = page.clickNew();
		popup.fillParticipanIdentifier(identifier);
		popup.fillParticipanScheme(scheme);
		popup.chooseFirstOwner();
		popup.chooseFirstDomain();
		popup.clickOK();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is now active (2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is now active (2)");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();

		soft.assertTrue(!page.isSaveButtonEnabled(), "Save button is now inactive after cancel");
		soft.assertTrue(!page.isCancelButtonEnabled(), "Cancel button is now inactive after cancel");

		page.filterArea.filter(identifier, scheme, "");
		soft.assertTrue(page.getGrid().getRowsNo() == 0
				, "Service group was NOT saved and is NOT visible in search");

		soft.assertAll();

	}

	@Test(description = "EDT-100")
	public void deleteServiceGroup(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupRowE row0 = page.getGrid().getRowsAs(ServiceGroupRowE.class).get(0);
		String identifier = row0.getParticipantIdentifier();
		row0.clickDelete();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is now active");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is now active");

		page.clickCancel().confirm();
		new ConfirmationDialog(driver).confirm();

		page.filterArea.filter(identifier, "", "");
		soft.assertTrue(page.getGrid().getRows().get(0).getParticipantIdentifier().equalsIgnoreCase(identifier)
				, "Service group was not deleted and is visible in search");

		page.refreshPage();
		ServiceGroupGrid grid = page.getGrid();
		row0 = grid.getRowsAs(ServiceGroupRowE.class).get(0);
		identifier = row0.getParticipantIdentifier();

		grid.selectRow(0);

		soft.assertTrue(page.isDeleteButtonEnabled(), "After row select Delete button is enabled");

		page.clickDelete();

		soft.assertTrue(page.isSaveButtonEnabled(), "Save button is now active (2)");
		soft.assertTrue(page.isCancelButtonEnabled(), "Cancel button is now active (2)");

		page.clickSave().confirm();

		page.filterArea.filter(identifier, "", "");
		soft.assertTrue(page.getGrid().getRowsNo() == 0
				, "Service group deleted and is NOT visible in search anymore");

		soft.assertAll();

	}

}
