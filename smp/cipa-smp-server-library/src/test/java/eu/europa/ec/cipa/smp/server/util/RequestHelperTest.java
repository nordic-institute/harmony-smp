package eu.europa.ec.cipa.smp.server.util;
import com.helger.commons.collections.CollectionHelper;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import com.helger.web.http.basicauth.HTTPBasicAuth;
import eu.europa.ec.cipa.smp.server.exception.UnauthorizedException;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
/**
 * Created by rodrfla on 13/01/2017.
 */
public class RequestHelperTest {

    @Test
    public void getServiceGroupOwner() {
        String username = "Dummy_User_From_Header";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("ServiceGroup-Owner", Arrays.asList(new String[]{username}));

        String retrivedUsername = RequestHelper.getServiceGroupOwner(defaultHttpHeader);
        RequestHelper.getServiceGroupOwner(defaultHttpHeader);
        assertNotNull(retrivedUsername);
        assertEquals(username, retrivedUsername);
    }

    @Test
    public void getServiceGroupOwnerWrongHeader() {
        String username = "Dummy_User_From_Header";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("ServiceGroup-WrongOwner", Arrays.asList(new String[]{username}));

        String retrivedUsername = RequestHelper.getServiceGroupOwner(defaultHttpHeader);
        RequestHelper.getServiceGroupOwner(defaultHttpHeader);
        assertNull(retrivedUsername);
    }
}
