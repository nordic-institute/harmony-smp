package eu.europa.ec.cipa.smp.server.util;

import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import eu.europa.ec.cipa.smp.server.errors.exceptions.AuthenticationException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.security.PreAuthenticatedCertificatePrincipal;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.Principal;
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
        Principal principal = new PreAuthenticatedCertificatePrincipal("CN=SMP_7,O=DG-DIGIT,C=X", "CN=PEPPOL,O=X,C=Y", "123");
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(principal, "N/A");
        authentication.setDetails(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(new DefaultHttpHeader(), false);

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
