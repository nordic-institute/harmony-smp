package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
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

import java.security.Security;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageParticipantIdentifierWSDeleteListTest extends AbstractTest {

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
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteListEmpty() throws Exception {
        ParticipantIdentifierPageType participantType = new ParticipantIdentifierPageType();
        manageParticipantIdentifierWS.deleteList(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteListNull() throws Exception {
        manageParticipantIdentifierWS.deleteList(null);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteListParticipantWithSMPNotCreatedByUser() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue("0007:123456789");
        participantType.getParticipantIdentifier().get(1).setValue("0060:123456789");
        participantType.setServiceMetadataPublisherID("found");
        manageParticipantIdentifierWS.deleteList(participantType);
    }

    @Test(expected = NotFoundFault.class)
    public void testDeleteListParticipantNotAlreadyExist() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue("0009:523456789");
        participantType.getParticipantIdentifier().get(1).setValue("0009:9923456789");
        participantType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.deleteList(participantType);
    }

    @Test
    public void testDeleteListParticipantOk() throws Exception {
        // First we ensure the participants don't exist
        // Ids must be case insensitive so we put them in upper case to test the case insensitivity
        final String partId1 = "0009:123456789DELETELIST";
        final String partId2 = "0009:223456789DELETELIST";
        final String smpId = "FOUNDUNSECURE";
        ParticipantBO partBO = new ParticipantBO();
        partBO.setSmpId(smpId);
        partBO.setScheme("iso6523-actorid-upis");
        partBO.setParticipantId(partId1);
        ParticipantBO found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);
        partBO.setParticipantId(partId2);
        found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);

        // Then we create the participants
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue(partId1);
        participantType.getParticipantIdentifier().get(1).setValue(partId2);
        participantType.setServiceMetadataPublisherID(smpId);
        manageParticipantIdentifierWS.deleteList(participantType);

        // Finally we verify that the participants have been created
        found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);
        partBO.setParticipantId(partId1);
        found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteWithMigrationPlanned() throws Exception {
        ParticipantIdentifierType participantType1 = new ParticipantIdentifierType();
        participantType1.setScheme("iso6523-actorid-upis");
        participantType1.setValue("0009:toBeDeletedWithMigrationPlanned");
        ParticipantIdentifierPageType participantIdentifierPageType = new ParticipantIdentifierPageType();
        participantIdentifierPageType.getParticipantIdentifier().add(participantType1);
        participantIdentifierPageType.setServiceMetadataPublisherID("toBeDeletedWithMigrationPlanned");
        manageParticipantIdentifierWS.deleteList(participantIdentifierPageType);
    }

    private ParticipantIdentifierPageType createParticipant() {
        ParticipantIdentifierType participantType1 = new ParticipantIdentifierType();
        participantType1.setScheme("iso6523-actorid-upis");

        ParticipantIdentifierType participantType2 = new ParticipantIdentifierType();
        participantType2.setScheme("iso6523-actorid-upis");

        ParticipantIdentifierPageType participantIdentifierPageType = new ParticipantIdentifierPageType();
        participantIdentifierPageType.getParticipantIdentifier().add(participantType1);
        participantIdentifierPageType.getParticipantIdentifier().add(participantType2);
        participantIdentifierPageType.setServiceMetadataPublisherID("NotFound");

        return participantIdentifierPageType;
    }
}

