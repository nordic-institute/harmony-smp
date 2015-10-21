package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthenticationRoleSMP;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by feriaad on 09/07/2015.
 */
public class CipaServiceWSListParticipantsTest extends AbstractTest {

    @Autowired
    private ICipaServiceWS cipaServiceWS;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void isAliveTest() {
        cipaServiceWS.isAlive();
    }

    @Test
    public void listParticipantsOk() throws UnauthorizedFault, InternalErrorFault, TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertTrue(!cipaServiceWS.listParticipants().getParticipant().isEmpty());
    }

    @Test(expected = UnauthorizedFault.class)
    public void listParticipantsNotAuthorized() throws UnauthorizedFault, InternalErrorFault, TechnicalException {
        UnsecureAuthenticationRoleSMP authentication = new UnsecureAuthenticationRoleSMP();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertTrue(!cipaServiceWS.listParticipants().getParticipant().isEmpty());
    }
}
