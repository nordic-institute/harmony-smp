package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.AbstractBaseDao;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {AbstractServiceTest.DomiSMPServicesConfig.class})
public abstract class AbstractServiceTest extends AbstractBaseDao {
    @Configuration
    @ComponentScan({"eu.europa.ec.edelivery.smp"})
    public static class DomiSMPServicesConfig {

    }
}
