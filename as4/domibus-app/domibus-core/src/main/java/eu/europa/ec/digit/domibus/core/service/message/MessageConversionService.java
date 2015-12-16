package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;

/**
 *
 * @author Vincent Dijkstra
 *
 */
public interface MessageConversionService {

    /**
     * Converts the incoming message to a {@link MessageBO} object that is used for
     * internal processing.
     * 
     * @param message incoming message
     * @return internal message object
     */
    public MessageBO convertBasic(final GatewayEnvelope message);
    
    /**
     * Converts the internal message to a {@link GatewayEnvelope} object.
     * 
     * @param messageBO an internal messageObject
     * @return original message object
     */
    public GatewayEnvelope convertBasic(final MessageBO messageBO);
    
    
}
