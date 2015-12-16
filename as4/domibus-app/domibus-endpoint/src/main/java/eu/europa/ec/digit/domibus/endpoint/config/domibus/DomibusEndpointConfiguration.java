package eu.europa.ec.digit.domibus.endpoint.config.domibus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.europa.ec.digit.domibus.endpoint.config.EndpointConfiguration;
import eu.europa.ec.digit.domibus.endpoint.config.JMSConfiguration;
import eu.europa.ec.digit.domibus.facade.config.domibus.DomibusFacadeConfiguration;


@Configuration
@Import({
    JMSConfiguration.class,
    DomibusFacadeConfiguration.class
})
@ComponentScan(basePackages = {
    "eu.europa.ec.digit.domibus.endpoint.ws",
    "eu.europa.ec.digit.domibus.endpoint.interceptor",
    "eu.europa.ec.digit.domibus.endpoint.listener.domibus",
    "eu.europa.ec.digit.domibus.endpoint.handler.domibus"
})
public class DomibusEndpointConfiguration extends EndpointConfiguration {

    /* ---- Constants ---- */
    private final Log log = LogFactory.getLog(getClass());

    /* --- Instance Variables ---- */

    /* ---- Constructors ---- */

    public DomibusEndpointConfiguration() {
        super();
        log.info("DomibusEndpointConfiguration - domibus-endpoint");
    }

    /* ---- Configuration Beans ---- */

    /* ---- Business Methods ---- */


}
