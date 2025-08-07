package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.*;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class Oasis20GenerationIT extends AbstractControllerTest {

    public static final Logger LOG = LoggerFactory.getLogger(Oasis20GenerationIT.class);

    @Autowired private DomainDao domainDao;
    @Autowired private ExtensionDao extensionDao;
    @Autowired private ResourceDefDao resourceDefDao;
    @Autowired private SubresourceDefDao subresourceDefDao;
    @Autowired private DomainResourceDefDao domainResourceDefDao;
    @Autowired private PlatformTransactionManager transactionManager;

    private static final String OASIS_2_RESOURCE_SEGMENT = "bdxr-smp-2";
    private static final String OASIS_2_RESOURCE_IDENTIFIER = "edelivery-oasis-smp-2.0-servicegroup";
    private static final String OASIS_2_SUBRESOURCE_IDENTIFIER = "edelivery-oasis-smp-2.0-servicemetadata";

    @Override
    @BeforeEach
    public void setup() throws IOException {
        super.setup();

        new TransactionTemplate(transactionManager).execute(status -> {
            prepareOasis20Definitions();
            return null;
        });
    }

    @ParameterizedTest(name = "Accepts OASIS 2.0 format: {0}")
    @CsvSource({
        "'Draft', " + OASIS2_DRAFT_XML_TEMPLATE,
        "'Final', " + OASIS2_FINAL_XML_TEMPLATE
    })
    void acceptsOasis20DraftAndFinalFormats(String testName, String xmlMetadataTemplate) throws Exception {
        LOG.info("Executing test: {}", testName);

        String participantId = UUID.randomUUID().toString();

        String participantUrl = String.format("/%s/%s::%s", OASIS_2_RESOURCE_SEGMENT, IDENTIFIER_SCHEME, participantId);

        String xmlSG = getSampleOasis2ServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        mvc.perform(put(participantUrl)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlSG))
            .andExpect(status().isCreated());

        String subresourceURL = String.format("%s/services/%s::%s", participantUrl, DOCUMENT_SCHEME, DOCUMENT_ID);

        mvc.perform(put(subresourceURL)
                .with(ADMIN_CREDENTIALS)
                .header("Domain", "domain")
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlMetadataTemplate))
            .andExpect(status().isCreated());

        String xpathExpression = String.format(
            "/*[local-name()='ServiceMetadata']/*[local-name()='%s'][@schemeID='%s'][text()='%s']",
            testName.equals("Draft") ? "ServiceID" : "ID",
            DOCUMENT_SCHEME,
            DOCUMENT_ID
        );

        mvc.perform(get(subresourceURL)
                .with(ADMIN_CREDENTIALS))
            .andExpect(status().isOk())
            .andExpect(xpath(
                "/*[local-name()='ServiceMetadata']/*[local-name()='ParticipantID'][@schemeID='%s'][text()='%s']",
                IDENTIFIER_SCHEME, participantId
            ).exists())
            .andExpect(xpath(xpathExpression).exists());
    }

    private void prepareOasis20Definitions() {
        DBDomain domain = domainDao.getDomainByCode("domain").get();
        DBExtension oasisExtension = extensionDao.getExtensionByIdentifier("edelivery-oasis-smp-extension").get();

        DBResourceDef resourceDef20 = new DBResourceDef();
        resourceDef20.setExtension(oasisExtension);
        resourceDef20.setIdentifier(OASIS_2_RESOURCE_IDENTIFIER);
        resourceDef20.setUrlSegment(OASIS_2_RESOURCE_SEGMENT);
        resourceDef20.setName("Oasis SMP 2.0 ServiceGroup IT");
        resourceDef20.setMimeType("text/xml");
        resourceDefDao.persist(resourceDef20);

        DBSubresourceDef subresourceDef20 = new DBSubresourceDef();
        subresourceDef20.setResourceDef(resourceDef20);
        subresourceDef20.setIdentifier(OASIS_2_SUBRESOURCE_IDENTIFIER);
        subresourceDef20.setUrlSegment("services");
        subresourceDef20.setName("Oasis SMP 2.0 ServiceMetadata IT");
        subresourceDef20.setMimeType("text/xml");
        subresourceDefDao.persist(subresourceDef20);

        DBDomainResourceDef domainResourceDef = new DBDomainResourceDef();
        domainResourceDef.setDomain(domain);
        domainResourceDef.setResourceDef(resourceDef20);
        domainResourceDefDao.persist(domainResourceDef);
    }
}