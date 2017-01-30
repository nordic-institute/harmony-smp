package eu.europa.ec.cipa.smp.server.util;

import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import eu.europa.ec.cipa.smp.server.errors.exceptions.AuthenticationException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.security.BlueCoatClientCertificateAuthentication;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.junit.Assert.*;
/**
 * Created by rodrfla on 13/01/2017.
 */
public class RequestHelperTest {

    @Test
    public void getServiceGroupOwner() {
        String username = "Dummy_User_From_Header";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("ServiceGroup-Owner", Arrays.asList(new String[]{username}));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader, true);
        assertNotNull(retrivedAuth.getUserName());
        assertEquals(username, retrivedAuth.getUserName());
    }

    @Test(expected = UnauthorizedException.class)
    public void getServiceGroupOwnerWrongHeader() {
        String username = "Dummy_User_From_Header";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("ServiceGroup-WrongOwner", Arrays.asList(new String[]{username}));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader, true);
        assertNull(retrivedAuth.getUserName());
    }

    @Test
    public void getDefaultUsername() {
        String username = "Basic c21wX2RlbW9fdXNlcjoxMjM0NTY3OF9kdW15X3Bhc3N3b3Jk";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Authorization", Arrays.asList(new String[]{username}));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader, false);
        assertNotNull(retrivedAuth.getUserName());
        assertEquals("smp_demo_user", retrivedAuth.getUserName());
        assertEquals("12345678_dumy_password", retrivedAuth.getPassword());
    }

    @Test
    public void getBlueCoatUsername() throws AuthenticationException {
        //given
        String clientCertHeader = "serial=0000000000000123&subject=CN=SMP_7,O=DG-DIGIT,C=X&validFrom=Oct 21 02:00:00 2014 CEST&validTo=Oct 21 01:59:59 2018 CEST&issuer=CN=PEPPOL,O=X,C=Y";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Client-Cert", Arrays.asList(clientCertHeader));
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));

        //when
        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader, false);

        //then
        assertNotNull(retrivedAuth.getUserName());
        assertEquals("CN=SMP_7,O=DG-DIGIT,C=X:0000000000000123", retrivedAuth.getUserName());
        assertNull(retrivedAuth.getPassword());
    }

    @Test(expected = UnauthorizedException.class)
    public void getDefaultUsernameNotFound() {
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        RequestHelper.getAuth(defaultHttpHeader, false);
    }
}
