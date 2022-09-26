package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.ServiceGroupGrid;
import pages.service_groups.ServiceGroupRow;
import pages.service_groups.edit.*;
import utils.Generator;
import utils.rest.SMPRestClient;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class EditPgTest extends BaseTest {

	@AfterMethod
	public void logoutAndReset() {
		genericLogoutProcedure();
	}


	@BeforeMethod
	public void loginAndGoToEditPage() {

		SMPPage page = genericLoginProcedure("SMP_ADMIN");

		logger.info("Going to Edit page");
		page.sidebar.goToPage(EditPage.class);
	}


	@Test(description = "EDT-10")
	public void testFilters() {
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
	public void doubleclickRow() {
		String extensionData = "<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ExtensionID>df</ExtensionID><ExtensionName>sdxf</ExtensionName><!-- Custom element is mandatory by OASIS SMP schema. Replace following element with your XML structure. --><ext:example xmlns:ext=\"http://my.namespace.eu\">" + Generator.randomAlphaNumeric(10) + "</ext:example></Extension>";

		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		String pi = row0.getParticipantIdentifier();
		grid.doubleClickRow(index);
		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertTrue(pi.equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for appropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		popup.enterDataInExtensionTextArea("kjsfdfjfhskdjfhkjdhfksdjhfjksdhfjksd");
		popup.clickOK();
		soft.assertTrue(!popup.getErrorMessage().isEmpty(), "When entering wrong data you get an error message on save");

		popup.enterDataInExtensionTextArea(extensionData);
		popup.clickOK();

		page.saveChangesAndConfirm();

		index = scrollToSG(pi);

		page.getGrid().doubleClickRow(index);
		ServiceGroupPopup popup2 = new ServiceGroupPopup(driver);
		soft.assertEquals(popup2.getExtensionAreaContent(), extensionData, "Extension data is saved properly");

		popup2.enterDataInExtensionTextArea("");
		popup2.clickCancel();

		index = scrollToSG(pi);

		page.getGrid().doubleClickRow(index);
		ServiceGroupPopup popup3 = new ServiceGroupPopup(driver);
		soft.assertTrue(!popup3.getExtensionAreaContent().isEmpty(), "Extension data is NOT saved empty as expected");


		soft.assertAll();
	}

	@Test(description = "EDT-30")
	public void editActionButtonOnRow() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceGroupPopup popup = row0.clickEdit();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for appropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertAll();
	}

	@Test(description = "EDT-40")
	public void editButtonOnPage() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		soft.assertTrue(!page.isEditButtonEnabled(), "Edit button is not enabled before row is selected");

		grid.selectRow(index);

		soft.assertTrue(page.isEditButtonEnabled(), "Edit button is active after ro is selected");

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceGroupPopup popup = page.clickEdit();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for appropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertAll();
	}

	@Test(description = "EDT-50")
	public void serviceGroupPopupUICheck() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for appropriate service group");
		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantSchemeValue()), "Popup opened for appropriate service group");
		soft.assertTrue(popup.isExtensionAreaEditable(), "extension area is editable");

		soft.assertTrue(!popup.isParticipantIdentifierInputEnabled(), "Participant Identifier field is disabled");
		soft.assertTrue(!popup.isParticipantSchemeInputEnabled(), "Participant Scheme field is disabled");
		soft.assertTrue(popup.isOwnersPanelEnabled(), "Owners panel is enabled");
		soft.assertTrue(popup.isDomainsPanelEnabled(), "Domain panel is enabled");

		soft.assertAll();
	}

	@Test(description = "EDT-60")
	public void newMetadataIcon() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);

		ServiceMetadataPopup popup = row0.clickAddMetadata();

		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(popup.getParticipantIdentifierValue()), "Popup opened for appropriate service group");

		soft.assertTrue(popup.isDocumentIdentifierEnabled());
		soft.assertTrue(popup.isDocumentSchemeEnabled());

		soft.assertAll();
	}


//	Cannot identify the cause of failure so move on and hope for the best

	@Test(description = "EDT-70")
	public void noSYSADMINOwners() {
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
				if (listedOption.equalsIgnoreCase(sysadmin)) {
					soft.fail("Found sysadmin between options for SG owners - " + sysadmin);
				}
			}
		}
		soft.assertAll();

	}

	@Test(description = "EDT-80")
	public void allDomainsInDomainsAccordionSection() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupGrid grid = page.getGrid();

		Integer index = 0;

		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		grid.doubleClickRow(index);

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);
		popup.domainsPanel.expandSection();

		List<String> listedOptions = popup.domainsPanel.getOptions();
		List<String> domains = SMPRestClient.getDomainAndSubdomains();

		for (String domain : domains) {
			boolean found = false;
			logger.info("Checking domain " + domain);
			for (String listedOption : listedOptions) {
				if (listedOption.equalsIgnoreCase(domain)) {
					found = true;
				}
			}
			soft.assertTrue(found, "Domain found in options - " + domain);
		}


		soft.assertAll();

	}

	@Test(description = "EDT-90")
	public void extensionValidatedOnOK() {
		String identifier = Generator.randomAlphaNumeric(7);
		String tmpSchemeRoot = Generator.randomAlphaNumeric(3).toLowerCase();
		String scheme = String.format("%s-%s-%s", tmpSchemeRoot, tmpSchemeRoot, tmpSchemeRoot);

		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);

		ServiceGroupPopup popup = page.clickNew();
		popup.fillParticipantIdentifier(identifier);
		popup.fillParticipantScheme(scheme);
		popup.chooseFirstOwner();
		popup.chooseFirstDomain();
		popup.fillExtensionArea("invalid XML text");
		popup.clickOK();

		soft.assertTrue(!popup.getErrorMessage().isEmpty(), "Error message displayed when entering invalid xml in extension area");

		popup.clickClear();
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


		SMPRestClient.deleteSG(identifier);

		identifier = Generator.randomAlphaNumeric(10);
//		scheme = Generator.randomAlphaNumeric(10);

		popup = page.clickNew();
		popup.fillParticipantIdentifier(identifier);
		popup.fillParticipantScheme(scheme);
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
	public void deleteServiceGroup() {
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

	@Test(description = "EDT-110")
	public void serviceMetadataDilogVerfication() {
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);
		String generator = Generator.randomAlphaNumeric(10);
		ServiceGroupGrid grid = page.getGrid();
		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		ServiceMetadataPopup metadataPopup= row0.clickAddMetadata();
		soft.assertTrue(!metadataPopup.isParticipantIdentifierEnabled(),"participantId field is enabled for an existing service group in service metadata popup");
		soft.assertTrue(!metadataPopup.isParticipantSchemeEnabled(),"participantScheme field is enabled for an existing service group in service metadata popup");
		soft.assertEquals(metadataPopup.docIDFieldContain(),"","docIDField is not empty");
		soft.assertEquals(metadataPopup.docIDSchemeFieldContain(),"","docIDField is not empty");
		soft.assertTrue(row0.getParticipantIdentifier().equalsIgnoreCase(metadataPopup.getParticipantIdentifierValue()), "ServiceMetadata dialog opened for appropriate service group");
		soft.assertTrue(row0.getParticipantScheme().equalsIgnoreCase(metadataPopup.getParticipantSchemeValue()), "ServiceMetadata dialog opened for appropriate service group");
		metadataPopup.fillForm(generator,generator,generator);
		ServiceMetadataWizardPopup metadataWizaedPopup = metadataPopup.clickMetadataWizard();
		metadataWizaedPopup.fillForm(generator,generator,generator,generator,"bdxr-transport-ebms3-as4-v1p0","internal/rest/domain");
		metadataWizaedPopup.fillCerificateBox(generator);
		soft.assertTrue(metadataWizaedPopup.isEnableOkBtn(),"ok button is disabled after providing the valid data");
		metadataPopup = metadataWizaedPopup.clickOK();
		soft.assertEquals(metadataPopup.docIDFieldContain(),generator,"After saving the servicemetadata wizard popup with valid data the docID field of service metadata popup doc id contain the coressponding value");
		soft.assertEquals(metadataPopup.docIDSchemeFieldContain(),generator,"After saving the servicemetadata wizard popup with valid data the docIDScheme field of service metadata popup doc id scheme contain the coressponding value");
		soft.assertTrue(metadataPopup.isOKBtnEnabled(),"OK button is not enabled");
		soft.assertAll();

	}

	@Test(description = "EDT-120")
	public void verifyServicemtadataWizardDilogField(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);
		String generator = Generator.randomAlphaNumeric(10);
		ServiceGroupGrid grid = page.getGrid();
		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		ServiceMetadataPopup metadataPopup= row0.clickAddMetadata();
		soft.assertTrue(!metadataPopup.isParticipantIdentifierEnabled(),"participantId field is enabled for an existing service group in service metadata popup");
		soft.assertTrue(!metadataPopup.isParticipantSchemeEnabled(),"participantScheme field is enabled for an existing service group in service metadata popup");
		metadataPopup.fillForm(generator,generator,generator);
		ServiceMetadataWizardPopup metadataWizard= metadataPopup.clickMetadataWizard();
		soft.assertEquals(metadataWizard.docIDFieldContain(),generator,"document identifier field of metdata wizard popup not contain the corresponding doc id filled in sevice metadata popup");
		soft.assertEquals(metadataWizard.docIDSchemeFieldContain(),generator,"document identifier field of metdata wizard popup not contain the corresponding doc id scheme filled in sevice metadata popup");
		soft.assertAll();
	}

	@Test(description = "EDT-130")
	public void verifyTransportProfile(){
		SoftAssert soft = new SoftAssert();
		EditPage page = new EditPage(driver);
		String generator = Generator.randomAlphaNumeric(10);
		ServiceGroupGrid grid = page.getGrid();
		Integer index = 0;
		ServiceGroupRowE row0 = grid.getRowsAs(ServiceGroupRowE.class).get(index);
		ServiceMetadataPopup metadataPopup= row0.clickAddMetadata();
		soft.assertTrue(!metadataPopup.isParticipantIdentifierEnabled(),"participantId field is enabled for an existing service group in service metadata popup");
		soft.assertTrue(!metadataPopup.isParticipantSchemeEnabled(),"participantScheme field is enabled for an existing service group in service metadata popup");
		ServiceMetadataWizardPopup metadataWizard= metadataPopup.clickMetadataWizard();
		soft.assertEquals(metadataWizard.transportProfileFieldContent(),"bdxr-transport-ebms3-as4-v1p0","The transport profile field in service metadata wizard popup not contain the default value");

		soft.assertAll();
	}


	private int scrollToSG(String pi) {
		EditPage page = new EditPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			page = new EditPage(driver);

			List<ServiceGroupRow> rows = page.getGrid().getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getParticipantIdentifier().equalsIgnoreCase(pi)) {
					return i;
				}
			}

			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		return -1;
	}

}
