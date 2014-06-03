package eu.domibus.ebms3.consumers;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * This Consumer supports the EBMS3 Ping feature.
 * It consumes and optionally logs any message.
 * It is required to configure consumptions and PModes to use this consumer:
 * <ul>
 * <li>Service = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/service</li>
 * <li>Action = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/test</li>
 * </ul>
 *
 * @author Thorsten Niedzwetzki
 */
public class PingConsumer implements EbConsumer {
    private static final Logger log = Logger.getLogger(PingConsumer.class);

    private Map<String, String> parameters;

    /**
     * Boolean consumption parameter: Log push messages
     */
    private static final String PARAMETER_LOG_PUSH = "log.push";

    /**
     * Boolean consumption parameter: Log pull messages
     */
    private static final String PARAMETER_LOG_PULL = "log.pull";

    /**
     * Handle push messages.
     */
    @Override
    public void push() {
        if (isConfigured(PARAMETER_LOG_PUSH)) {
            log.info("Push");
        }
    }

    /**
     * Handle pull messages.
     */
    @Override
    public void pull() {
        if (isConfigured(PARAMETER_LOG_PULL)) {
            log.info("Pull");
        }
    }

    /**
     * Take consumption parameters.
     */
    @Override
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Check whether a consumption parameter is set to {@code true}.
     * Returns {@code false} if it is set to {@code false} or if it is not set at all.
     *
     * @param parameterName name of the consumption parameter to evaluate
     * @return {@code true} if the {@code parameterName} is set to {@code true}
     */
    private boolean isConfigured(final String parameterName) {
        return parameters != null && String.valueOf(true).equals(parameters.get(parameterName));
    }

}
