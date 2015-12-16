package eu.europa.ec.digit.domibus.core.service.message;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.digit.domibus.core.mapper.basic.BasicMessageMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;

@Service
public class MessageConversionServiceImpl implements MessageConversionService {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
    
    @Autowired
    private BasicMessageMapper basicMessageMapper = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
    
	@Override
	public MessageBO convertBasic(GatewayEnvelope envelope) {
		return basicMessageMapper.mapTo(envelope);
	}

	@Override
	public GatewayEnvelope convertBasic(MessageBO messageBO) {
		return basicMessageMapper.mapFrom(messageBO);
	}

    /* ---- Getters and Setters ---- */

	public BasicMessageMapper getBasicMessageMapper() {
		return basicMessageMapper;
	}

	public void setBasicMessageMapper(BasicMessageMapper basicMessageMapper) {
		this.basicMessageMapper = basicMessageMapper;
	}

}
