package eu.europa.ec.cipa.bdmsl.ws.mapping;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import ma.glasnost.orika.MapperFactory;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by feriaad on 16/06/2015.
 */
public class SoapMappingInitializerTest extends AbstractTest {

    @Autowired
    private MapperFactory mapperFactory;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testSmpBoToSmpWs() {
        ServiceMetadataPublisherServiceType input = new ServiceMetadataPublisherServiceType();
        PublisherEndpointType publisherEndpointType = new PublisherEndpointType();
        publisherEndpointType.setLogicalAddress("logicalAddress");
        publisherEndpointType.setPhysicalAddress("physicalAddress");
        input.setPublisherEndpoint(publisherEndpointType);
        input.setServiceMetadataPublisherID("123");

        ServiceMetadataPublisherBO expected = new ServiceMetadataPublisherBO();
        expected.setSmpId("123");
        expected.setLogicalAddress("logicalAddress");
        expected.setPhysicalAddress("physicalAddress");
        expected.setCertificateId("unsecure-http-client");

        ServiceMetadataPublisherBO resultSmpBo =  mapperFactory.getMapperFacade().map(input, ServiceMetadataPublisherBO.class);

        Assert.assertEquals(expected, resultSmpBo);
    }
}
