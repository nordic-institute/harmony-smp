package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;



public class SmlConnectorTestConstants {
    //protected static final Identifier PARTICIPANT_ID = new Identifier("sample:value", "sample:scheme");
    protected static final Identifier PARTICIPANT_ID = null;
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

}
