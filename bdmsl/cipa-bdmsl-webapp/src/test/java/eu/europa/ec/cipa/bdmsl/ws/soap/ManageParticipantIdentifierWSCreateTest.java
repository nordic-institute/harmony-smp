package eu.europa.ec.cipa.bdmsl.ws.soap;

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
 * Created by feriaad on 16/06/2015.
 */
public class ManageParticipantIdentifierWSCreateTest extends AbstractTest {

    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

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
    public void testCreateEmpty() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = new ServiceMetadataPublisherServiceForParticipantType();
        manageParticipantIdentifierWS.create(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateNull() throws Exception {
        manageParticipantIdentifierWS.create(null);
    }

    @Test(expected = NotFoundFault.class)
    public void testCreateParticipantWithoutExistingSMP() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        manageParticipantIdentifierWS.create(participantType);
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

    @Test(expected = UnauthorizedFault.class)
    public void testCreateParticipantWithSMPNotCreatedByUser() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0007:223456789");
        participantType.setServiceMetadataPublisherID("found");
        manageParticipantIdentifierWS.create(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantAlreadyExist1() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0008:123456789");
        participantType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.create(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantAlreadyExist2() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue("0009:CASEINSENSITIVITYCHECK");
        participantType.setServiceMetadataPublisherID("FOUNDUNSECURE");
        manageParticipantIdentifierWS.create(participantType);
    }

    @Test
    public void testCreateParticipantOk() throws Exception {
        final String partId = "0009:123456789NotExists";
        final String smpId = "foundUnsecure";
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue(partId);
        participantType.setServiceMetadataPublisherID(smpId);

        DnsMessageSenderServiceMock dnsMessageSenderService = (DnsMessageSenderServiceMock) this.dnsMessageSenderService;
        Assert.assertTrue(dnsMessageSenderService.getMessages().isEmpty());

        // First we ensure the participant doesn't exist
        ParticipantBO partBO = new ParticipantBO();
        partBO.setSmpId(smpId);
        partBO.setScheme("iso6523-actorid-upis");
        partBO.setParticipantId(partId);
        ParticipantBO found = participantDAO.findParticipant(partBO);
        Assert.assertNull(found);

        manageParticipantIdentifierWS.create(participantType);

        String messages = dnsMessageSenderService.getMessages();
        Assert.assertTrue(messages.contains("B-550a04c0fae77ed8b4217cf6c281dca2.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("B-e7c230e6b6fa3c0e8d5e226cdebe62daefdfc1702c0d3b4a93598520.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("B-550a04c0fae77ed8b4217cf6c281dca2.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tCNAME\tfoundUnsecure.publisher.acc.edelivery.tech.ec.europa.eu."));
        Assert.assertTrue(messages.contains("B-e7c230e6b6fa3c0e8d5e226cdebe62daefdfc1702c0d3b4a93598520.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tNAPTR\t100 10 \"U\" \"Meta:SMP\" \"!^.*$!http://foundUnsecure.publisher.acc.edelivery.tech.ec.europa.eu.!\" ."));

        // Finally we verify that the participant has been created
        found = participantDAO.findParticipant(partBO);
        Assert.assertNotNull(found);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantIllegalIssuingAgency() throws Exception {
        // First we ensure the participant doesn't exist
        final String partId = "0008:223456789";
        final String smpId = "foundUnsecure";
        // Then we create the participant
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setValue(partId);
        participantType.setServiceMetadataPublisherID(smpId);
        manageParticipantIdentifierWS.create(participantType);

    }

    /**
     * BusDox specification chapter 2.3
     * (https://joinup.ec.europa.eu/svn/peppol/PEPPOL_EIA/1-ICT_Architecture/1-ICT-Transport_Infrastructure/13-ICT-Models/ICT-Transport-BusDox_Definitions-101.pdf).
     * There it is stated:
     * <p/>
     * Any Scheme Identifier defined outside of this specification MUST take
     * the form <domain>-<identifierArea>-<identifier type>, such as for
     * example "busdox-actorid-upis".
     *
     * @throws Exception
     */
    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantIllegalScheme1() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setScheme("123");
        manageParticipantIdentifierWS.create(participantType);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateParticipantIllegalScheme2() throws Exception {
        ServiceMetadataPublisherServiceForParticipantType participantType = createParticipant();
        participantType.getParticipantIdentifier().setScheme("domain");
        manageParticipantIdentifierWS.create(participantType);
    }
}

