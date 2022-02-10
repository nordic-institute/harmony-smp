package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.BlueCoatAuthenticationFilter;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

public class SMPSecurityPropertyUpdateListenerTest {



    BlueCoatAuthenticationFilter blueCoatAuthenticationFilter = Mockito.mock(BlueCoatAuthenticationFilter.class);
    ConfigurationDao configurationDao = Mockito.mock(ConfigurationDao.class);
    ForwardedHeaderTransformer forwardedHeaderTransformer = Mockito.mock(ForwardedHeaderTransformer.class);
    SMPSecurityPropertyUpdateListener testInstance = new SMPSecurityPropertyUpdateListener(blueCoatAuthenticationFilter,configurationDao,forwardedHeaderTransformer );

    @Test
    public void testInit() {
        testInstance.init();
        Mockito.verify(configurationDao, Mockito.times(1)).addPropertyUpdateListener(testInstance);
    }

    @Test
    public void propertiesUpdateTrue() {
        Mockito.doReturn(Boolean.TRUE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED);
        Mockito.doReturn(Boolean.TRUE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.HTTP_FORWARDED_HEADERS_ENABLED);
        testInstance.propertiesUpdate();
        Mockito.verify(blueCoatAuthenticationFilter, Mockito.times(1)).setBlueCoatEnabled(true);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(1)).setRemoveOnly(false);
    }

    @Test
    public void propertiesUpdateFalse() {
        Mockito.doReturn(Boolean.FALSE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED);
        Mockito.doReturn(Boolean.FALSE ).when(configurationDao).getCachedPropertyValue(SMPPropertyEnum.HTTP_FORWARDED_HEADERS_ENABLED);
        testInstance.propertiesUpdate();
        Mockito.verify(blueCoatAuthenticationFilter, Mockito.times(1)).setBlueCoatEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(1)).setRemoveOnly(true);
    }
}