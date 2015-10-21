package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.dao.ISmpDAO;
import eu.europa.ec.cipa.bdmsl.mock.DnsMessageSenderServiceMock;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xbill.DNS.Message;

import java.security.Security;
import java.util.List;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageServiceMetadataWSTest extends AbstractTest {

    @Autowired
    private IManageServiceMetadataWS manageServiceMetadataWS;

    @Autowired
    private ICertificateDAO certificateDAO;

    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

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

    @Test
    public void testRead() throws Exception {
        ServiceMetadataPublisherServiceType input = new ServiceMetadataPublisherServiceType();
        input.setServiceMetadataPublisherID("foundUnsecure");
        ServiceMetadataPublisherServiceType result = manageServiceMetadataWS.read(input);
        Assert.assertEquals(input.getServiceMetadataPublisherID(), result.getServiceMetadataPublisherID());
        Assert.assertNotNull(result.getPublisherEndpoint());
    }

    @Test(expected = BadRequestFault.class)
    public void testReadNull() throws Exception {
        manageServiceMetadataWS.read(null);
    }

    @Test(expected = BadRequestFault.class)
    public void testReadEmpty() throws Exception {
        manageServiceMetadataWS.read(new ServiceMetadataPublisherServiceType());
    }

    @Test(expected = NotFoundFault.class)
    public void testReadNotFound() throws Exception {
        ServiceMetadataPublisherServiceType smp = new ServiceMetadataPublisherServiceType();
        smp.setServiceMetadataPublisherID("SmpNotFoundException");
        manageServiceMetadataWS.read(smp);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateNull() throws Exception {
        manageServiceMetadataWS.create(null);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateEmpty() throws Exception {
        manageServiceMetadataWS.create(new ServiceMetadataPublisherServiceType());
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateBadPhysicalAddress() throws Exception {
        ServiceMetadataPublisherServiceType smp = new ServiceMetadataPublisherServiceType();
        PublisherEndpointType publisherEndpointType = new PublisherEndpointType();
        publisherEndpointType.setLogicalAddress("http://logical");
        publisherEndpointType.setPhysicalAddress("PhysicalWrong");
        smp.setServiceMetadataPublisherID("smpId");
        smp.setPublisherEndpoint(publisherEndpointType);
        manageServiceMetadataWS.create(smp);
    }

    @Test(expected = BadRequestFault.class)
    public void testCreateAlreadyExist() throws Exception {
        ServiceMetadataPublisherServiceType smp = createSimpleSMP("found");
        manageServiceMetadataWS.create(smp);
    }


    /**
     * Create a SMP that doesn't exist with an unsecure certificate.
     *
     * @throws Exception
     */
    @Test
    public void testCreateOkNotAlreadyExist() throws Exception {
        // first we ensure that the data don't exist
        String smpId = "notExist";
        ServiceMetadataPublisherServiceType smpToBeCreated = new ServiceMetadataPublisherServiceType();
        smpToBeCreated.setServiceMetadataPublisherID(smpId);

        DnsMessageSenderServiceMock dnsMessageSenderService = (DnsMessageSenderServiceMock) this.dnsMessageSenderService;
        Assert.assertTrue(dnsMessageSenderService.getMessages().isEmpty());

        ServiceMetadataPublisherServiceType smp = createSimpleSMP(smpId);
        manageServiceMetadataWS.create(smp);

        String messages = dnsMessageSenderService.getMessages();
        Assert.assertTrue(messages.contains("notExist.publisher.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("notExist.publisher.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tCNAME\tlogical."));

        // Then we check that the data have been created
        Assert.assertNotNull(manageServiceMetadataWS.read(smpToBeCreated));
        Assert.assertNotNull(certificateDAO.findCertificateByCertificateId(UnsecureAuthentication.UNSECURE_HTTP_CLIENT));
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteNotFound() throws Exception {
        manageServiceMetadataWS.delete("Not found");
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteNull() throws Exception {
        manageServiceMetadataWS.delete(null);
    }

    @Test(expected = BadRequestFault.class)
    public void testDeleteEmpty() throws Exception {
        manageServiceMetadataWS.delete("");
    }

    @Test
    public void testDeleteOk() throws Exception {
        ServiceMetadataPublisherServiceType smpToBeDeleted = new ServiceMetadataPublisherServiceType();
        smpToBeDeleted.setServiceMetadataPublisherID("toBeDeleted");
        Assert.assertNotNull(manageServiceMetadataWS.read(smpToBeDeleted));

        PageRequestType pageRequestType = new PageRequestType();
        pageRequestType.setServiceMetadataPublisherID("toBeDeleted");
        Assert.assertEquals(1, manageParticipantIdentifierWS.list(pageRequestType).getParticipantIdentifier().size());

        DnsMessageSenderServiceMock dnsMessageSenderService = (DnsMessageSenderServiceMock) this.dnsMessageSenderService;
        Assert.assertTrue(dnsMessageSenderService.getMessages().isEmpty());

        // this SMP is linked to one participant. The participant must also be deleted
        manageServiceMetadataWS.delete("toBeDeleted");

        String messages = dnsMessageSenderService.getMessages();
        Assert.assertTrue(messages.contains("B-0d8a3142047e1f07f0b56ff4ab3c5c8a.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("B-ad2ad556658228598f7abcd6334c22a4139e98f042f708196469bbe3.iso6523-actorid-upis.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        Assert.assertTrue(messages.contains("toBeDeleted.publisher.acc.edelivery.tech.ec.europa.eu.\t0\tANY\tANY"));
        try {
            manageServiceMetadataWS.read(smpToBeDeleted);
            Assert.fail("A SmpNotFoundException should have been raised");
        } catch (NotFoundFault fault) {
            // ok, expected
        }
        try {
            manageParticipantIdentifierWS.list(pageRequestType);
            Assert.fail("A SmpNotFoundException should have been raised");
        } catch (NotFoundFault fault) {
            // ok, expected
        }
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteUnauthorized() throws Exception {
        manageServiceMetadataWS.delete("found");
    }

    @Test(expected = UnauthorizedFault.class)
     public void testUpdateUnauthorized() throws Exception {
        String smpId = "found";
        ServiceMetadataPublisherServiceType smpToBeUpdated = createSimpleSMP(smpId);
        smpToBeUpdated.setServiceMetadataPublisherID(smpId);
        manageServiceMetadataWS.update(smpToBeUpdated);
    }

    @Test
    public void testUpdateOk() throws Exception {
        String smpId = "toBeUpdated";
        ServiceMetadataPublisherServiceType smpToBeUpdated = createSimpleSMP(smpId);

        ServiceMetadataPublisherServiceType found = manageServiceMetadataWS.read(smpToBeUpdated);
        Assert.assertEquals("10.10.10.10", found.getPublisherEndpoint().getPhysicalAddress());
        Assert.assertEquals("http://logicalAddress", found.getPublisherEndpoint().getLogicalAddress());

        smpToBeUpdated.getPublisherEndpoint().setPhysicalAddress("50.50.50.50");
        smpToBeUpdated.getPublisherEndpoint().setLogicalAddress("http://logicalAddressUpdated");

        DnsMessageSenderServiceMock dnsMessageSenderService = (DnsMessageSenderServiceMock) this.dnsMessageSenderService;
        Assert.assertTrue(dnsMessageSenderService.getMessages().isEmpty());

        smpToBeUpdated.setServiceMetadataPublisherID(smpId);
        manageServiceMetadataWS.update(smpToBeUpdated);

        String messages = dnsMessageSenderService.getMessages();
        Assert.assertTrue(messages.contains("toBeUpdated.publisher.acc.edelivery.tech.ec.europa.eu.\t60\tIN\tCNAME\tlogicalAddressUpdated."));

        found = manageServiceMetadataWS.read(smpToBeUpdated);
        Assert.assertEquals("50.50.50.50", found.getPublisherEndpoint().getPhysicalAddress());
        Assert.assertEquals("http://logicalAddressUpdated", found.getPublisherEndpoint().getLogicalAddress());
    }

    @Test(expected = NotFoundFault.class)
    public void testUpdateNotFound() throws Exception {
        String smpId = "Notfound";
        ServiceMetadataPublisherServiceType smpToBeUpdated = createSimpleSMP(smpId);
        manageServiceMetadataWS.update(smpToBeUpdated);
    }

    private ServiceMetadataPublisherServiceType createSimpleSMP(String smpId) {
        ServiceMetadataPublisherServiceType smpToBeUpdated = new ServiceMetadataPublisherServiceType();
        PublisherEndpointType publisherEndpointType = new PublisherEndpointType();
        publisherEndpointType.setLogicalAddress("http://logical");
        publisherEndpointType.setPhysicalAddress("10.10.10.10");
        smpToBeUpdated.setPublisherEndpoint(publisherEndpointType);
        smpToBeUpdated.setServiceMetadataPublisherID(smpId);
        return smpToBeUpdated;
    }

    @Test(expected = BadRequestFault.class)
    public void testUpdateNull() throws Exception {
        manageServiceMetadataWS.update(null);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testDeleteWithMigrationPlanned() throws Exception {
        String smpId = "toBeDeletedWithMigrationPlanned";
        manageServiceMetadataWS.delete(smpId);
    }

    @Test(expected = BadRequestFault.class)
    public void testUpdateEmpty() throws Exception {
        String smpId = "";
        ServiceMetadataPublisherServiceType smpToBeUpdated = new ServiceMetadataPublisherServiceType();
        smpToBeUpdated.setServiceMetadataPublisherID(smpId);
        manageServiceMetadataWS.update(smpToBeUpdated);
    }

}

