package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageParticipantIdentifierWSDeleteTest extends AbstractTest {

    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

    @Autowired
    private IParticipantDAO participantDAO;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteEmpty() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = new ServiceMetadataPublisherServiceForParticipantType();
        manageParticipantIdentifierWS.delete(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteNull() throws Exception {
        manageParticipantIdentifierWS.delete(null);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteParticipantWithSMPNotCreatedByUser() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0007:123456789");
        participantType.setServiceMetadataPublisherID("found");
        manageParticipantIdentifierWS.delete(participantType);
    }

    @Test(expected = NotFoundFault.class)
    public void testDeleteParticipantNotAlreadyExist() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0007:notAlreadyExist");
        participantType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.delete(participantType);
    }

    @Test
    public void testDeleteParticipantOk() throws Exception {
        // First we ensure that the participant exists
        final String partId = "0009:123456789AlwaysPresent";
        final String smpId = "foundUnsecure";
        ParticipantBO partBO = new ParticipantBO();
        partBO.setSmpId(smpId);
        partBO.setScheme("iso6523-actorid-upis");
        partBO.setParticipantId(partId);
        ParticipantBO found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);

        // Then we delete the participant
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue(partId);
        participantType.setServiceMetadataPublisherID(smpId);
        manageParticipantIdentifierWS.delete(participantType);

        // Finally we verify that the participant has been deleted
        found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteWithMigrationPlanned() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0009:toBeDeletedWithMigrationPlanned");
        participantType.setServiceMetadataPublisherID("toBeDeletedWithMigrationPlanned");
        manageParticipantIdentifierWS.delete(participantType);
    }


    private ServiceMetadataPublisherServiceForParticipantType createParticipant() {
        ServiceMetadataPublisherServiceForParticipantType participantType = new ServiceMetadataPublisherServiceForParticipantType();
        ParticipantIdentifierType partIdType = new ParticipantIdentifierType();
        partIdType.setScheme("iso6523-actorid-upis");
        partIdType.setValue("0088:123456789");
        participantType.setParticipantIdentifier(partIdType);
        participantType.setServiceMetadataPublisherID("NotFound");
        return participantType;
    }
}

