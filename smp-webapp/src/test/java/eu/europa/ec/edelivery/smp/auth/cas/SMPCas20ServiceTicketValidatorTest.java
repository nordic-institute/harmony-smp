package eu.europa.ec.edelivery.smp.auth.cas;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SMPCas20ServiceTicketValidatorTest {

    @Test
    public void testGetUrlSuffix() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = "urlSuffix";

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);

        assertEquals(casSuffix, testInstance.getUrlSuffix());
    }

    @Test
    public void testGetUrlSuffixDefault() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = null;

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);

        assertEquals("serviceValidate", testInstance.getUrlSuffix());
    }
}