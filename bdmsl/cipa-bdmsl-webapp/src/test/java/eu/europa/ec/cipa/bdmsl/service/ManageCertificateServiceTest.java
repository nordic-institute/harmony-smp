package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.dao.ISmpDAO;
import eu.europa.ec.cipa.bdmsl.dao.IWildcardDAO;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageCertificateServiceTest extends AbstractTest {

    @Autowired
    private IManageCertificateService manageCertificateService;

    @Autowired
    private ICertificateDAO certificateDAO;

    @Autowired
    private ISmpDAO smpDAO;

    @Autowired
    private IWildcardDAO wildcardDAO;

    @Test
    public void testChangeCertificates() throws TechnicalException, BusinessException, ExecutionException, InterruptedException {
        Assert.assertEquals("CN=SMP_TEST_CHANGE_CERTIFICATE_SCHEDULER,O=DG-DIGIT,C=BE:000000000456EFGH", smpDAO.findSMP("smpForChangeCertificateTest").getCertificateId());
        CertificateBO certificateBO = new CertificateBO();
        certificateBO.setId(4l);
        Assert.assertEquals("CN=SMP_TEST_CHANGE_CERTIFICATE_SCHEDULER,O=DG-DIGIT,C=BE:000000000456EFGH", wildcardDAO.findWildcard("iso6523-actorid-upis", certificateBO).getCertificateId());
        Assert.assertNotNull(certificateDAO.findCertificateByCertificateId("CN=SMP_TEST_CHANGE_CERTIFICATE_SCHEDULER,O=DG-DIGIT,C=BE:000000000456EFGH"));
        manageCertificateService.changeCertificates();
        Assert.assertEquals("CN=test:123", smpDAO.findSMP("smpForChangeCertificateTest").getCertificateId());
        certificateBO.setId(2l);
        Assert.assertEquals("CN=test:123", wildcardDAO.findWildcard("iso6523-actorid-upis", certificateBO).getCertificateId());
        Assert.assertNull(certificateDAO.findCertificateByCertificateId("CN=SMP_TEST_CHANGE_CERTIFICATE_SCHEDULER,O=DG-DIGIT,C=BE:000000000456EFGH"));
    }

}
