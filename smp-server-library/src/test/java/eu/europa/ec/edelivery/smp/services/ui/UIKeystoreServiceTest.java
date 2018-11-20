package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import static org.junit.Assert.*;


@ContextConfiguration(classes = {UIKeystoreService.class, ConversionTestConfig.class, PropertiesSingleDomainTestConfig.class})
public class UIKeystoreServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    protected UIKeystoreService testInstance;

    @Test
    public void testGetKeystoreEntriesList()  {
        List<CertificateRO> lst = testInstance.getKeystoreEntriesList();
        assertEquals(1, lst.size());
    }


}