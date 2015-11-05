package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
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
public class ManageParticipantIdentifierWSCreateListTest extends AbstractTest {

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
    public void testCreateListEmpty() throws Exception {
        ParticipantIdentifierPageType participantType = new ParticipantIdentifierPageType();
        manageParticipantIdentifierWS.createList(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateListNull() throws Exception {
        manageParticipantIdentifierWS.createList(null);
    }

    @Test(expected = NotFoundFault.class)
    public void testCreateListParticipantWithoutExistingSMP() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue("0009:723456789");
        participantType.getParticipantIdentifier().get(1).setValue("0009:823456789");
        manageParticipantIdentifierWS.createList(participantType);
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

    @Test(expected = UnauthorizedFault.class)
    public void testCreateListParticipantWithSMPNotCreatedByUser() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue("0009:523456789");
        participantType.getParticipantIdentifier().get(1).setValue("0009:623456789");
        participantType.setServiceMetadataPublisherID("found");
        manageParticipantIdentifierWS.createList(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateListParticipantAlreadyExist() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.createList(participantType);
    }

    @Test
    public void testCreateListParticipantOk() throws Exception {
        // First we ensure the participants don't exist
        final String partId1 = "0009:323456789";
        final String partId2 = "0009:423456789";
        final String smpId = "foundUnsecure";
        ParticipantBO partBO = new ParticipantBO();
        partBO.setSmpId(smpId);
        partBO.setScheme("iso6523-actorid-upis");
        partBO.setParticipantId(partId1);
        ParticipantBO found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);
        partBO.setParticipantId(partId2);
        found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);

        // Then we create the participants
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.getParticipantIdentifier().get(0).setValue(partId1);
        participantType.getParticipantIdentifier().get(1).setValue(partId2);
        participantType.setServiceMetadataPublisherID(smpId);
        manageParticipantIdentifierWS.createList(participantType);

        // Finally we verify that the participants have been created
        found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);
        partBO.setParticipantId(partId1);
        found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateListParticipantIllegalIssuingAgency() throws Exception {
        ParticipantIdentifierPageType participantType = createParticipant();
        participantType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.createList(participantType);
    }

}

