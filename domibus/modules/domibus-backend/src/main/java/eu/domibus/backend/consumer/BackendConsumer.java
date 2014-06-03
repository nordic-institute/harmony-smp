/*
 * 
 */
package eu.domibus.backend.consumer;

import org.apache.log4j.Logger;
import eu.domibus.backend.consumer.helper.BackendConsumerHelper;
import eu.domibus.ebms3.consumers.EbConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * The Class BackendConsumer.
 */
public class BackendConsumer extends eu.domibus.backend.spring.BackendSpringBeanAutowiringSupport
        implements EbConsumer {

    /**
     * The log.
     */
    private static final Logger log = Logger.getLogger(BackendConsumer.class);

    @Autowired
    private BackendConsumerHelper backendConsumerHelper;

    /**
     * The parameters.
     */
    protected Map<String, String> parameters;

    public BackendConsumer() {
        init();
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.domibus.ebms3.consumers.EbConsumer#setParameters(java.util.Map)
     */
    public void setParameters(final Map<String, String> properties) {
        this.parameters = properties;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * eu.domibus.ebms3.consumers.EbConsumer#push(eu.domibus.ebms3.module
     * .MsgInfo, org.apache.axis2.context.MessageContext)
     */
    public void push() {
        log.debug("Saving push message");

        init();

        backendConsumerHelper.push(parameters);

        log.debug("Saved push message");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * eu.domibus.ebms3.consumers.EbConsumer#pull(eu.domibus.ebms3.module
     * .MsgInfo, org.apache.axis2.context.MessageContext)
     */
    public void pull() {
        throw new UnsupportedOperationException();
    }
}
