package ui;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.MetadataGrid;
import pages.service_groups.MetadataRow;
import pages.service_groups.ServiceGroupRow;
import pages.service_groups.search.SearchPage;
import pages.service_groups.search.pojo.ServiceGroup;
import utils.Generator;
import utils.rest.SMPRestClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SearchPgTest extends BaseTest {

    @AfterMethod
    public void resetFilters() {
        SMPPage page = new SMPPage(driver);
        page.refreshPage();
        page.waitForXMillis(500);

    }

    @Test(description = "SRCH-0")
    public void searchPgInitialState() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());
        soft.assertTrue(page.filters.isSearchButtonVisible(), "Search button is not visible");
        soft.assertTrue(page.filters.isSearchButtonEnable(), "Search button is not enable");
        soft.assertTrue(page.filters.getParticipantIdentifierInputValue().isEmpty());
        soft.assertTrue(page.filters.getParticipantSchemeInputValue().isEmpty());
        soft.assertEquals(page.filters.domainSelect.getSelectedValue(), "All Domains");

        soft.assertAll();
    }

    @Test(description = "SRCH-10")
    public void domainSelectContent() {
        SoftAssert soft = new SoftAssert();
        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        List<String> uiDomains = page.filters.domainSelect.getOptionTexts();
        List<String> restDomains = SMPRestClient.getDomainAndSubdomains();

        for (String restDomain : restDomains) {
            boolean found = false;
            for (String uiDomain : uiDomains) {
                if (uiDomain.equalsIgnoreCase(restDomain)) {
                    found = true;
                }
            }
            soft.assertTrue(found, "Domain was found in domain dropdown " + restDomain);
        }

        soft.assertAll();

    }

    @Test(description = "SRCH-20")
    public void searchGridInitialState() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        List<String> headers = page.getServiceGroupGrid().getHeaders();
        soft.assertTrue(headers.contains("Participant identifier"));
        soft.assertTrue(headers.contains("Participant scheme"));
        soft.assertTrue(headers.contains("OASIS ServiceGroup URL"));
        soft.assertTrue(headers.contains("Metadata size"));

        soft.assertAll();
    }

    @Test(description = "SRCH-30")
    public void searchFilterResults() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        ServiceGroupRow row0 = page.getServiceGroupGrid().getRows().get(0);
        String pScheme = row0.getParticipantScheme();
        String pIdentifier = row0.getParticipantIdentifier();

//		looking for exact match
        page.filters.filter(pIdentifier, pScheme, "");

        List<ServiceGroupRow> rows = page.getServiceGroupGrid().getRows();

        for (ServiceGroupRow row : rows) {
            soft.assertTrue(row.getParticipantIdentifier().contains(pIdentifier));
            soft.assertTrue(row.getParticipantScheme().contains(pScheme));
        }

//		Search for substring
        page.filters.filter(pIdentifier.substring(2), pScheme.substring(2), "");
        rows = page.getServiceGroupGrid().getRows();

        for (ServiceGroupRow row : rows) {

            String identifier = row.getParticipantIdentifier();
            String scheme = row.getParticipantScheme();

            soft.assertTrue(identifier.contains(pIdentifier), String.format("Identifier %s, found %s", pIdentifier, identifier));
            soft.assertTrue(scheme.contains(pScheme), String.format("Scheme %s, found %s", pScheme, scheme));
        }

        soft.assertAll();
    }

    @Test(description = "SRCH-40")
    public void openURLLink() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        ServiceGroupRow row0 = page.getServiceGroupGrid().getRows().get(0);
        String listedURL = row0.getServiceGroupURL();
        String pScheme = row0.getParticipantScheme();
        String pIdentifier = row0.getParticipantIdentifier();

//		verify proper URL format
        String tmpURLPart = null;
        try {
            tmpURLPart = URLDecoder.decode(listedURL, "UTF-8").split("smp/")[1].trim();
            soft.assertEquals(tmpURLPart, pScheme + "::" + pIdentifier, "URL contains the proper scheme and identifier");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ServiceGroup serviceGroup = SMPRestClient.getServiceGroup(listedURL);

        soft.assertTrue(row0.getMetadataSize() == serviceGroup.getServiceMetadataReferenceCollection().size(),
                "Number of listed MetadataReferences in XML matches UI");


        soft.assertAll();
    }


    @Test(description = "SRCH-50")
    @Ignore
    public void expandServiceGroupCheckMetadata() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        ServiceGroupRow row0 = page.getServiceGroupGrid().getRows().get(0);
        String listedURL = row0.getServiceGroupURL();
        String pScheme = row0.getParticipantScheme();
        String pIdentifier = row0.getParticipantIdentifier();

//		verify proper URL format
        ServiceGroup serviceGroup = SMPRestClient.getServiceGroup(listedURL);

        MetadataGrid metadataGrid = row0.expandMetadata();

        List<MetadataRow> metadataRows = metadataGrid.getMetadataRows();

        soft.assertTrue(row0.getMetadataSize() == metadataRows.size(), "Metadata size field compared with the size of the list containing the metadata");

        for (MetadataRow metadataRow : metadataRows) {
            String docScheme = metadataRow.getDocumentIdentifierScheme();
            String docId = metadataRow.getDocumentIdentifier();
            String url = metadataRow.getURL();

            soft.assertTrue(url.contains(String.format("%s::%s/services/%s::%s", pScheme, pIdentifier, docScheme, docId)), "Checking URL format for metadata " + docId);


            String metadata = SMPRestClient.getMetadataString(url);

            soft.assertTrue(metadata.contains(pScheme), "Checking XML contains proper participant scheme for metadata " + docId);
            soft.assertTrue(metadata.contains(pIdentifier), "Checking XML contains proper participant ID for metadata " + docId);
            soft.assertTrue(metadata.toLowerCase().contains(docId.toLowerCase()), "Checking XML contains proper document ID for metadata " + docId);
            soft.assertTrue(metadata.contains(docScheme), "Checking XML contains proper document scheme for metadata " + docId);


        }

        soft.assertAll();
    }

    @Test(description = "SRCH-60")
    public void collapseMetadata() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        ServiceGroupRow row0 = null;
        List<ServiceGroupRow> rows = page.getServiceGroupGrid().getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getMetadataSize() > 0) {
                row0 = rows.get(i);
            }
        }

        if (null == row0) {
            row0 = rows.get(0);
            SMPRestClient.createMetadata(row0.getParticipantIdentifier());
            page.refreshPage();
            logger.info("Created Metadata for row 0");
            row0 = page.getServiceGroupGrid().getRows().get(0);
        }


        soft.assertTrue(row0.verifyMetadataExpandButton(), "Initially the row has expanding symbol on it");
        row0.expandMetadata();

        soft.assertTrue(row0.verifyMetadataCollapseButton(), "Row has collapsing symbol on it after first click");

        row0.collapseMetadata();
        soft.assertTrue(row0.verifyMetadataExpandButton(), "Row has expanding symbol on it after collapse");
        soft.assertFalse(row0.isMetadataExpanded(), "Metadata table is not present no more");

        soft.assertAll();
    }

    @Test(description = "SRCH-70")
    public void verifyOpenMetadataURL() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());

        ServiceGroupRow row0 = null;
        List<ServiceGroupRow> rows = page.getServiceGroupGrid().getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getMetadataSize() > 0) {
                row0 = rows.get(i);
            }
        }

        if (null == row0) {
            row0 = rows.get(0);
            SMPRestClient.createMetadata(row0.getParticipantIdentifier());
            page.refreshPage();
            logger.info("Created Metadata for row 0");
            row0 = page.getServiceGroupGrid().getRows().get(0);
        }

        String listedURL = row0.getServiceGroupURL();
        String pScheme = row0.getParticipantScheme();
        String pIdentifier = row0.getParticipantIdentifier();

//		verify proper URL format
        ServiceGroup serviceGroup = SMPRestClient.getServiceGroup(listedURL);

        MetadataGrid metadataGrid = row0.expandMetadata();

        List<MetadataRow> metadataRows = metadataGrid.getMetadataRows();

        soft.assertTrue(row0.getMetadataSize() == metadataRows.size(), "Metadata size field compared with the size of the list containing the metadata");

        for (MetadataRow metadataRow : metadataRows) {
            String docScheme = metadataRow.getDocumentIdentifierScheme();
            String docId = metadataRow.getDocumentIdentifier();
            String url = null;
            try {
                url = URLDecoder.decode(metadataRow.getURL(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            soft.assertTrue(url.contains(String.format("%s::%s/services/%s::%s", pScheme, pIdentifier, docScheme, docId)), "Checking URL format for metadata " + docId);

            String mainWindow = driver.getWindowHandle();

            metadataRow.clickURL();
            page.waitForNumberOfWindowsToBe(2);
            Set<String> handleSet = driver.getWindowHandles();
            String[] handles = handleSet.toArray(new String[handleSet.size()]);

            soft.assertTrue(handles.length == 2);

            driver.switchTo().window(handles[1]);
            driver.close();
            driver.switchTo().window(handles[0]);

        }

        soft.assertAll();
    }

    @Test(description = "SRCH-80")
    public void filterByDifferentDomains() {
        SoftAssert soft = new SoftAssert();

        String participantID = Generator.randomAlphaNumeric(5);
        String tmp = Generator.randomAlphaNumeric(3).toLowerCase();
        String participantScheme = String.format("%s-%s-%s", tmp, tmp, tmp);

        List<String> domains = Arrays.asList(createdDomains.get(0), createdDomains.get(1));
        List<String> owners = Arrays.asList(createdUsers.get(0));

        logger.info("Creating service group with participant id: " + participantID);
        SMPRestClient.createServiceGroup(participantID, participantScheme, owners, domains);

        SearchPage searchPage = new SearchPage(driver);
        searchPage.refreshPage();

        searchPage.filters.filter(participantID, participantScheme, SMPRestClient.getDomainSubDomainCombo(createdDomains.get(0)));
        List<ServiceGroupRow> results = searchPage.getServiceGroupGrid().getRows();

        soft.assertEquals(results.size(), 1, "Results size is 1 (first search)");
        soft.assertEquals(results.get(0).getParticipantIdentifier().toLowerCase(), participantID.toLowerCase(),
                "First and only result is the one we entered and is found when filtering by first domain");


        searchPage.filters.filter(participantID, participantScheme, SMPRestClient.getDomainSubDomainCombo(createdDomains.get(1)));
        results = searchPage.getServiceGroupGrid().getRows();

        soft.assertEquals(results.size(), 1, "Results size is 1 (second search)");
        soft.assertEquals(results.get(0).getParticipantIdentifier().toLowerCase(), participantID.toLowerCase(),
                "First and only result is the one we entered and is found when filtering by second domain");


        SMPRestClient.deleteSG(participantID);

        soft.assertAll();
    }

    @Test(description = "SRCH-90")
    public void verifyDifferentParticipantIdAndSchemeResult() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());
        String emptyMsg = "No data to display";

        ServiceGroupRow row0 = page.getServiceGroupGrid().getRows().get(0);
        String pScheme = row0.getParticipantScheme();

        ServiceGroupRow row1 = page.getServiceGroupGrid().getRows().get(1);
        String pIdentifier = row1.getParticipantIdentifier();

        page.filters.filter(pIdentifier, pScheme, "");

        soft.assertEquals(page.getServiceGroupGrid().getEmptyTableText(), emptyMsg, "empty table not found");
        soft.assertAll();
    }

    @Test(description = "SRCH-100")
    public void metadataTableContent() {
        SoftAssert soft = new SoftAssert();

        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());
        ServiceGroupRow row0 = null;
        List<ServiceGroupRow> rows = page.getServiceGroupGrid().getRows();
        row0 = rows.get(0);
        MetadataGrid metadataGrid = row0.expandMetadata();
        List<MetadataRow> metadataRows = metadataGrid.getMetadataRows();
        if (row0.getMetadataSize() == 0) {
            //row0.emptyMetadataContentText()
            soft.assertEquals(row0.emptyMetadataContentText(), "No service metadata");
        } else {
            if (row0.getMetadataSize() > 0) {
                soft.assertTrue(row0.getMetadataSize() == metadataRows.size(), "metadata size is not equal to no of metadata present inside the corressponding row");
            }

        }
        soft.assertAll();
    }

    @Test(description = "SRCH-101")
    public void verifyCollapsingSidebarPageAfterLogin() {
        SoftAssert soft = new SoftAssert();
        SearchPage page = new SearchPage(driver);
        soft.assertTrue(page.isLoaded());
        page.sidebar.collapsingSideBar();
        soft.assertFalse(page.sidebar.isSidebarSearchTextEnable(), "Search button is visible so sidebar page is not collpased");
        page.sidebar.expandingSideBar();
        soft.assertTrue(page.sidebar.isSidebarSearchTextEnable(), "Search button is not visible so sidebar page is not expanding");
        soft.assertAll();
    }
}
