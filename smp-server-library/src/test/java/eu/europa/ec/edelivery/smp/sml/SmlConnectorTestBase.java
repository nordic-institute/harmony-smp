package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmlConnector.class, SmlIntegrationConfiguration.class,
        SecurityUtilsServices.class, UIKeystoreService.class,
        ConversionTestConfig.class, PropertiesSingleDomainTestConfig.class})
@Configuration
@TestPropertySource(properties = {
        "bdmsl.integration.enabled=true"})
public class SmlConnectorTestBase {
    protected static final ParticipantIdentifierType PARTICIPANT_ID = new ParticipantIdentifierType("sample:value", "sample:scheme");
    protected static final DBDomain DEFAULT_DOMAIN;

    static {
        DEFAULT_DOMAIN = new DBDomain();
        DEFAULT_DOMAIN.setDomainCode("default_domain_id");
        DEFAULT_DOMAIN.setSmlSmpId("SAMPLE-SMP-ID");
    }

    protected static final String ERROR_UNEXPECTED_MESSAGE = "[ERR-106] Something unexpected happend";
    protected static final String ERROR_SMP_NOT_EXISTS = "[ERR-100] The SMP '" + DEFAULT_DOMAIN.getSmlSmpId() + "' doesn't exist";
    protected static final String ERROR_SMP_ALREADY_EXISTS = "[ERR-106] The SMP '" + DEFAULT_DOMAIN.getSmlSmpId() + "' already exists";
    protected static final String ERROR_PI_ALREADY_EXISTS = "[ERR-106] The participant identifier 'sample:value' does already exist for the scheme sample:scheme";
    protected static final String ERROR_PI_NO_EXISTS = "[ERR-100] The participant identifier 'sample:value' doesn't exist for the scheme sample:scheme";


    @Autowired
    protected SmlConnector smlConnector;
    @Autowired
    SmlIntegrationConfiguration mockSml;

}
