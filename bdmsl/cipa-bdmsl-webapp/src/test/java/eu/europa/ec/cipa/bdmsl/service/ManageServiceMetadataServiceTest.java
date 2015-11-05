package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.common.exception.BusinessException;
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
 * Created by feriaad on 16/06/2015.
 */
public class ManageServiceMetadataServiceTest extends AbstractTest {

    @Autowired
    private IManageServiceMetadataService manageServiceMetadataService;

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
    public void testRead() throws TechnicalException, BusinessException {
        String id = "foundUnsecure";
        ServiceMetadataPublisherBO smpResultBo = manageServiceMetadataService.read(id);
        Assert.assertEquals(id, smpResultBo.getSmpId());
    }

    @Test(expected = BadRequestException.class)
    public void testReadNull() throws TechnicalException, BusinessException {
        manageServiceMetadataService.read(null);
    }

    @Test(expected = BadRequestException.class)
    public void testReadEmpty() throws TechnicalException, BusinessException {
        manageServiceMetadataService.read("");
    }
}
