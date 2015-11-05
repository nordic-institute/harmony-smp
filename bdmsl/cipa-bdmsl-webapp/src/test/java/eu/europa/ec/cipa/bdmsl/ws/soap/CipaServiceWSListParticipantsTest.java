package eu.europa.ec.cipa.bdmsl.ws.soap;

import ec.services.wsdl.bdmsl.data._1.IsAliveType;
import ec.services.wsdl.bdmsl.data._1.ListParticipantsInType;
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

import java.security.Security;

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
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void isAliveTest() {
        cipaServiceWS.isAlive(new IsAliveType());
    }

    @Test
    public void listParticipantsOk() throws UnauthorizedFault, InternalErrorFault, TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertTrue(!cipaServiceWS.listParticipants(new ListParticipantsInType()).getParticipant().isEmpty());
    }

    @Test(expected = UnauthorizedFault.class)
    public void listParticipantsNotAuthorized() throws UnauthorizedFault, InternalErrorFault, TechnicalException {
        UnsecureAuthenticationRoleSMP authentication = new UnsecureAuthenticationRoleSMP();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertTrue(!cipaServiceWS.listParticipants(new ListParticipantsInType()).getParticipant().isEmpty());
    }
}
