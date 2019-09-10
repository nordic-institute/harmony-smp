package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.BlueCoatAuthenticationFilter;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class SMPPropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPPropertyUpdateListener.class);

    @Autowired
    BlueCoatAuthenticationFilter blueCoatAuthenticationFilter;

    @Autowired
    ConfigurationDao configurationDao;


    @PostConstruct
    public void init() {
        configurationDao.addPropertyUpdateListener(this);
    }

    @Override
    public void propertiesUpdate() {
        Boolean bcv = (Boolean) configurationDao.getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED);
        boolean setBlueCoatEnabled = bcv != null && bcv;
        LOG.info("Set blue coat enabled: " + Boolean.toString(setBlueCoatEnabled));
        blueCoatAuthenticationFilter.setBlueCoatEnabled(setBlueCoatEnabled);
    }
}
