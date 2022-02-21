package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.BlueCoatAuthenticationFilter;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import javax.annotation.PostConstruct;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.SMP_FORWARDED_HEADER_TRANSFORMER_BEAN;


/**
 * Class update security configuration on property update event
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Component
public class SMPSecurityPropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPSecurityPropertyUpdateListener.class);

    final BlueCoatAuthenticationFilter blueCoatAuthenticationFilter;
    final ConfigurationDao configurationDao;
    final ForwardedHeaderTransformer forwardedHeaderTransformer;

    public SMPSecurityPropertyUpdateListener(BlueCoatAuthenticationFilter blueCoatAuthenticationFilter,
                                             ConfigurationDao configurationDao,
                                             ForwardedHeaderTransformer forwardedHeaderTransformer) {
        this.blueCoatAuthenticationFilter = blueCoatAuthenticationFilter;
        this.configurationDao = configurationDao;
        this.forwardedHeaderTransformer = forwardedHeaderTransformer;
    }

    @PostConstruct
    public void init() {
        configurationDao.addPropertyUpdateListener(this);
    }

    @Override
    public void propertiesUpdate() {
        boolean setBlueCoatEnabled = BooleanUtils.toBoolean((Boolean) configurationDao.getCachedPropertyValue(SMPPropertyEnum.BLUE_COAT_ENABLED));
        boolean setForwardHeadersEnabled = BooleanUtils.toBoolean((Boolean) configurationDao.getCachedPropertyValue(SMPPropertyEnum.HTTP_FORWARDED_HEADERS_ENABLED));

        if (setBlueCoatEnabled) {
            LOG.warn("Set blue coat enabled: [true]. Do not enable this option when using SMP without reverse-proxy and HTTP header protection!");
        }
        blueCoatAuthenticationFilter.setBlueCoatEnabled(setBlueCoatEnabled);

        LOG.info("Set http forward headers  enabled: [{}]." + setForwardHeadersEnabled);
        if (setForwardHeadersEnabled) {
            LOG.warn("Set http forward headers  enabled:: [true]. Do not enable this option when using SMP without reverse-proxy and HTTP header protection!");
        }
        forwardedHeaderTransformer.setRemoveOnly(!setForwardHeadersEnabled);
    }
}
