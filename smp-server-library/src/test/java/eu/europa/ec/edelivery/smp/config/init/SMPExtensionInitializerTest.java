package eu.europa.ec.edelivery.smp.config.init;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ExtensionDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ContextConfiguration( classes = {SMPExtensionInitializerTest.OasisSMPExtensionConfig.class}
)
public class SMPExtensionInitializerTest extends AbstractJunit5BaseDao {
    @Configuration
    @ComponentScan({"eu.europa.ec.smp.spi"})
    public static class OasisSMPExtensionConfig {

    }

    @Autowired
    SMPExtensionInitializer testInstance;

    @Autowired
    ExtensionDao extensionDao;

    @Test
    @Transactional
    public void testValidateExtensionData() {
        int extensionCount = extensionDao.getAllExtensions().size();
        testInstance.validateExtensionData();
        // added OasisSMP extension
        assertEquals(extensionCount + 1, extensionDao.getAllExtensions().size());
    }
}
