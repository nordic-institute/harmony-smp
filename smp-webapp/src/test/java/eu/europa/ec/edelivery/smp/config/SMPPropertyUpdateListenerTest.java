package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.BlueCoatAuthenticationFilter;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.Assert.*;

public class SMPPropertyUpdateListenerTest {

    SMPPropertyUpdateListener testInstance = new SMPPropertyUpdateListener();

    BlueCoatAuthenticationFilter blueCoatAuthenticationFilter = Mockito.mock(BlueCoatAuthenticationFilter.class);
    ConfigurationDao configurationDao = Mockito.mock(ConfigurationDao.class);


    @Before
    public void before() throws IOException {
        ReflectionTestUtils.setField(testInstance,"blueCoatAuthenticationFilter",blueCoatAuthenticationFilter);
        ReflectionTestUtils.setField(testInstance,"configurationDao",configurationDao);
    }

    @Test
    public void testInit() {
        testInstance.init();
        Mockito.verify(configurationDao, Mockito.times(1)).addPropertyUpdateListener(testInstance);
    }

    @Test
    public void propertiesUpdateTrue() {
        Mockito.doReturn(Boolean.TRUE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED);
        testInstance.propertiesUpdate();
        Mockito.verify(blueCoatAuthenticationFilter, Mockito.times(1)).setBlueCoatEnabled(true);
    }

    @Test
    public void propertiesUpdateFalse() {
        Mockito.doReturn(Boolean.FALSE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED);
        testInstance.propertiesUpdate();
        Mockito.verify(blueCoatAuthenticationFilter, Mockito.times(1)).setBlueCoatEnabled(false);
    }
}