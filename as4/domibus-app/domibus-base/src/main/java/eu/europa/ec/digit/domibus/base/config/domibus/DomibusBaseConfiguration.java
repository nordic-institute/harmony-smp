package eu.europa.ec.digit.domibus.base.config.domibus;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.domibus.ebms3.common.dao.PModeDao;
import eu.europa.ec.digit.domibus.base.config.BaseConfiguration;
import eu.europa.ec.digit.domibus.base.policy.NotificationEndpointPolicy;
import eu.europa.ec.digit.domibus.base.policy.domibus.BasicNotificationEndpointPolicy;
import eu.europa.ec.digit.domibus.common.log.Logger;


/**
 * @author Vincent Dijkstra
 */
@Configuration
@ComponentScan({
    "eu.europa.ec.digit.domibus.base.config",
    "eu.europa.ec.digit.domibus.base.policy",
    "eu.europa.ec.digit.domibus.base.dao",
    "eu.domibus.common.dao"
})
@EnableTransactionManagement
public class DomibusBaseConfiguration extends BaseConfiguration {

    /* ---- Constants ---- */
    private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */
    
    @Autowired
    private BasicNotificationEndpointPolicy basicNotificationEndpointPolicy = null;

    /* ---- Constructors ---- */

    public DomibusBaseConfiguration() {
        super();
        log.debug("DomibusBaseConfiguration - domibus-base");
    }

    /* ---- Configuration Beans ---- */
    
    @Bean (name = "notificationEndpointSelectionPolicy")
    public Map<String, NotificationEndpointPolicy<?, ?>> notificationEndpointSelectionPolicy() {
    	Map<String, NotificationEndpointPolicy<?, ?>> map = new HashMap<>();
    	map.put("basic", basicNotificationEndpointPolicy); 	
    	return map;
    }
    
    @Bean (name = "pModeProvider")
    public PModeDao pModeProvider() {
    	PModeDao pmode =  new PModeDao();
    	pmode.init();
    	return pmode;
    }
    
    /* ---- Getters and Setters ---- */

	public BasicNotificationEndpointPolicy getBasicNotificationEndpointPolicy() {
		return basicNotificationEndpointPolicy;
	}

	public void setBasicNotificationEndpointPolicy(BasicNotificationEndpointPolicy basicNotificationEndpointPolicy) {
		this.basicNotificationEndpointPolicy = basicNotificationEndpointPolicy;
	}
}
