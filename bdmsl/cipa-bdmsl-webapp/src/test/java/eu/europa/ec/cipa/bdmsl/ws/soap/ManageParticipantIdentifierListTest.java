package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Security;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageParticipantIdentifierListTest extends AbstractTest {

    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

    @Autowired
    private IParticipantDAO participantDAO;

    private static boolean initialized;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Before
    public void init() throws Exception {
        super.init();
        if (!initialized) {
            final String smpId = "smpForListTest";
            // First insert 50 participants
            for (int i = 0; i < 50; i++) {
                ServiceMetadataPublisherServiceForParticipantType participantType = new ServiceMetadataPublisherServiceForParticipantType();
                ParticipantIdentifierType partIdType = new ParticipantIdentifierType();
                partIdType.setScheme("iso6523-actorid-upis");
                partIdType.setValue("0088:" + i);
                participantType.setParticipantIdentifier(partIdType);
                participantType.setServiceMetadataPublisherID(smpId);
                manageParticipantIdentifierWS.create(participantType);
            }
            initialized = true;
        }
    }

    @Test(expected = BadRequestFault.class)
    public void testListEmpty() throws Exception {
        PageRequestType pageRequestType = new PageRequestType();
        manageParticipantIdentifierWS.list(pageRequestType);
    }

    @Test(expected = BadRequestFault.class)
    public void testListNull() throws Exception {
        manageParticipantIdentifierWS.delete(null);
    }

    @Test
    public void testListParticipantOk() throws Exception {
        final String smpId = "smpForListTest";
        // Then retrieve the data
        PageRequestType pageRequestType = new PageRequestType();
        pageRequestType.setServiceMetadataPublisherID(smpId);

        // no pageNumber -> pageNumber = 1 : the first page is page number 1
        ParticipantIdentifierPageType result = manageParticipantIdentifierWS.list(pageRequestType);
        int count = 0;
        int pageNumber = 2;
        while (result != null && result.getParticipantIdentifier() != null && result.getParticipantIdentifier().size() > 0) {
            for (ParticipantIdentifierType part : result.getParticipantIdentifier()) {
                count++;
            }
            pageRequestType.setNextPageIdentifier(String.valueOf(pageNumber));
            pageNumber++;
            result = manageParticipantIdentifierWS.list(pageRequestType);
        }

        Assert.assertEquals(50, count);
    }

    @Test
    public void testListParticipantPageRequestTooHigh() throws Exception {
        final String smpId = "smpForListTest";
        // Then retrieve the data
        PageRequestType pageRequestType = new PageRequestType();
        pageRequestType.setServiceMetadataPublisherID(smpId);
        pageRequestType.setNextPageIdentifier(String.valueOf(5000));
        ParticipantIdentifierPageType result = manageParticipantIdentifierWS.list(pageRequestType);
        Assert.assertNull(result);
    }

}

