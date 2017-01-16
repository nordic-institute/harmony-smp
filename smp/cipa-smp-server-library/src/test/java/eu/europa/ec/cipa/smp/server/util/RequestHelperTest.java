package eu.europa.ec.cipa.smp.server.util;

import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import eu.europa.ec.cipa.smp.server.exception.UnauthorizedException;
import org.junit.Test;

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

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader);
        assertNotNull(retrivedAuth.getUserName());
        assertEquals(username, retrivedAuth.getUserName());
    }

    @Test(expected = UnauthorizedException.class)
    public void getServiceGroupOwnerWrongHeader() {
        String username = "Dummy_User_From_Header";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("ServiceGroup-WrongOwner", Arrays.asList(new String[]{username}));

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader);
        assertNull(retrivedAuth.getUserName());
    }

    @Test
    public void getDefaultUsername() {
        String username = "Basic c21wX2RlbW9fdXNlcjoxMjM0NTY3OF9kdW15X3Bhc3N3b3Jk";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Authorization", Arrays.asList(new String[]{username}));

        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader);
        assertNotNull(retrivedAuth.getUserName());
        assertEquals("smp_demo_user", retrivedAuth.getUserName());
        assertEquals("12345678_dumy_password", retrivedAuth.getPassword());
    }

    @Test(expected = UnauthorizedException.class)
    public void getDefaultUsernameNotFound() {
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        BasicAuthClientCredentials retrivedAuth = RequestHelper.getAuth(defaultHttpHeader);
    }
}
