package eu.europa.ec.digit.domibus.common.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import eu.domibus.ebms3.common.CompressionMimeTypeBlacklist;
import eu.domibus.ebms3.common.MessageIdGenerator;
import eu.domibus.ebms3.common.TimestampDateFormatter;

@Import({
    JMSConfiguration.class
})
@PropertySource({
    "classpath:/domibus-jms.properties"
})
public abstract class CommonConfiguration {

    /* ---- Constants ---- */
    public final Logger log = Logger.getLogger(getClass());
    public final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String MESSAGE_PROTOCOL = "SOAP 1.2 Protocol";
    public final static String JAXB_CONFIG_CONTEXT = "eu.domibus.common.configuration.model";
    public final static String JAXB_EBMS_CONTEXT = "eu.domibus.common.model.org.oasis_open.docs.ebxml_bp.ebbp_signals_2_0:eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704:eu.domibus.common.model.org.w3._2003._05.soap_envelope:eu.domibus.common.model.org.w3._2005._05.xmlmime:eu.domibus.common.model.org.xmlsoap.schemas.soap.envelope";
    public final static String JAXB_MESSAGE_CONTEXT = "eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704";

    /* ---- Instance Variables ---- */

    @Autowired
    private Environment environment = null;

    /* ---- Constructors ---- */

    public CommonConfiguration() {
        super();
        log.info("DomibusCommonConfiguration - domibus-common");
    }

    /* ---- Configuration Beans ---- */

    @Bean (name = "compressionMimeTypeBlacklist")
    public CompressionMimeTypeBlacklist compressionMimeTypeBlackList() {
    	CompressionMimeTypeBlacklist blackList = new CompressionMimeTypeBlacklist();
    	List<String> entries = new ArrayList<>();
    	entries.add("application/vnd.etsi.asic-s+zip");
    	entries.add("image/jpeg");
    	blackList.setEntries(entries);
    	return blackList;
    }

    @Bean (name = "domibusProperties")
    public Properties domibusProperties() {
    	Properties properties = new Properties();
        properties.put("domibus.msh.messageid.suffix", "e-delivery.eu");
        // Sender Worker execution interval as a cron expression
        properties.put("domibus.msh.sender.cron", "0/5 * * * * ?");
        // Should unrecoverable errors should be retried or not
        properties.put("domibus.dispatch.ebms.error.unrecoverable.retry",  "true");
    	return properties;
    }

    @Bean (name = "messageIdGenerator")
    public MessageIdGenerator messageIdGenerator() {
    	MessageIdGenerator messageIdGenerator = new MessageIdGenerator();
    	messageIdGenerator.setMessageIdSuffix(domibusProperties().getProperty("domibus.msh.messageid.suffix"));
    	return messageIdGenerator;
    }

	@Bean (name = "jaxbContextMessagingOnly")
	public JAXBContext jaxbMessagingContext() throws JAXBException {
		return JAXBContext.newInstance(JAXB_MESSAGE_CONTEXT);
	}

	/**
	 * JAXB Context for mapping of ebMS3 schema.
	 * @return a JAXB context
	 */
	@Bean (name = "jaxbContextEBMS")
	public JAXBContext jaxbContextEBMS() throws JAXBException {
		return JAXBContext.newInstance(JAXB_EBMS_CONTEXT);
	}

	/**
	 * JAXB context for mapping of pMode XML configuration.
	 * @return a JAXB context
	 */
	@Bean (name = "jaxbContextConfig")
	public JAXBContext jaxbContext() throws JAXBException {
		return JAXBContext.newInstance(JAXB_CONFIG_CONTEXT);
	}

	@Bean (name = "documentBuilderFactory")
	@Scope (value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DocumentBuilderFactory documentBuilderFactory() {
		return DocumentBuilderFactory.newInstance();
	}

	@Bean (name = "transformerFactory")
	public TransformerFactory transformerFactory() {
		return TransformerFactory.newInstance();
	}

    /**
     * DateTimeFormat, to be used within the ebXML messages.
     * @return
     */
    @Bean (name = "xmlDateTimeFormat")
    public SimpleDateFormat simpleDateFormat() {
    	return new SimpleDateFormat(DATE_FORMAT_PATTERN);
    }

    @Bean (name = "dateFormatter")
    public TimestampDateFormatter timestampDateFormatter() {
    	return new TimestampDateFormatter();
    }

    @Bean (name = "messageFactory")
    public MessageFactory messageFactory() throws SOAPException {
    	MessageFactory factory = MessageFactory.newInstance(MESSAGE_PROTOCOL);
    	return factory;
    }



    /* ---- Getters and Setters ---- */
}
