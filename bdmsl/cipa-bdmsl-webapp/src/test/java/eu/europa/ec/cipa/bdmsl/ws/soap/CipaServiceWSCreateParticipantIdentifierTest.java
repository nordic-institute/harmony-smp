package eu.europa.ec.cipa.bdmsl.ws.soap;

import ec.services.wsdl.bdmsl.data._1.SMPAdvancedServiceForParticipantType;
import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.mock.DnsMessageSenderServiceMock;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
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

import java.security.Security;

/**
 * Created by feriaad on 09/07/2015.
 */
public class CipaServiceWSCreateParticipantIdentifierTest extends AbstractTest {

    @Autowired
    private ICipaServiceWS cipaServiceWS;

    @Autowired
    private IParticipantDAO participantDAO;

    @Autowired
    private IDnsMessageSenderService dnsMessageSenderService;


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
    public void testCreateParticipantIdentifierEmpty() throws Exception {
        SMPAdvancedServiceForParticipantType participantType = new SMPAdvancedServiceForParticipantType();
        cipaServiceWS.createParticipantIdentifier(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantIdentifierNull() throws Exception {
        cipaServiceWS.createParticipantIdentifier(null);
    }

    @Test(expected = NotFoundFault.class)
    public void testCreateParticipantIdentifierWithoutExistingSMP() throws Exception {
        SMPAdvancedServiceForParticipantType participantType = createParticipant();
        cipaServiceWS.createParticipantIdentifier(participantType);
    }

    @Test
    public void testCreateParticipantIdentifierOk() throws Exception {
        final String partId = "0009:123456789CreateParticipantIdentifierNotExists";
        final String smpId = "foundUnsecure";
        SMPAdvancedServiceForParticipantType participantType = createParticipant();
        participantType.getCreateParticipantIdentifier().getParticipantIdentifier().setValue(partId);
        participantType.getCreateParticipantIdentifier().setServiceMetadataPublisherID(smpId);

        // First we ensure the participant doesn't exist
        ParticipantBO partBO = new ParticipantBO();
        partBO.setSmpId(smpId);
        partBO.setScheme("iso6523-actorid-upis");
        partBO.setParticipantId(partId);
        ParticipantBO found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);

        DnsMessageSenderServiceMock dnsMessageSenderService = (DnsMessageSenderServiceMock) this.dnsMessageSenderService;
        Assert.assertTrue(dnsMessageSenderService.getMessages().isEmpty());

        cipaServiceWS.createParticipantIdentifier(participantType);

        String messages = dnsMessageSenderService.getMessages();
        Assert.assertTrue(messages.contains("B-548113997c2b615d667ac512da31474f.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("B-1d51d91db4358ccf472afb065b628d9d03a3c20b376ba4667c5ff4b8.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("B-548113997c2b615d667ac512da31474f.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tCNAME\tfoundUnsecure.publisher.acc.edelivery.tech.ec.europa.eu."));
        Assert.assertTrue(messages.contains("B-1d51d91db4358ccf472afb065b628d9d03a3c20b376ba4667c5ff4b8.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tNAPTR\t100 10 \"U\" \"META:SMP_TEST\" \"!^.$!http://foundUnsecure.publisher.acc.edelivery.tech.ec.europa.eu.!\" ."));


        // Finally we verify that the participant has been created
        found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);
    }

    private SMPAdvancedServiceForParticipantType createParticipant() {
        ServiceMetadataPublisherServiceForParticipantType participantType = new ServiceMetadataPublisherServiceForParticipantType();
        ParticipantIdentifierType partIdType = new ParticipantIdentifierType();
        partIdType.setScheme("iso6523-actorid-upis");
        partIdType.setValue("0088:123456789");
        participantType.setParticipantIdentifier(partIdType);
        participantType.setServiceMetadataPublisherID("NotFound");
        SMPAdvancedServiceForParticipantType advancedParticipantType = new SMPAdvancedServiceForParticipantType();
        advancedParticipantType.setCreateParticipantIdentifier(participantType);
        advancedParticipantType.setServiceName("META:SMP_TEST");
        return advancedParticipantType;
    }
}
