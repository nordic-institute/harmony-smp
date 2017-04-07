package eu.europa.ec.cipa.smp.server.security;

import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by gutowpa on 05/04/2017.
 */
public class PreAuthenticatedCertificatePrincipalTest {

    private static final String ANY_VALID_DN="C=PL,O=org,CN=common name";

    @Test
    public void resultFieldsAreNormalized(){
        //given
        String subjectDN = "C=BE, CN=common name,   OU=org unit 1,OU=org unit 2,O=org";
        String issuerDN = "C=PL,O=issuer org,CN=issuer common name";
        String serial = "c400";

        //when
        PreAuthenticatedCertificatePrincipal principal = new PreAuthenticatedCertificatePrincipal(subjectDN, issuerDN, serial);

        //then
        assertEquals("CN=common name,O=org,C=BE:000000000000c400", principal.getName());
        assertEquals("CN=common name,O=org,C=BE", principal.getSubjectDN());
        assertEquals("CN=issuer common name,O=issuer org,C=PL", principal.getIssuerDN());
    }

    @Test
    public void serialNumberDoesNotContainHexPrefix(){
        //given-when
        PreAuthenticatedCertificatePrincipal principal = new PreAuthenticatedCertificatePrincipal(ANY_VALID_DN, ANY_VALID_DN, "0x00f7");

        //then
        assertEquals("CN=common name,O=org,C=PL:00000000000000f7", principal.getName());
    }

    @Test
    public void serialNumberDoesNotContainColonHexSeparators(){
        //given-when
        PreAuthenticatedCertificatePrincipal principal = new PreAuthenticatedCertificatePrincipal(ANY_VALID_DN, ANY_VALID_DN, "ab:cd:ef:01:23:45");

        //then
        assertEquals("CN=common name,O=org,C=PL:0000abcdef012345", principal.getName());
    }

    @Test(expected = BadCredentialsException.class)
    public void rejectInvalidDomainNamePatternForIssuer(){
        new PreAuthenticatedCertificatePrincipal(ANY_VALID_DN, "C=BE,+O=org,CN=common name", "00");
    }

    @Test(expected = BadCredentialsException.class)
    public void rejectInvalidDomainNamePatternForSubject(){
        new PreAuthenticatedCertificatePrincipal("+C=BE,CN=common name", ANY_VALID_DN, "00");
    }

    @Test
    public void supportSerialAsBigInteger(){
        //given
        BigInteger serial = BigInteger.valueOf(256);

        //when
        PreAuthenticatedCertificatePrincipal principal = new PreAuthenticatedCertificatePrincipal(ANY_VALID_DN, ANY_VALID_DN, serial);

        //then
        assertEquals("CN=common name,O=org,C=PL:0000000000000100", principal.getName());
    }
}
